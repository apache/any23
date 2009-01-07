package com.google.code.any23;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;

import org.apache.commons.httpclient.HttpMethod;

import com.google.code.any23.Fetcher.Type;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 * 
 * @author Gabriele Renzi
 */
public class Rover implements RDFizer {

	private final String CONTENT_TYPE = "Content-Type";

	private URL baseUri;

	private Fetcher fetcher;

	private String[] formats;
	
	/**
	 * @param url an http url where the document (RDF,HTML) can be found, uses Fetcher to get it
	 */
	public Rover(URL url) {
		this(url, new Fetcher());
	}
	
	/**
	 * @param url an url containing an RDF or HTML document 
	 * @param fetcher  a fetcher
	 */
	public Rover(URL url, Fetcher fetcher) {
		this.baseUri = url;
		this.fetcher = fetcher;
	}

	public boolean getText(Writer writer, Format output) throws IOException {

		HttpMethod httpMethod = fetcher.get(baseUri.toString(),
				Type.RDF_OR_HTML);
		RDFizer fizer = null;
		InputStream is = null;
		try {
			if ((httpMethod.getStatusCode() / 100) != 2) {
				return false;
			}

			Format guess = guessResponseFormat(httpMethod.getResponseHeader(
					CONTENT_TYPE).getValue(), baseUri.toString());
			is = httpMethod.getResponseBodyAsStream();
			if (null == is)
				return false;

			if (Format.HTML == guess)
				fizer = new HTMLRDFizer(baseUri, new HTMLParser(is).getDocumentNode());
			else
				fizer = new PlainRDFizer(baseUri, is, guess);
			boolean result = fizer.getText(writer, output);
			this.formats = fizer.getFormats();
			return result;
		} finally {
			if (null != is)
				is.close();
			httpMethod.releaseConnection();
		}
	}

	/**
	 * Tries to guess the content type of a document by looking at the response header and at the extension 
	 * @param contentType the content-type header
	 * @param url
	 * @return The most likely format
	 */
	protected Format guessResponseFormat(String contentType, String url) {
		String resp = contentType.toLowerCase();
		if (resp.matches(".*n3.*"))
			return Format.N3;
		if (resp.matches(".*turtle.*"))
			return Format.TURTLE;
		if (resp.matches(".*n-triples.*"))
			return Format.NTRIPLES;
		if (resp.matches(".*rdf.*"))
			return Format.RDFXML;
		if (resp.matches(".*html.*"))
			return Format.HTML;
		// ok failed to get a reasonable content type, try to guess the ending
		if (url.matches(".*n3"))
			return Format.N3;
		if (url.matches(".*rdf"))
			return Format.RDFXML;
		if (url.matches(".*html?"))
			return Format.HTML;

		return Format.RDFXML;
	}

	/*
	 * utility method, assumes NTRIPLES as the output format
	 */

	public boolean getText(Writer writer) throws IOException {
		return getText(writer, Format.NTRIPLES);
	}

	/**
	 * A simple main for testing
	 * @param args a url and an optional format name such as TURTLE,N3,N-TRIPLES,RDF/XML
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Tell me one url, or what shall I do?");
			System.err
					.println("usage: Rover <url> [TURTLE|N3|N-TRIPLES|RDFXML]");
			System.exit(1);
		}
		URL url = new URL(args[0].trim());
		String format = "TURTLE";
		if (args.length > 1)
			format = args[1];
		System.err.println(url);
		Rover ro = new Rover(url);
		PrintWriter pw = new PrintWriter(System.out);
		ro.getText(pw, Format.valueOf(format));
	}

	public String[] getFormats() {
		return formats;
	}
}
