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

package org.apache.any23.extractor.html;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.any23.AbstractAny23TestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class to ensure behaviors of {@link HTMLDocument} parser with encoding
 * corner cases. 
 */
public class EncodingTest extends AbstractAny23TestBase {

    private final static String HELLO_WORLD = "Hell\u00F6 W\u00F6rld!";

    @Test
    public void testEncodingHTML_ISO_8859_1() throws Exception {
        HTMLDocument document = parseHTML("/microformats/xfn/encoding-iso-8859-1.html");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingHTML_UTF_8() throws Exception {
        HTMLDocument document = parseHTML("/microformats/xfn/encoding-utf-8.html");
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
     * @throws Exception if there is an error asserting the test data.
     */
    @Test
    public void testEncodingHTML_UTF_8_DeclarationAfterTitle() throws Exception {
        HTMLDocument document = parseHTML("/microformats/xfn/encoding-utf-8-after-title.html");
        Assert.assertNotSame(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingXHTML_ISO_8859_1() throws Exception {
        HTMLDocument document = parseHTML("/microformats/xfn/encoding-iso-8859-1.xhtml");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    @Test
    public void testEncodingXHTML_UTF_8() throws Exception {
        HTMLDocument document = parseHTML("/microformats/xfn/encoding-utf-8.xhtml");
        Assert.assertEquals(HELLO_WORLD, document.find("//TITLE"));
    }

    private HTMLDocument parseHTML(String filename) throws FileNotFoundException, IOException {
        return new HTMLFixture(copyResourceToTempFile(filename)).getHTMLDocument();
    }
}
