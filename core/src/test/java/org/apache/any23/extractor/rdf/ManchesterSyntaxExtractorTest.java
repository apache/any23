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

package org.apache.any23.extractor.rdf;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractionResultImpl;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.TripleHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * Test case for {@link ManchesterSyntaxExtractor}.
 *
 * @author Peter Ansell
 */
public class ManchesterSyntaxExtractorTest {

    private static final Logger logger = LoggerFactory.getLogger(ManchesterSyntaxExtractorTest.class);

    private ManchesterSyntaxExtractor extractor;

    @Before
    public void setUp() {
        extractor = new ManchesterSyntaxExtractor();
    }

    @After
    public void tearDown() {
        extractor = null;
    }

    @Test
    public void testExampleManchesterSyntax()
    		throws Exception {
        final IRI uri = RDFUtils.iri("http://example.org/example-manchestersyntax.omn");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TripleHandler th = new RDFXMLWriter(baos);
        final ExtractionContext extractionContext = new ExtractionContext("owl-manchestersyntax-extractor", uri);
        final ExtractionResult result = new ExtractionResultImpl(extractionContext, extractor, th);
        extractor.setStopAtFirstError(false);
        try {
            extractor.run(
                    ExtractionParameters.newDefault(),
                    extractionContext,
                    this.getClass().getResourceAsStream("/text/owl-manchester/example-manchestersyntax.omn"),
                    result
            );
        } finally {
            logger.debug(baos.toString());
            th.close();
            result.close();
        }
    }

}
