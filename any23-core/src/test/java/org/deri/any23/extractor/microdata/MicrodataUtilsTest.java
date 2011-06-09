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

package org.deri.any23.extractor.microdata;

import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.util.StreamUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Test case for {@link MicrodataUtils}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(MicrodataUtilsTest.class);

    @Test
    public void testBasicMicrodataDetection() throws IOException {
        extractItemsAndVerifyJSONSerialization(
                "microdata-basic",
                "microdata-basic-expected"
        );
    }

    @Test
    public void testNestedMicrodataDetection() throws IOException {
        extractItemsAndVerifyJSONSerialization(
                "microdata-nested",
                "microdata-nested-expected"
        );
    }

    @Test
    public void testMicrodataJSONSerialization() throws IOException {
        final Document document = getMicrodataDom("microdata-nested");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        MicrodataUtils.getMicrodataAsJSON(document, ps);
        ps.flush();
        final String expected = StreamUtils.asString(
                this.getClass().getResourceAsStream("/microdata/microdata-json-serialization.json")
        );
        Assert.assertEquals("Unexpected serialization for Microdata file.", expected, baos.toString());
    }

    private Document getDom(String document) throws IOException {
        final InputStream is = this.getClass().getResourceAsStream(document);
        try {
            final TagSoupParser tagSoupParser = new TagSoupParser(is, "http://test-document");
            return tagSoupParser.getDOM();
        } finally {
            is.close();
        }
    }

    private Document getMicrodataDom(String htmlFile) throws IOException {
         return getDom("/microdata/" + htmlFile + ".html");
    }

    private void extractItemsAndVerifyJSONSerialization(String htmlFile, String expectedResult)
    throws IOException {
        final Document document = getMicrodataDom(htmlFile);
        final ItemScope[] items = MicrodataUtils.getMicrodata(document);
        for(ItemScope item : items) {
            logger.info( item.toJSON() );
        }

        final Properties resultContent = new Properties();
        resultContent.load( this.getClass().getResourceAsStream("/microdata/" + expectedResult + ".properties") );
        Assert.assertEquals("Unexpected number of detect items.", resultContent.entrySet().size(), items.length);
        for (int i = 0; i < items.length; i++) {
            Assert.assertEquals(
                    "Error while comparing result [" + i + "]",
                    resultContent.getProperty("result" + i),
                    items[i].toJSON()
            );
        }
    }

}
