/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.deri.any23.validator.XMLValidationReportSerializer;
import org.deri.any23.writer.FormatWriter;
import org.deri.any23.writer.NQuadsWriter;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.ReportingTripleHandler;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;
import sun.security.validator.ValidatorException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * This class is responsible for building the {@link org.deri.any23.servlet.Servlet}
 * web response.
 */
class WebResponder {

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

    public void sendError(int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("text/plain");
        response.getWriter().println(message);
    }

    public void runExtraction(DocumentSource in, ExtractionParameters eps, String format, boolean report)
    throws IOException {
        if (in == null) return;
        if (!initRdfWriter(format)) return;
        final ExtractionReport er;
        try {
            er = runner.extract(eps, in, rdfWriter);
            if (! er.hasMatchingExtractors() ) {
                sendError(415, "No suitable extractor found for this media type");
                return;
            }
        } catch (IOException ioe) {
            if (ioe.getCause() != null && ValidatorException.class.equals(ioe.getCause().getClass())) {
                any23servlet.log("Could not fetch input; untrusted SSL certificate?", ioe.getCause());
                sendError(502, "Could not fetch input; untrusted SSL certificate? " + ioe.getCause());
                return;
            }
            any23servlet.log("Could not fetch input", ioe);
            sendError(502, "Could not fetch input: " + ioe.getMessage());
            return;
        } catch (ExtractionException e) {
            any23servlet.log("Could not parse input", e);
            sendError(502, "Could not parse input: " + e.getMessage());
            return;
        }
        any23servlet.log("Extraction complete, " + reporter.getTotalTriples() + " triples");
        if (reporter.getTotalTriples() == 0) {
            response.setStatus(204);    // HTTP 204 No Content
            return;
        }
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
        XMLValidationReportSerializer reportSerializer = new XMLValidationReportSerializer();
        if(report) {
            PrintStream ps = new PrintStream(sos);
            try {
                ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
                ps.println("<response>");
                ps.println("<report>");
                ps.println("<![CDATA[");
                reportSerializer.serialize(er.getValidationReport(), ps);
                ps.println("]]>");
                ps.println("</report>");
                ps.println("<data>");
                ps.println("<![CDATA[");
                ps.write(byteOutStream.toByteArray());
                ps.println("]]>");
                ps.println("</data>");
                ps.println("</response>");
            } catch (SerializationException se) {
                throw new RuntimeException("An error occurred while serializing the output report.", se);
            } finally {
                ps.close();
            }
        } else {
            sos.write(byteOutStream.toByteArray());
        }
    }

    private boolean initRdfWriter(String format) throws IOException {
        FormatWriter fw = getFormatWriter(format);
        if (fw == null) {
            sendError(400, "Invalid format '" + format + "', try one of rdfxml, turtle, ntriples, nquads");
            return false;
        }
        outputMediaType = fw.getMIMEType();
        rdfWriter = new IgnoreAccidentalRDFa(fw);
        reporter = new ReportingTripleHandler(rdfWriter);
        rdfWriter = reporter;
        return true;
    }

    private FormatWriter getFormatWriter(String format) throws IOException {
        if ("rdf".equals(format) || "xml".equals(format) || "rdfxml".equals(format)) {
            return new RDFXMLWriter(byteOutStream);
        }
        if ("turtle".equals(format) || "ttl".equals(format)) {
            return new TurtleWriter(byteOutStream);
        }
        if ("n3".equals(format)) {
            return new TurtleWriter(byteOutStream, true);
        }
        if ("n-triples".equals(format) || "ntriples".equals(format) || "nt".equals(format)) {
            return new NTriplesWriter(byteOutStream);
        }
        if("nquads".equals(format) || "n-quads".equals(format) || "nq".equals(format)) {
            return new NQuadsWriter(byteOutStream);
        }
        return null;
    }

}
