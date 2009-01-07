package com.google.code.any23.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.any23.RDFizer;
import com.google.code.any23.Rover;
import com.google.code.any23.RDFizer.Format;

public class Servlet extends HttpServlet {

	private static final int URL_PART = 1;
	private static final int FORMAT_PART = 0;
	private static final long serialVersionUID = 8207685628715421336L;

	/*
	 * utility method, splits the url so that we have a first empty part, then the forma part and the the url as a whole
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
		
		PrintWriter writer = resp.getWriter();
		
		String[] args = getArgumentsFromRequest(req);
		if (null==args) {
			resp.setStatus(404);		
			writer.print("Invalid request, try /format/some-url.com");
			return;
		}
			
		
		doProcessing(resp, writer, args[FORMAT_PART], args[URL_PART]);
		
	}

	private void doProcessing(HttpServletResponse resp, PrintWriter writer,
			String formatString, String urlString) {
		log("url:"+urlString);
		log("format:"+formatString);
		Format format;
		try {

			format = getFormat(formatString);
		} catch (IllegalArgumentException e1) {
			resp.setStatus(404);	
			writer.print("Invalid Format, try one of rdf,n3, turtle or ntriples");
			return;
		}

		
		
		URL url;
		try {

			url = new URL(urlString);
		} catch (MalformedURLException e) {
			resp.setStatus(404);		
			writer.print("Invalid URL, I'm sorry");
			return;
		}
		
		try {
			String rdfResult = getRdfFor(url, format);
			resp.setStatus(200);		
			resp.setContentType(contentTypeFor(format));
			writer.print(rdfResult);
		} catch (IOException e) {
			resp.setStatus(404);
			writer.print("Could not open URL, I'm sorry");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {	
		
		PrintWriter writer;
		writer = resp.getWriter();
		String url = req.getParameter("url");

		String format = req.getParameter("format");

		doProcessing(resp, writer, format, url);
	}

	private String contentTypeFor(Format format) {
		switch (format) {
			case RDFXML: 
				return "application/rdf+xml";
			case N3:	
				return "text/rdf+n3; charset=utf-8";
			case TURTLE: 
				return "application/x-turtle";
			case NTRIPLES: 
				return "text/plain";
			default :
				return "text/plain";
		}
	}

	private Format getFormat(String formatString) throws IllegalArgumentException{
		if ("rdf".equals(formatString))
			return Format.RDFXML;
		else if ("turtle".equals(formatString))
			return Format.TURTLE;
		else if ("n3".equals(formatString))
			return Format.N3;
		else if ("ntriples".equals(formatString))
			return Format.NTRIPLES;
		else 
			throw new IllegalArgumentException();
	}

	protected String getRdfFor(URL url, Format format) throws IOException {
		RDFizer rover = getRDFizer(url);
		Writer sw = new StringWriter();
		rover.getText(sw, format);
		return sw.toString();
	}

	protected RDFizer getRDFizer(URL url) {
		return new Rover(url);
	}
}
