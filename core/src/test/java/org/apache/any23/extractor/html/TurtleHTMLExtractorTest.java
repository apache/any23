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

import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorFactory;
import org.junit.Test;
import org.eclipse.rdf4j.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Test case for {@link TurtleHTMLExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TurtleHTMLExtractorTest extends AbstractExtractorTestCase {

    private static final Logger logger = LoggerFactory.getLogger(TurtleHTMLExtractor.class);

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new TurtleHTMLExtractorFactory();
    }

    /**
     * Tests the extraction of the RDF content from the sample HTML file.
     * 
     * @throws IOException if there is an error interpreting the input data
     * @throws ExtractionException if there is an exception during extraction
     * @throws org.eclipse.rdf4j.repository.RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    public void testExtraction() throws Exception {
        assertExtract("/html/html-turtle.html");
        logger.debug( dumpModelToRDFXML() );
        assertStatementsSize( null, (Value) null, 10);
    }

}
