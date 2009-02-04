package org.deri.any23.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.writer.FormatWriter;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.TurtleWriter;

/**
 * A servlet that fetches a client-specified URI, RDFizes the content,
 * and returns it in a format chosen by the client.
 * 
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Servlet extends HttpServlet {

	private static final int URL_PART = 1;
	private static final int FORMAT_PART = 0;
	private static final long serialVersionUID = 8207685628715421336L;

	/*
	 * Utility method, splits the URL so that we have a first empty part, 
	 * then the format part and the URL as a whole
	 */
	private String[] getArgumentsFromRequest(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (null==pathInfo)
			return new String[0];
		String[] args =pathInfo.split("/", 3);
		if (args.length < 3)
			return null;
		log("Splitting arguments:"+ Arrays.toString(args));
		String[] res = new String[2];
		res[0] = args[1];
		res[1] = fixSchema(args[2]+"?"+request.getQueryString());
		log("arguments:"+ Arrays.toString(res));
		return res;
	}
	
	private String fixSchema(String url) {
		return url.replaceFirst("^(http://?)?", "http://");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String[] args = getArgumentsFromRequest(req);
		if (null==args) {
			resp.setStatus(404);		
			resp.getWriter().print("Invalid request, try /format/some-url.com");
			return;
		}
		doProcessing(resp, args[FORMAT_PART], args[URL_PART]);
	}

	private void doProcessing(HttpServletResponse resp, 
			String formatString, String urlString) throws IOException {
		log("url:"+urlString);
		log("format:"+formatString);
		FormatWriter output;
		try {
			output = getTripleHandler(formatString, resp.getOutputStream());
		} catch (IllegalArgumentException e1) {
			resp.setStatus(404);	
			resp.getWriter().print("Invalid Format, try one of rdf,n3, turtle or ntriples");
			return;
		}
		
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			resp.setStatus(404);		
			resp.getWriter().print("Invalid URL, I'm sorry");
			return;
		}
		
		try {
			Any23 runner = new Any23();
			runner.extract(url.toString(), output);
			resp.setStatus(200);		
			resp.setContentType(output.getMIMEType());
		} catch (IOException e) {
			resp.setStatus(404);
			resp.getWriter().print("Could not open URL, I'm sorry");
		} catch (ExtractionException e) {
			resp.setStatus(404);
			resp.getWriter().print("Could not open URL, I'm sorry");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {	
		String url = req.getParameter("url");
		String format = req.getParameter("format");
		doProcessing(resp, format, url);
	}

	private FormatWriter getTripleHandler(String formatString, OutputStream out) 
	throws IllegalArgumentException{
		if ("rdf".equals(formatString))
			return new RDFXMLWriter(out);
		else if ("turtle".equals(formatString))
			return new TurtleWriter(out);
		else if ("n3".equals(formatString))
			return new TurtleWriter(out, true);
		else if ("ntriples".equals(formatString))
			return new NTriplesWriter(out);
		else 
			throw new IllegalArgumentException();
	}
}
