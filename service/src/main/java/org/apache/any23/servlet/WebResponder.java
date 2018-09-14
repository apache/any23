/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.any23.Any23;
import org.apache.any23.ExtractionReport;
import org.apache.any23.configuration.Settings;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.filter.IgnoreAccidentalRDFa;
import org.apache.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.validator.SerializationException;
import org.apache.any23.validator.XMLValidationReportSerializer;
import org.apache.any23.writer.CompositeTripleHandler;
import org.apache.any23.writer.CountingTripleHandler;
import org.apache.any23.writer.FormatWriter;
import org.apache.any23.writer.TripleWriterFactory;
import org.apache.any23.writer.ReportingTripleHandler;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.any23.writer.WriterFactory;
import org.apache.any23.writer.WriterFactoryRegistry;

/**
 * This class is responsible for building the {@link Servlet}
 * web response.
 */
class WebResponder {

    private static final WriterFactoryRegistry writerRegistry = WriterFactoryRegistry.getInstance();

    /**
     * Library facade.
     */
    private final Any23 runner;

    /**
     * Servlet for which building the response.
     */
    private Servlet any23servlet;

    /**
     * Servlet response object.
     */
    private HttpServletResponse response;

    /**
     * RDF triple writer.
     */
    private TripleHandler rdfWriter = null;

    /**
     * Error and statistics reporter.
     */
    private ReportingTripleHandler reporter = null;

    /**
     * Type of expected output.
     */
    private String outputMediaType = null;

    /**
     * The output stream.
     */
    private ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

    public WebResponder(Servlet any23servlet, HttpServletResponse response) {
        this.any23servlet = any23servlet;
        this.response = response;
        this.runner = new Any23();
        runner.setHTTPUserAgent("Apache Any23 Servlet http://any23.org/");
    }

    protected Any23 getRunner() {
        return runner;
    }

    public void runExtraction(
            DocumentSource in,
            ExtractionParameters eps,
            String format,
            boolean report, boolean annotate
    ) throws IOException {
        if (in == null)
          return;
        if (!initRdfWriter(format, report, annotate))
          return;
        ExtractionReport er = null;
        try {
            er = runner.extract(eps, in, rdfWriter);
            rdfWriter.close();
            if (! er.hasMatchingExtractors() ) {
                sendError(
                        415,
                        "No suitable extractor found for this media type",
                        null,
                        er,
                        report
                );
                return;
            }
        } catch (IOException ioe) {
            // IO Error.
            if (ioe.getCause() instanceof CertificateException) {
                final String errMsg = "Could not fetch input, IO Error.";
                any23servlet.log(errMsg, ioe.getCause());
                sendError(502, errMsg, ioe, null, report);
                return;
            }
            any23servlet.log("Could not fetch input", ioe);
            sendError(502, "Could not fetch input.", ioe, null, report);
            return;
        } catch (ExtractionException e) {
            if (rdfWriter != null) {
                try {
                    rdfWriter.close();
                } catch (TripleHandlerException the) {
                    throw new RuntimeException("Error while closing TripleHandler", the);
                }
            }

            // Extraction error. Although there is a critical error we still wish 
            // to return accurate, partial extraction results to the user
            String extractionError = "Failed to fully parse input. The extraction result, at the bottom "
                    + "of this response, if any, will contain extractions only up until the extraction error.";
            any23servlet.log(extractionError, e);
            sendError(502, extractionError, e, er, report);
            return;
        } catch (Exception e) {
            any23servlet.log("Internal error", e);
            sendError(500, "Internal error.", e, null, report);
            return;
        }

        /* *** No triples found. *** */
        any23servlet.log("Extraction complete, " + reporter.getTotalTriples() + " triples");

        // Regular response.
        response.setContentType(outputMediaType);
        response.setStatus(200);
        // Set the output encoding equals to the input one.
        final String charsetEncoding = er.getEncoding();
        if (Charset.isSupported(charsetEncoding)) {
            response.setCharacterEncoding(er.getEncoding());
        } else {
            response.setCharacterEncoding("UTF-8");
        }

        final ServletOutputStream sos = response.getOutputStream();
        final byte[] data = byteOutStream.toByteArray();
        if(report) {
            final PrintStream ps = new PrintStream(sos);
            try {
                printHeader(ps);
                printResponse(reporter, er, data, ps);
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while serializing the output response.", e);
            } finally {
                ps.close();
            }
        } else {
            sos.write(data);
        }
    }

    public void sendError(int code, String msg, boolean report) throws IOException {
        sendError(code, msg, null, null, report);
    }
    
    private void printHeader(PrintStream ps) {
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    }

    private void printResponse(ReportingTripleHandler rth, ExtractionReport er, byte[] data, PrintStream ps) {
        ps.println("<response>");
        printExtractors(rth, ps);
        printReport(null, null, er, ps);
        printData(data, ps);
        ps.println("</response>");
    }

    private void printExtractors(ReportingTripleHandler rth, PrintStream ps) {
        ps.println("<extractors>");
        for (String extractor : rth.getExtractorNames()) {
            ps.print("<extractor>");
            ps.print(extractor);
            ps.println("</extractor>");
        }
        ps.println("</extractors>");
    }
    
