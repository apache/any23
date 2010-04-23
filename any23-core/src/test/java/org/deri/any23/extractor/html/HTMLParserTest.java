/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
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
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reference Test class for the {@link org.deri.any23.extractor.html.TagSoupParser} parser.
 */
public class HTMLParserTest {

    @Test
    public void testParseSimpleHTML() throws IOException {
        String html = "<html><head><title>Test</title></head><body><h1>Hello!</h1></body></html>";
        InputStream input = new ByteArrayInputStream(html.getBytes());
        Node document = new TagSoupParser(input, "http://example.com/").getDOM();
        Assert.assertEquals("Test", new HTMLDocument(document).find("//TITLE"));
        Assert.assertEquals("Hello!", new HTMLDocument(document).find("//H1"));
    }

}
