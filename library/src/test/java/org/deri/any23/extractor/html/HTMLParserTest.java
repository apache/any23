package org.deri.any23.extractor.html;

import junit.framework.TestCase;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HTMLParserTest extends TestCase {

    public void testParseSimpleHTML() throws IOException {
        String html = "<html><head><title>Test</title></head><body><h1>Hello!</h1></body></html>";
        InputStream input = new ByteArrayInputStream(html.getBytes());
        Node document = new TagSoupParser(input, "http://example.com/").getDOM();
        assertEquals("Test", new HTMLDocument(document).find("//TITLE"));
        assertEquals("Hello!", new HTMLDocument(document).find("//H1"));
    }
}
