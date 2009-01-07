package com.google.code.any23;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.code.any23.RDFizer;
import com.google.code.any23.Rover;
import com.google.code.any23.RDFizer.Format;


import junit.framework.TestCase;

public class TestRoverGuessEncoding extends TestCase {

	static class Rovy extends Rover {
		public Rovy(URL url) {
			super(url);
		}

		public RDFizer.Format guess(String response, String url){
			return guessResponseFormat(response, url);
		}
	}
	private Rovy rover;
	
	public void setUp() throws MalformedURLException {
		rover = new Rovy(new URL("http://foo.com"));
	}
	
	public void testContentPlain() {
		assertGuess(Format.RDFXML, "text/plain", "foaf.rdf");
	}
	
	public void testContentTextRdf() {
		assertGuess(Format.RDFXML, "text/rdf", "foaf");
	}
	
	public void testContentTextN3() {
		assertGuess(Format.N3, "text/rdf+n3", "foaf");
	}	
	
	public void testContentTextTurtle() {
		assertGuess(Format.TURTLE, "text/turtle", "foaf");
	}	
	
	public void testContent() {
		assertGuess(Format.RDFXML, "application/rdf+xml", "foaf");
	}
	
	public void testContentXml() {
		assertGuess(Format.RDFXML, "application/xml", "foaf.rdf");
	}
	
	public void testExtensionN3() {
		assertGuess(Format.N3, "text/plain", "foaf.n3");
	}
	
	public void testXmlAndNoExtension() {
		assertGuess(Format.RDFXML, "application/xml", "foaf");
	}
	public void testTextXmlAndNoExtension() {
		assertGuess(Format.RDFXML, "text/xml", "foaf");
	}

	public void testTextHtmAndNoExtension() {
		assertGuess(Format.HTML, "text/html", "foaf");
	}
	
	public void testTextPlainAndExtensions() {
		assertGuess(Format.HTML, "text/plain", "foaf.html");
		assertGuess(Format.HTML, "text/plain", "foaf.htm");
		assertGuess(Format.HTML, "text/plain", "foaf.xhtml");
	}

	public void testApplicationXmlAndExtensions() {
		assertGuess(Format.HTML, "application/xml", "foaf.html");
		assertGuess(Format.HTML, "application/xml", "foaf.htm");
		assertGuess(Format.HTML, "application/xml", "foaf.xhtml");
	}
	
	private void assertGuess(RDFizer.Format f, String a, String b) {
		assertEquals(f, rover.guess(a,b));
	}
}
