package com.google.code.any23;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import com.google.code.any23.RDFizer.Format;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestRoverGetText extends TestCase {

	private Model model;
	private URL url;

	public void setUp() throws MalformedURLException {
		url = new URL("http://bob.com");
		model = ModelFactory.createDefaultModel();
	}

	public void testTTL() {
		assertReads(Format.TURTLE, "<a> <b> <c> .");
	}

	public void testN3() {
		assertReads(Format.N3, "<Bob><brothers>(<Jim><Mark>).");
	}

	public void testNTRIP() {
		assertReads(
				Format.NTRIPLES,
				"<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> .");
	}

	public void testHTML() throws IOException {
		
		String content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";

		Writer writer = new StringWriter();
		MockFetcher fetcher = new MockFetcher("text/html", content);
		RDFizer rover = new Rover(url, fetcher);
		Format output = Format.RDFXML;
		boolean text = rover.getText(writer, output);
		assertTrue(text);
		model.read(new StringReader(writer.toString()), url.toString(), output
				.toString());
		assertFalse(model.isEmpty());

	}

	private void assertReads(Format output, String content)
			throws AssertionFailedError {
		Writer writer = new StringWriter();
		MockFetcher fetcher = new MockFetcher(output.toString(), content);
		RDFizer rover = new Rover(url, fetcher);
		try {
			boolean text = rover.getText(writer, output);
			assertTrue(text);
			model.read(new StringReader(writer.toString()), url.toString(),
					output.toString());
			assertFalse(model.isEmpty());
		} catch (IOException e) {
			throw new AssertionFailedError("io exception");
		}
	}
}
