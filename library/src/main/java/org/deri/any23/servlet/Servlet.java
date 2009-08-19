package org.deri.any23.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.stream.InputStreamCacheMem;
import org.deri.any23.writer.FormatWriter;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;

/**
 * A servlet that fetches a client-specified URI, RDFizes the content,
 * and returns it in a format chosen by the client.
 * 
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Servlet extends HttpServlet {

	public static final String DEFAULT_BASE_URI = "http://any23.org/tmp";
	private static final long serialVersionUID = 8207685628715421336L;

	private String getFormatFromRequest(HttpServletRequest request) {
		if (request.getPathInfo() == null) return null;
		String[] args = request.getPathInfo().split("/", 3);
		if (args.length < 2 || "".equals(args[1])) {
			if (request.getParameter("format") != null) {
				return request.getParameter("format");
			}
		}
		return args[1];
	}
	
	private String getInputURIFromRequest(HttpServletRequest request) {
		if (request.getPathInfo() == null) return null;
		String[] args = request.getPathInfo().split("/", 3);
		if (args.length < 3) {
			if (request.getParameter("uri") != null) {
				return request.getParameter("uri");
			}
			if (request.getParameter("url") != null) {
				return request.getParameter("url");
			}
			return null;
		}
		String uri = args[2];
		if (request.getQueryString() != null) {
			uri = uri + "?" + request.getQueryString();
		}
		if (!hasScheme(uri)) {
			uri = "http://" + uri;
		}
		return uri;
	}
	
	// RFC 3986: scheme = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
	private final static Pattern schemeRegex = Pattern.compile("^[a-zA-Z][a-zA-Z0-9.+-]*:");
	private boolean hasScheme(String uri) {
		return schemeRegex.matcher(uri).find();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String format = getFormatFromRequest(req);
		String uri = getInputURIFromRequest(req);
		if (format == null || uri == null) {
			sendError(resp, 404, "Invalid GET request, try /format/some-domain.example.com/my-input-file.rdf");
			return;
		}
		doProcessingFromURI(resp, format, uri);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String uri = getInputURIFromRequest(req);
		String format = getFormatFromRequest(req);
		if (format == null || "".equals(format)) {
			sendError(resp, 400, "Invalid POST request, format parameter not specified");
		}
		if (uri != null) {
			log("Attempting conversion to '" + format + "' from URI <" + uri + ">");
			doProcessingFromURI(resp, format, uri);
			return;
		}
		if (uri == null && !"application/x-www-form-urlencoded".equals(req.getContentType())) {
			log("Attempting conversion to '" + format + "' from POST body");
			doProcessingFromBody(resp, format, new String(InputStreamCacheMem.toByteArray(req.getInputStream()), "utf-8"));
			return;
		}
		if (uri == null && req.getParameter("body") != null) {
			log("Attempting conversion to '" + format + "' from body parameter");
			doProcessingFromBody(resp, format, req.getParameter("body"));
			return;
		}
		sendError(resp, 400, "Invalid POST request, uri or body parameter not specified");
	}

	private void doProcessingFromURI(HttpServletResponse resp, 
			String format, String uri) throws IOException {
		if (!isValidURI(uri)) {
			sendError(resp, 400, "Invalid input URI " + uri);
			return;
		}
		FormatWriter output = getFormatWriter(format, resp);
		if (output == null) return;
		try {
			resp.setContentType(output.getMIMEType());
			resp.setStatus(200);		
			doExtract(uri, output);
		} catch (IOException e) {
			sendError(resp, 400, "Could not fetch input: " + e.getMessage());
		} catch (ExtractionException e) {
			sendError(resp, 400, "Could not parse input: " + e.getMessage());
		}
	}

	private void doProcessingFromBody(HttpServletResponse resp, 
			String format, String body) throws IOException {
		FormatWriter output = getFormatWriter(format, resp);
		if (output == null) return;
		try {
			resp.setContentType(output.getMIMEType());
			resp.setStatus(200);		
			createRunner().extract(body, DEFAULT_BASE_URI, output);
		} catch (IOException e) {
			sendError(resp, 400, "Could not fetch input: " + e.getMessage());
		} catch (ExtractionException e) {
			sendError(resp, 400, "Could not parse input: " + e.getMessage());
		}
	}
	
	private FormatWriter getFormatWriter(String format, HttpServletResponse resp) 
	throws IOException {
		if ("rdf".equals(format) || "xml".equals(format) || "rdfxml".equals(format)) {
			return new RDFXMLWriter(resp.getOutputStream());
		}
		if ("turtle".equals(format) || "n3".equals(format) || "ttl".equals(format)) {
			return new TurtleWriter(resp.getOutputStream());
		}
		if ("ntriples".equals(format) || "nt".equals(format)) {
			return new NTriplesWriter(resp.getOutputStream());
		}
		sendError(resp, 400, "Invalid format '" + format + "', try one of rdfxml, turtle, ntriples");
		return null;
	}

	private boolean isValidURI(String uri) {
		try {
			URL url = new URL(uri);
			if (!"http".equals(url.getProtocol()) && !"https".equals(url.getProtocol())) {
				return false;
			}
		} catch (MalformedURLException e) {
			return false;
		}
		return true;
	}

	private Any23 createRunner() {
		Any23 runner = new Any23();
		runner.setHTTPUserAgent("Any23-Servlet");
		return runner;
	}
	
	protected void doExtract(String uri, TripleHandler output) throws ExtractionException, IOException {
		createRunner().extract(uri, output);
	}
	
	private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
		resp.setStatus(code);
		resp.setContentType("text/plain");
		resp.getWriter().print(message);
	}
}
