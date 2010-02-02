package org.deri.any23.servlet;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.writer.FormatWriter;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.ReportingTripleHandler;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;
import sun.security.validator.ValidatorException;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WebResponder {
    private Servlet any23servlet;
    private HttpServletResponse response;
    private ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    private TripleHandler rdfWriter = null;
    private ReportingTripleHandler reporter = null;
    private String outputMediaType = null;
    private final Any23 runner;

    public WebResponder(Servlet any23servlet, HttpServletResponse response) {
        this.any23servlet = any23servlet;
        this.response = response;
        this.runner = new Any23();
        runner.setHTTPUserAgent("Any23-Servlet");
    }

    public void sendError(int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("text/plain");
        response.getWriter().println(message);
    }

    public void runExtraction(DocumentSource in, String format) throws IOException {
        if (in == null) return;
        if (!initRdfWriter(format)) return;
        try {
            if (!runner.extract(in, rdfWriter)) {
                sendError(415, "No suitable extractor found for this media type");
                return;
            }
        } catch (IOException e) {
            if (e.getCause() != null && ValidatorException.class.equals(e.getCause().getClass())) {
                any23servlet.log("Could not fetch input; untrusted SSL certificate?", e.getCause());
                sendError(502, "Could not fetch input; untrusted SSL certificate? " + e.getCause());
                return;
            }
            any23servlet.log("Could not fetch input", e);
            sendError(502, "Could not fetch input: " + e.getMessage());
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
        response.getOutputStream().write(byteOutStream.toByteArray());
    }

    private boolean initRdfWriter(String format) throws IOException {
        FormatWriter fw = getFormatWriter(format);
        if (fw == null) {
            sendError(400, "Invalid format '" + format + "', try one of rdfxml, turtle, ntriples");
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
        return null;
    }

    Any23 getRunner() {
        return runner;
    }
}
