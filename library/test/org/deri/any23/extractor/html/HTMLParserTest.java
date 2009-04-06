package org.deri.any23.extractor.html;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.extractor.html.TagSoupParser;
import org.w3c.dom.Node;

public class HTMLParserTest extends TestCase {

	public void testParseSimpleHTML() {
		String html = "<html><head><title>Test</title></head><body><h1>Hello!</h1></body></html>";
		InputStream input = new ByteArrayInputStream(html.getBytes());
		Node document = new TagSoupParser(input, "http://example.com/").getDOM();
		assertEquals("Test", new HTMLDocument(document).find("//TITLE"));
		assertEquals("Hello!", new HTMLDocument(document).find("//H1"));
	}
}