    private void printIssueReport(ExtractionReport er, PrintStream ps) {
        ps.println("<issueReport>");
        for(Extractor<?> extractor : er.getMatchingExtractors()) {
            final String name = extractor.getDescription().getExtractorName();
            final Collection<IssueReport.Issue> extractorIssues = er.getExtractorIssues(name);
            if(extractorIssues.isEmpty())
                continue;
            ps.println( String.format("<extractorIssues extractor=\"%s\">", name));
            for(IssueReport.Issue issue : er.getExtractorIssues(name)) {
                ps.println(
                        String.format(
                                "<issue level=\"%s\" row=\"%d\" col=\"%d\">%s</issue>",
                                issue.getLevel().toString(),
                                issue.getRow(),
                                issue.getCol(),
                                issue.getMessage()
                        )
                );
            }
            ps.println("</extractorIssues>");
        }
        ps.println("</issueReport>");

    }

    private void printReport(String msg, Throwable e, ExtractionReport er, PrintStream ps) {
        XMLValidationReportSerializer reportSerializer = new XMLValidationReportSerializer();
        ps.println("<report>");

        // Human readable error message.
        if(msg != null) {
            ps.printf("<message>%s</message>%n", msg);
        } else {
            ps.print("<message/>\n");
        }

        // Error stack trace.
        if(e != null) {
            ps.println("<error>");
            ps.println("<![CDATA[");
            e.printStackTrace(ps);
            ps.println("]]>");
            ps.println("</error>");
        } else {
            ps.println("<error/>");
        }

        // Issue Report.
        printIssueReport(er, ps);

        // Validation report.
        try {
            reportSerializer.serialize(er.getValidationReport(), ps);
        } catch (SerializationException se) {
            ps.println("An error occurred while serializing error.");
            se.printStackTrace(ps);
        }
        ps.println("</report>");
    }

    private void printData(byte[] data, PrintStream ps) {
        ps.println("<data>");
        ps.println("<![CDATA[");
        try {
            ps.write(data);
        } catch (IOException ioe) {
            ps.println("An error occurred while serializing data.");
            ioe.printStackTrace(ps);
        }
        ps.println("]]>");
        ps.println("</data>");
    }

    private void sendError(int code, String msg, Exception e, ExtractionReport er, boolean report)
    throws IOException {
        response.setStatus(code);
        response.setContentType("text/plain");
        final ServletOutputStream sos = response.getOutputStream();
        final PrintStream ps = new PrintStream(sos);
        final byte[] data = byteOutStream.toByteArray();
        if (report) {
            try {
                printHeader(ps);
                printReport(msg, e, er, ps);
            } finally {
                ps.close();
            }
        } else {
            ps.println(msg);
            if (e != null) {
                ps.println("================================================================");
                e.printStackTrace(ps);
                ps.println("================================================================");
                printData(data, ps);
            }
        }
    }

    private boolean initRdfWriter(String format, boolean report, boolean annotate) throws IOException {
        final WriterFactory factory = getFormatWriter(format);
        if (!(factory instanceof TripleWriterFactory)) {
            sendError(
                    400,
                    "Invalid format '" + format + "', try one of: "
                            + writerRegistry.getWriters().stream()
                            .filter(f -> f instanceof TripleWriterFactory)
                            .map(WriterFactory::getIdentifier).collect(Collectors.toList()),
                    null,
                    null,
                    report
            );
            return false;
        }
        TripleHandler fw = ((TripleWriterFactory) factory).getTripleWriter(byteOutStream, Settings.of());
        if (fw instanceof FormatWriter) {
            ((FormatWriter)fw).setAnnotated(annotate);
        }
        outputMediaType = ((TripleWriterFactory) factory).getTripleFormat().getMimeType();
        List<TripleHandler> tripleHandlers = new ArrayList<>();
        tripleHandlers.add(new IgnoreAccidentalRDFa(fw));
        tripleHandlers.add(new CountingTripleHandler());
        rdfWriter = new CompositeTripleHandler(tripleHandlers);
        reporter = new ReportingTripleHandler(rdfWriter);
        rdfWriter = new IgnoreAccidentalRDFa(
            new IgnoreTitlesOfEmptyDocuments(reporter),
            true    // suppress stylesheet triples.
        );
        return true;
    }

    private WriterFactory getFormatWriter(String format) throws IOException {
        final String finalFormat;
        // FIXME: Remove this hardcoded set
        if ("rdf".equals(format) || "xml".equals(format) || "rdfxml".equals(format)) {
            finalFormat = "rdfxml";
        } else if ("turtle".equals(format) || "ttl".equals(format)) {
            finalFormat = "turtle";
        } else if ("n3".equals(format)) {
            finalFormat = "turtle";
        } else if ("n-triples".equals(format) || "ntriples".equals(format) || "nt".equals(format)) {
            finalFormat = "ntriples";
        } else if("nquads".equals(format) || "n-quads".equals(format) || "nq".equals(format)) {
            finalFormat = "nquads";
        } else if("trix".equals(format)) {
            finalFormat = "trix";
        } else if("json".equals(format)) {
            finalFormat = "json";
        } else if("jsonld".equals(format)){
            finalFormat = "jsonld";        
        } else {
            return null;
        }
        return writerRegistry.getWriterByIdentifier(finalFormat);
    }

}
