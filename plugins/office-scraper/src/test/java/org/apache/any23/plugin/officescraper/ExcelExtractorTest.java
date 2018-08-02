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

package org.apache.any23.plugin.officescraper;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractionResultImpl;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.Excel;
import org.apache.any23.writer.CompositeTripleHandler;
import org.apache.any23.writer.CountingTripleHandler;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test case for {@link ExcelExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExcelExtractorTest {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExtractorTest.class);

    private ExcelExtractor extractor;

    @Before
    public void setUp() {
        extractor = new ExcelExtractorFactory().createExtractor();
    }

    @Test
    public void testGetDescription() {
        Assert.assertNotNull( extractor.getDescription() );
    }

    @Test
    public void testExtractXLSX() throws IOException, ExtractionException, TripleHandlerException {
        final String FILE = "test1-workbook.xlsx";
        processFile(FILE);
    }

    @Test
    public void testExtractXLS() throws IOException, ExtractionException, TripleHandlerException {
        final String FILE = "test2-workbook.xls";
        processFile(FILE);
    }

    private void processFile(String resource) throws IOException, ExtractionException, TripleHandlerException {
        final ExtractionParameters extractionParameters = ExtractionParameters.newDefault();
        final ExtractionContext extractionContext = new ExtractionContext(
                extractor.getDescription().getExtractorName(),
                RDFUtils.iri("file://" + resource)
        );
        final InputStream is = this.getClass().getResourceAsStream(resource);
        final CompositeTripleHandler compositeTripleHandler = new CompositeTripleHandler();
        final TripleHandler verifierTripleHandler = Mockito.mock(TripleHandler.class);
        compositeTripleHandler.addChild(verifierTripleHandler);
        final CountingTripleHandler countingTripleHandler = new CountingTripleHandler();
        compositeTripleHandler.addChild(countingTripleHandler);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        compositeTripleHandler.addChild( new NTriplesWriter(out) );
        final ExtractionResult extractionResult = new ExtractionResultImpl(
                extractionContext, extractor, compositeTripleHandler
        );
        extractor.run(extractionParameters, extractionContext, is, extractionResult);
        compositeTripleHandler.close();
        logger.debug(out.toString());

        verifyPredicateOccurrence(verifierTripleHandler, Excel.getInstance().containsSheet, 2 );
        verifyPredicateOccurrence(verifierTripleHandler, Excel.getInstance().containsRow  , 6 );
        verifyPredicateOccurrence(verifierTripleHandler, Excel.getInstance().containsCell , 18);

        verifyTypeOccurrence(verifierTripleHandler, Excel.getInstance().sheet, 2 );
        verifyTypeOccurrence(verifierTripleHandler, Excel.getInstance().row  , 6 );
        verifyTypeOccurrence(verifierTripleHandler, Excel.getInstance().cell , 18);
    }

    private void verifyPredicateOccurrence(TripleHandler mock, IRI predicate, int occurrence)
    throws TripleHandlerException {
        Mockito.verify( mock, Mockito.times(occurrence)).receiveTriple(
                Mockito.any(),
                Mockito.eq(predicate),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        );
    }

    private void verifyTypeOccurrence(TripleHandler mock, IRI type, int occurrence)
    throws TripleHandlerException {
        Mockito.verify( mock, Mockito.times(occurrence)).receiveTriple(
                Mockito.any(),
                Mockito.eq(RDF.TYPE),
                Mockito.eq(type),
                Mockito.any(),
                Mockito.any()
        );
    }

}
