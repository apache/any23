package org.deri.any23.extractor.html;

import org.deri.any23.extractor.html.HTMLDocument;
import org.junit.Test;



import junit.framework.TestCase;

public class EncodingTest extends TestCase {
	private final static String HELLO_WORLD = "Hell\u00F6 W\u00F6rld!";
	
	@Test
	public void testEncodingHTML_ISO_8859_1() {
		HTMLDocument document = parseHTML("xfn/encoding-iso-8859-1.html");
		assertEquals(HELLO_WORLD, document.find("//TITLE"));
	}

	@Test
	public void testEncodingHTML_UTF_8() {
		HTMLDocument document = parseHTML("xfn/encoding-utf-8.html");
		assertEquals(HELLO_WORLD, document.find("//TITLE"));
	}

	@Test
	public void testEncodingXHTML_ISO_8859_1() {
		HTMLDocument document = parseHTML("xfn/encoding-iso-8859-1.xhtml");
		assertEquals(HELLO_WORLD, document.find("//TITLE"));
	}
	
	@Test
	public void testEncodingXHTML_UTF_8() {
		HTMLDocument document = parseHTML("xfn/encoding-utf-8.xhtml");
		assertEquals(HELLO_WORLD, document.find("//TITLE"));
	}

	private HTMLDocument parseHTML(String filename) {
		return new HTMLFixture(filename).getHTMLDocument();
	}
}
