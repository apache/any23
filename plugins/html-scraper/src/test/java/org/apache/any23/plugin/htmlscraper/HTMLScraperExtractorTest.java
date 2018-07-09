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

package org.apache.any23.plugin.htmlscraper;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test case for {@link HTMLScraperExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HTMLScraperExtractorTest {

    private HTMLScraperExtractor extractor;

    @Before
    public void setUp() {
        extractor = new HTMLScraperExtractorFactory().createExtractor();
    }

    @After
    public void tearDown() {
        extractor = null;
    }

    @Test
    public void testGetExtractors() {
        final String[] extractors = extractor.getTextExtractors();
        Assert.assertEquals( new HashSet<>(Arrays.asList(extractors)).size(), 4 );
    }

    @Test
    public void testRun() throws IOException, ExtractionException {
        final InputStream is = this.getClass().getResourceAsStream("html-scraper-extractor-test.html");
        final ExtractionResult extractionResult = mock(ExtractionResult.class);
        final IRI pageIRI = SimpleValueFactory.getInstance().createIRI("http://fake/test/page/testrun");
        final ExtractionContext extractionContext = new ExtractionContext(
                extractor.getDescription().getExtractorName(),
                pageIRI
        );
        extractor.run(ExtractionParameters.newDefault(), extractionContext, is, extractionResult);

        verify(extractionResult).writeTriple(
                eq(pageIRI), eq(HTMLScraperExtractor.PAGE_CONTENT_DE_PROPERTY), any());
        verify(extractionResult).writeTriple(
                eq(pageIRI), eq(HTMLScraperExtractor.PAGE_CONTENT_AE_PROPERTY), any());
        verify(extractionResult).writeTriple(
                eq(pageIRI), eq(HTMLScraperExtractor.PAGE_CONTENT_LCE_PROPERTY), any());
        verify(extractionResult).writeTriple(
                eq(pageIRI), eq(HTMLScraperExtractor.PAGE_CONTENT_CE_PROPERTY), any());
    }

}
