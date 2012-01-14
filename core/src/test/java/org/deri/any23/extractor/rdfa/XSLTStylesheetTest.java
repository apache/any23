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

package org.deri.any23.extractor.rdfa;

import org.deri.any23.extractor.html.TagSoupParser;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Test case for {@link XSLTStylesheet} class.
 * Through this test we verify regressions on the <i>RDFa XSLT transformer</i> for <i>HTML/XHTML</i>
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class XSLTStylesheetTest {

    private static final Logger logger = LoggerFactory.getLogger(XSLTStylesheetTest.class);

    /**
     * This test verifies the correct handling of base management for an <i>HTML</i> input.
     *
     * @throws java.io.IOException
     * @throws XSLTStylesheetException
     */
    @Test
    public void testHTMLRDFaBaseHanding() throws IOException, XSLTStylesheetException {
        final String[] vars = checkPageBaseHandling("/html/rdfa/base-handling.html");
        Assert.assertEquals("Unexpected value for this_location", "http://di2.deri.ie/people/", vars[0]);
        Assert.assertEquals("Unexpected value for this_root"    , "http://di2.deri.ie/"      , vars[1]);
        Assert.assertEquals("Unexpected value for html_base"    , "http://di2.deri.ie/people/", vars[2]);
    }

    /**
     * This test verifies the correct handling of base management for an <i>XHTML</i> input.
     *
     * @throws java.io.IOException
     * @throws XSLTStylesheetException
     */
    @Test
    public void testXHTMLRDFaBaseHanding() throws IOException, XSLTStylesheetException {
        final String[] vars = checkPageBaseHandling("/html/rdfa/base-handling.xhtml");
        Assert.assertEquals("Unexpected value for this_location", "http://example.org/john-d/", vars[0]);
        Assert.assertEquals("Unexpected value for this_root"    , "http://example.org/"       , vars[1]);
        Assert.assertEquals("Unexpected value for html_base"    , "http://example.org/john-d/", vars[2]);
    }

    private String[] checkPageBaseHandling(String testFile) throws IOException, XSLTStylesheetException {
        final TagSoupParser tagSoupParser = new TagSoupParser(
                this.getClass().getResourceAsStream(testFile),
                "http://test/document/uri"
        );
        final StringWriter sw = new StringWriter();
        RDFaExtractor.getXSLT().applyTo(tagSoupParser.getDOM(), sw);
        final String content = sw.toString();
        logger.debug(content);
        final Pattern pattern = Pattern.compile("<!--this_location: '(.+)' this_root: '(.+)' html_base: '(.+)'-->");
        final Matcher matcher = pattern.matcher(content);
        Assert.assertTrue("Cannot find comment matching within generated output.", matcher.find());
        return new String[]{ matcher.group(1), matcher.group(2), matcher.group(3) };
    }

}
