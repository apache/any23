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

package org.deri.any23.plugin.htmlscraper;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test case for {@link org.deri.any23.plugin.htmlscraper.HTMLScraperExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HTMLScraperExtractorTest {

    private HTMLScraperExtractor extractor;

    @Before
    public void setUp() {
        extractor = (HTMLScraperExtractor) new HTMLScraperPlugin().getExtractorFactory().createExtractor();
    }

    @After
    public void tearDown() {
        extractor = null;
    }

    @Test
    public void testGetExtractors() {
        final String[] extractors = extractor.getTextExtractors();
        Assert.assertEquals( new HashSet<String>(Arrays.asList(extractors)).size(), 4 );
    }

    @Test
    public void testRun() throws IOException, ExtractionException {
        final InputStream is = this.getClass().getResourceAsStream("html-scraper-extractor-test.html");
        final ExtractionResult extractionResult = mock(ExtractionResult.class);
        final URI pageURI = ValueFactoryImpl.getInstance().createURI("http://fake/test/page/testrun");
        extractor.run(is, pageURI, extractionResult);

        verify(extractionResult).writeTriple(
                eq(pageURI), eq(HTMLScraperExtractor.PAGE_CONTENT_DE_PROPERTY) , (Value) Matchers.anyObject())
        ;
        verify(extractionResult).writeTriple(
                eq(pageURI), eq(HTMLScraperExtractor.PAGE_CONTENT_AE_PROPERTY) , (Value) Matchers.anyObject())
        ;
        verify(extractionResult).writeTriple(
                eq(pageURI), eq(HTMLScraperExtractor.PAGE_CONTENT_LCE_PROPERTY) , (Value) Matchers.anyObject())
        ;
        verify(extractionResult).writeTriple(
                eq(pageURI), eq(HTMLScraperExtractor.PAGE_CONTENT_CE_PROPERTY) , (Value) Matchers.anyObject())
        ;
    }

}
