/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.html;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class to ensure behaviors of {@link org.deri.any23.extractor.html.HTMLDocument} parser with encoding
 * corner cases. 
 */
public class EncodingTest {

    private final static String HELLO_WORLD = "Hell\u00F6 W\u00F6rld!";

    @Test
    public void testEncodingHTML_ISO_8859_1() {
        HTMLDocument document = parseHTML("microformats/xfn/encoding-iso-8859-1.html");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingHTML_UTF_8() {
        HTMLDocument document = parseHTML("microformats/xfn/encoding-utf-8.html");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

     /**
     * Known issue: NekoHTML does not auto-detect the encoding, but relies
     * on the explicitly specified encoding (via XML declaration or
     * HTTP-Equiv meta header). If the meta header comes *after* the
     * title element, then NekoHTML will not use the declared encoding
     * for the title.
     *
     * For this test we expect to not recognize the title.
     */
    @Test
    public void testEncodingHTML_UTF_8_DeclarationAfterTitle() {
        HTMLDocument document = parseHTML("microformats/xfn/encoding-utf-8-after-title.html");
        Assert.assertNotSame(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingXHTML_ISO_8859_1() {
        HTMLDocument document = parseHTML("microformats/xfn/encoding-iso-8859-1.xhtml");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingXHTML_UTF_8() {
        HTMLDocument document = parseHTML("microformats/xfn/encoding-utf-8.xhtml");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    private HTMLDocument parseHTML(String filename) {
        return new HTMLFixture(filename).getHTMLDocument();
    }
}
