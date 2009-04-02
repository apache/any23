package org.deri.any23.mime;

import java.net.MalformedURLException;
import java.net.URL;

import org.sindice.rdfizer.RDFizer;
import org.sindice.rdfizer.Rover;
import org.sindice.rdfizer.RDFizer.Format;



import junit.framework.TestCase;

public class TestGuessMIMEType extends TestCase {

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
		assertGuess(Format.RDFXML, "text/plain", "foo.rdf");
	}
	
	public void testContentTextRdf() {
		assertGuess(Format.RDFXML, "text/rdf", "foo");
	}
	
	public void testContentTextN3() {
		assertGuess(Format.N3, "text/rdf+n3", "foo");
	}	
	
	public void testContentTextTurtle() {
		assertGuess(Format.TURTLE, "text/turtle", "foo");
	}	
	
	public void testContent() {
		assertGuess(Format.RDFXML, "application/rdf+xml", "foo");
	}
	
	public void testContentXml() {
		assertGuess(Format.RDFXML, "application/xml", "foo.rdf");
	}
	
	public void testExtensionN3() {
		assertGuess(Format.N3, "text/plain", "foo.n3");
	}
	
	public void testXmlAndNoExtension() {
		assertGuess(Format.RDFXML, "application/xml", "foo");
	}
	public void testTextXmlAndNoExtension() {
		assertGuess(Format.RDFXML, "text/xml", "foo");
	}

	public void testTextHtmAndNoExtension() {
		assertGuess(Format.XHTML, "text/html", "foo");
	}
	
	public void testTextPlainAndExtensions() {
		assertGuess(Format.XHTML, "text/plain", "foo.html");
		assertGuess(Format.XHTML, "text/plain", "foo.htm");
		assertGuess(Format.XHTML, "text/plain", "foo.xhtml");
	}

	public void testApplicationXmlAndExtensions() {
		assertGuess(Format.XHTML, "application/xml", "foo.html");
		assertGuess(Format.XHTML, "application/xml", "foo.htm");
		assertGuess(Format.XHTML, "application/xml", "foo.xhtml");
	}
	
	private void assertGuess(RDFizer.Format f, String a, String b) {
		assertEquals(f, rover.guess(a,b));
	}
}
