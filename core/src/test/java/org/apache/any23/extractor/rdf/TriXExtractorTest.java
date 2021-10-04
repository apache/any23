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

import java.lang.invoke.MethodHandles;

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.html.AbstractExtractorTestCase;
//import org.apache.any23.rdf.RDFUtils;
//import org.eclipse.rdf4j.model.vocabulary.OWL;
//import org.eclipse.rdf4j.model.vocabulary.RDF;
//import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case for {@link TriXExtractor}.
 *
 */
public class TriXExtractorTest extends AbstractExtractorTestCase {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ExtractorFactory<?> getExtractorFactory() {
        return new TriXExtractorFactory();
    }

    @Test
    public void testExampleActivateTriXExtractorOnHTMLDocument() {
        assertExtract("/org/apache/any23/extractor/rdf/BBC_News_Scotland.html");
        logger.debug(dumpModelToNQuads());
        assertStatementsSize(null, null, null, 2);
        // assertContains(RDFUtils.iri("http://example.org/example-manchestersyntax"), RDF.TYPE, OWL.ONTOLOGY);
        // assertContains(RDFUtils.iri("http://example.org/example-manchestersyntax#TestIndividual"), RDFS.COMMENT,
        // "Test individual is a unique individual");
    }

}
