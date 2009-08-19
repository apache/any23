package org.deri.any23.servlet;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.source.MemCopyFactory;
import org.deri.any23.writer.TripleHandler;

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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		WebResponder responder = new WebResponder(this, resp);
		String format = getFormatFromRequest(req);
		String uri = getInputURIFromRequest(req);
		if (format == null || uri == null) {
			responder.sendError(404, "Invalid GET request, try /format/some-domain.example.com/my-input-file.rdf");
			return;
		}
		responder.doProcessingFromURI(format, uri);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		WebResponder responder = new WebResponder(this, resp);
		if (req.getContentType() == null) {
			responder.sendError(400, "Invalid POST request, no Content-Type for the message body specified");
			return;
		}
		String uri = getInputURIFromRequest(req);
		String format = getFormatFromRequest(req);
		if (format == null || "".equals(format)) {
			responder.sendError(400, "Invalid POST request, format parameter not specified");
		}
		if (uri != null) {
			log("Attempting conversion to '" + format + "' from URI <" + uri + ">");
			responder.doProcessingFromURI(format, uri);
			return;
		}
		if ("application/x-www-form-urlencoded".equals(req.getContentType())) {
			if (req.getParameter("body") == null) {
				responder.sendError(400, "Invalid POST request, parameter 'uri' or 'body' required");
				return;
			}
			String type = null;
			if (req.getParameter("type") != null && !"".equals(req.getParameter("type"))) {
				type = req.getParameter("type");
			}
			log("Attempting conversion to '" + format + "' from body parameter");
			responder.doProcessingFromBody(format, req.getParameter("body"), type);
			return;
		}
		log("Attempting conversion to '" + format + "' from POST body");
		responder.doProcessingFromBody(format, 
				new String(MemCopyFactory.toByteArray(req.getInputStream()), "utf-8"), 
				getContentTypeHeader(req));
	}

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

	private String getContentTypeHeader(HttpServletRequest req) {
		if (req.getHeader("Content-Type") == null) return null;
		if ("".equals(req.getHeader("Content-Type"))) return null;
		return req.getHeader("Content-Type");
	}

	// Hack: Allow overriding for easier testing.
	protected boolean doExtract(Any23 runner, String uri, TripleHandler output) throws ExtractionException, IOException {
		return runner.extract(uri, output);
	}
}