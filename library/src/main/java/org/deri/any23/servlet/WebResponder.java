package org.deri.any23.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.writer.FormatWriter;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.ReportingTripleHandler;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;

public class WebResponder {
	private Servlet any23servlet;
	private HttpServletResponse response;
	private ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
	private TripleHandler rdfWriter = null;
	private ReportingTripleHandler reporter = null;
	private String outputMediaType = null;
	
	public WebResponder(Servlet any23servlet, HttpServletResponse response) {
		this.any23servlet = any23servlet;
		this.response = response;
	}
	
	public void sendError(int code, String message) throws IOException {
		response.setStatus(code);
		response.setContentType("text/plain");
		response.getWriter().println(message);
	}
	
	public void doProcessingFromURI(String format, final String uri) throws IOException {
		if (!isValidURI(uri)) {
			sendError(400, "Invalid input URI " + uri);
			return;
		}
		runExtraction(format, new ExtractionRunner() {
			public boolean run() throws ExtractionException, IOException {
				return any23servlet.doExtract(createRunner(), uri, rdfWriter);
			}
		});
	}

	public void doProcessingFromBody(String format, final String body, final String contentType) throws IOException {
		runExtraction(format, new ExtractionRunner() {
			public boolean run() throws ExtractionException, IOException {
				return createRunner().extract(body, Servlet.DEFAULT_BASE_URI, contentType, null, rdfWriter);
			}
		});
	}
	
	private void runExtraction(String format, ExtractionRunner extractionRunner) throws IOException {
		if (!initRdfWriter(format)) return;
		try {
			if (!extractionRunner.run()) {
				sendError(415, "No suitable extractor found for this media type");
				return;
			}
		} catch (IOException e) {
			any23servlet.log("Could not fetch input", e);
			sendError(400, "Could not fetch input: " + e.getMessage());
			return;
		} catch (ExtractionException e) {
			any23servlet.log("Could not parse input", e);
			sendError(400, "Could not parse input: " + e.getMessage());
			return;
		}
		any23servlet.log("Extraction complete, " + reporter.getTotalTriples() + " triples");
		if (reporter.getTotalTriples() == 0) {
			response.setStatus(204);	// HTTP 204 No Content
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
		if ("turtle".equals(format) || "n3".equals(format) || "ttl".equals(format)) {
			return new TurtleWriter(byteOutStream);
		}
		if ("ntriples".equals(format) || "nt".equals(format)) {
			return new NTriplesWriter(byteOutStream);
		}
		return null;
	}

	private boolean isValidURI(String s) {
		try {
			URI uri = new URI(s);
			if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme())) {
				return false;
			}
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}

	private Any23 createRunner() {
		Any23 runner = new Any23();
		runner.setHTTPUserAgent("Any23-Servlet");
		return runner;
	}
	
	private interface ExtractionRunner {
		boolean run() throws ExtractionException, IOException;
	}	
}
