package org.deri.any23.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deri.any23.http.HTTPClient;
import org.deri.any23.source.ByteArrayDocumentSource;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.deri.any23.source.StringDocumentSource;

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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		// Show /resources/form.html for GET requests to the app's root
		if (("/".equals(req.getPathInfo()) && req.getQueryString() == null)) {
			getServletContext().getRequestDispatcher("/resources/form.html").forward(req, resp);
			return;
		}
		// forward requests to /resources/* to the default servlet, this is
		// where we can put static files
		if (req.getPathInfo().startsWith("/resources/")) {
			getServletContext().getNamedDispatcher("default").forward(req, resp);
			return;
		}
		WebResponder responder = new WebResponder(this, resp);
		String format = getFormatFromRequest(req);
		String uri = getInputURIFromRequest(req);
		if (format == null || uri == null) {
			responder.sendError(404, "Invalid GET request, try /format/some-domain.example.com/my-input-file.rdf");
			return;
		}
		responder.runExtraction(createHTTPDocumentSource(responder, uri), format);
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
			responder.runExtraction(createHTTPDocumentSource(responder, uri), format);
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
			responder.runExtraction( 
					new StringDocumentSource(req.getParameter("body"), Servlet.DEFAULT_BASE_URI, type),
					format);
			return;
		}
		log("Attempting conversion to '" + format + "' from POST body");
		responder.runExtraction(
				new ByteArrayDocumentSource(req.getInputStream(), Servlet.DEFAULT_BASE_URI, getContentTypeHeader(req)),
				format);
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
		} else if (hasOnlySingleSlashAfterScheme(uri)) {
			 // This is to work around an issue where Tomcat 6.0.18 is
			 // too smart for us. Tomcat normalizes double-slashes in
			 // the path, and thus turns "http://" into "http:/" if it
			 // occurs in the path. So we restore the double slash.
			uri = uri.replaceFirst(":/", "://");
		}
		return uri;
	}
	
	// RFC 3986: scheme = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
	private final static Pattern schemeRegex = 
		Pattern.compile("^[a-zA-Z][a-zA-Z0-9.+-]*:");
	private boolean hasScheme(String uri) {
		return schemeRegex.matcher(uri).find();
	}
	
	private final static Pattern schemeAndSingleSlashRegex =
		Pattern.compile("^[a-zA-Z][a-zA-Z0-9.+-]*:/[^/]");
	private boolean hasOnlySingleSlashAfterScheme(String uri) {
		return schemeAndSingleSlashRegex.matcher(uri).find();
	}

	private String getContentTypeHeader(HttpServletRequest req) {
		if (req.getHeader("Content-Type") == null) return null;
		if ("".equals(req.getHeader("Content-Type"))) return null;
		return req.getHeader("Content-Type");
	}

	private DocumentSource createHTTPDocumentSource(WebResponder responder, String uri) throws IOException {
		try {
			if (!isValidURI(uri)) {
				throw new URISyntaxException(uri, "@@@");
			}
			return createHTTPDocumentSource(responder.getRunner().getHTTPClient(), uri);
		} catch (URISyntaxException ex) {
			responder.sendError(400, "Invalid input URI " + uri);
			return null;
		}
	}

	protected DocumentSource createHTTPDocumentSource(HTTPClient httpClient, String uri) 
	throws IOException, URISyntaxException {
		return new HTTPDocumentSource(httpClient, uri);
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
}