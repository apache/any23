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

package org.deri.any23.servlet;

import org.deri.any23.Any23;
import org.deri.any23.ExtractionReport;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.validator.SerializationException;
import org.deri.any23.validator.ValidationReport;
import org.deri.any23.validator.XMLValidationReportSerializer;
import org.deri.any23.writer.*;
import sun.security.validator.ValidatorException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for building the {@link org.deri.any23.servlet.Servlet}
 * web response.
 */
class WebResponder {

    private static final WriterRegistry writerRegistry = WriterRegistry.getInstance();

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
        runner.setHTTPUserAgent("Any23-Servlet");
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
        if (in == null) return;
        if (!initRdfWriter(format, report, annotate)) return;
        final ExtractionReport er;
        try {
            er = runner.extract(eps, in, rdfWriter);
            rdfWriter.close();
            if (! er.hasMatchingExtractors() ) {
                sendError(
                        415,
                        "No suitable extractor found for this media type",
                        null,
                        er.getValidationReport(),
                        report
                );
                return;
            }
        } catch (IOException ioe) {
            // IO Error.
            if (ioe.getCause() != null && ValidatorException.class.equals(ioe.getCause().getClass())) {
                final String errMsg = "Could not fetch input, IO Error.";
                any23servlet.log(errMsg, ioe.getCause());
                sendError(502, errMsg, ioe, null, report);
                return;
            }
            any23servlet.log("Could not fetch input", ioe);
            sendError(502, "Could not fetch input.", ioe, null, report);
            return;
        } catch (ExtractionException e) {
            // Extraction error.
            any23servlet.log("Could not parse input", e);
            sendError(502, "Could not parse input.", e, null, report);
            return;
        } catch (Exception e) {
            any23servlet.log("Internal error", e);
            sendError(500, "Internal error.", e, null, report);
            return;
        }

        /* *** No triples found. *** */
        any23servlet.log("Extraction complete, " + reporter.getTotalTriples() + " triples");
        if (reporter.getTotalTriples() == 0) {
            sendError(
                    501,
                    "Extraction completed. No triples have been found.",
                    null,
                    er.getValidationReport(), report
            );
            return;
        }

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
                printResponse(reporter, er.getValidationReport(), data, ps);
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

    private void printResponse(ReportingTripleHandler rth, ValidationReport vr, byte[] data, PrintStream ps) {
        ps.println("<response>");
        printExtractors(rth, ps);
        printReport(null, null, vr, ps);
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

    private void printReport(String msg, Throwable e, ValidationReport vr, PrintStream ps) {
        XMLValidationReportSerializer reportSerializer = new XMLValidationReportSerializer();
        ps.println("<report>");
        ps.printf("<message>%s</message>\n", msg == null ? "" : msg);
        ps.println("<error>");
        if(e != null) {
            ps.println("<![CDATA[");
            e.printStackTrace(ps);
            ps.println("]]>");
        }
        ps.println("</error>");
        // ps.println("<![CDATA[");
        try {
            reportSerializer.serialize(vr, ps);
        } catch (SerializationException se) {
            ps.println("An error occurred while serializing error.");
            se.printStackTrace(ps);
        }
        // ps.println("]]>");
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

    private void sendError(int code, String msg, Exception e, ValidationReport vr, boolean report)
    throws IOException {
        response.setStatus(code);
        response.setContentType("text/plain");
        final PrintStream ps = new PrintStream(response.getOutputStream());
        if (report) {
            try {
                printHeader(ps);
                printReport(msg, e, vr, ps);
            } finally {
                ps.close();
            }
        } else {
            ps.println(msg);
            if (e != null) {
                ps.println("================================================================");
                e.printStackTrace(ps);
                ps.println("================================================================");
            }
        }
    }

    private boolean initRdfWriter(String format, boolean report, boolean annotate) throws IOException {
        final FormatWriter fw = getFormatWriter(format, annotate);
        if (fw == null) {
            sendError(
                    400,
                    "Invalid format '" + format + "', try one of: [rdfxml, turtle, ntriples, nquads, trix, json]",
                    null,
                    null,
                    report
            );
            return false;
        }
        outputMediaType = WriterRegistry.getMimeType( fw.getClass() );
        List<TripleHandler> tripleHandlers = new ArrayList<TripleHandler>();
        tripleHandlers.add(new IgnoreAccidentalRDFa(fw));
        tripleHandlers.add(new CountingTripleHandler());
        rdfWriter = new CompositeTripleHandler(tripleHandlers);
        reporter = new ReportingTripleHandler(rdfWriter);
        rdfWriter = reporter;
        return true;
    }

    private FormatWriter getFormatWriter(String format, boolean annotate) throws IOException {
        final String finalFormat;
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
        } else {
            return null;
        }
        final FormatWriter writer = writerRegistry.getWriterInstanceByIdentifier(finalFormat, byteOutStream);
        writer.setAnnotated(annotate);
        return writer;
    }

}
