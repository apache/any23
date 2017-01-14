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

import org.apache.any23.extractor.ExtractorFactory;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * Test case for {@link HeadLinkExtractor}
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HeadLinkExtractorTest extends AbstractExtractorTestCase {

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new HeadLinkExtractorFactory();
    }

    @Test
    public void testLinkExtraction() throws RepositoryException {
        assertExtract("/html/html-head-link-extractor.html");
        assertModelNotEmpty();
        final ValueFactory valueFactory = SimpleValueFactory.getInstance();
        final IRI externalLinkIRI = valueFactory.createIRI("http://www.myexperiment.org/workflows/16.rdf");
        assertContains(
                AbstractExtractorTestCase.baseIRI,
                valueFactory.createIRI("http://www.w3.org/1999/xhtml/vocab#alternate"),
                externalLinkIRI

        );
        assertContains(
                externalLinkIRI,
                valueFactory.createIRI("http://purl.org/dc/terms/title"),
                valueFactory.createLiteral("RDF+XML")

        );
        assertContains(
                externalLinkIRI,
                valueFactory.createIRI("http://purl.org/dc/terms/format"),
                valueFactory.createLiteral("application/rdf+xml")

        );
    }
}
