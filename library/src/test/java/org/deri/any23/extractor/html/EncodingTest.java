package org.deri.any23.extractor.html;

import org.junit.Test;
import org.junit.Assert;

public class EncodingTest {

    private final static String HELLO_WORLD = "Hell\u00F6 W\u00F6rld!";

    @Test
    public void testEncodingHTML_ISO_8859_1() {
        HTMLDocument document = parseHTML("xfn/encoding-iso-8859-1.html");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingHTML_UTF_8() {
        HTMLDocument document = parseHTML("xfn/encoding-utf-8.html");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    public void testEncodingHTML_UTF_8_DeclarationAfterTitle() {
        /**
         * Known issue: NekoHTML does not auto-detect the encoding, but relies
         * on the explicitly specified encoding (via XML declaration or
         * HTTP-Equiv meta header). If the meta header comes *after* the
         * title element, then NekoHTML will not use the declared encoding
         * for the title.
         *
         * This test has been disabled.
         * TODO (medium): try to see if the latest version of Neko may help with that
         *
         */
        HTMLDocument document = parseHTML("xfn/encoding-utf-8-after-title.html");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingXHTML_ISO_8859_1() {
        HTMLDocument document = parseHTML("xfn/encoding-iso-8859-1.xhtml");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingXHTML_UTF_8() {
        HTMLDocument document = parseHTML("xfn/encoding-utf-8.xhtml");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    private HTMLDocument parseHTML(String filename) {
        return new HTMLFixture(filename).getHTMLDocument();
    }
}
