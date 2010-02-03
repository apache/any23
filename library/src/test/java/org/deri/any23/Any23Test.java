/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23;

import junit.framework.Assert;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.writer.NTriplesWriter;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * Test case for {@link org.deri.any23.Any23} facade. 
 */
public class Any23Test {

    private String url = "http://bob.com";

    @Test
    public void testTTLDetection() throws Exception {
        assertReads("<a> <b> <c> .", "rdf-turtle");
    }

    @Test
    public void testN3Detection() throws Exception {
        assertReads("<Bob><brothers>(<Jim><Mark>).", "rdf-turtle");
    }

    @Test
    public void testNTRIPDetection() throws Exception {
        assertReads(
            "<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> .",
            "rdf-nt"
        );
    }

    @Test
    public void testHTMLDetection() throws Exception {
        assertReads("<html><body><div class=\"vcard fn\">Joe</div></body></html>");
    }

    private void assertReads(String content, String... parsers) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Any23 runner = new Any23(parsers.length == 0 ? null : parsers);
        if (parsers.length != 0) {
            runner.setMIMETypeDetector(null);   // Use all the provided extractors.
        }
        runner.extract(new StringDocumentSource(content, url), new NTriplesWriter(out));
        String result = out.toString("us-ascii");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length() > 10);
    }
}
