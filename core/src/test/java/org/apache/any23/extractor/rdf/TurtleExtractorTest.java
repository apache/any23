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
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractionResultImpl;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test case for {@link NTriplesExtractor}.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class TurtleExtractorTest {

    private static final Logger logger = LoggerFactory.getLogger(TurtleExtractorTest.class);

    private TurtleExtractor extractor;

    @Before
    public void setUp() {
        extractor = new TurtleExtractor();
    }

    @After
    public void tearDown() {
        extractor = null;
    }

    /**
     * Tests the correct support for a typed literal with incompatible value.
     * 
     * @throws IOException if there is an error interpreting the input data
     * @throws ExtractionException if there is an exception during extraction
     * @throws TripleHandlerException if there is an error within the {@link org.apache.any23.writer.TripleHandler} implementation
     */
    @Test
    public void testTypedLiteralIncompatibleValueSupport()
    throws IOException, ExtractionException, TripleHandlerException {
        final IRI uri = RDFUtils.iri("http://host.com/test-malformed-literal.turtle");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TripleHandler th = new RDFXMLWriter(baos);
        final ExtractionContext extractionContext = new ExtractionContext("turtle-extractor", uri);
        final ExtractionResult result = new ExtractionResultImpl(extractionContext, extractor, th);
        extractor.setStopAtFirstError(false);
        try {
            extractor.run(
                    ExtractionParameters.newDefault(),
                    extractionContext,
                    this.getClass().getResourceAsStream("/org/apache/any23/extractor/rdf/testMalformedLiteral"),
                    result
            );
        } finally {
            logger.debug(baos.toString());
            th.close();
            result.close();
        }
    }

}
