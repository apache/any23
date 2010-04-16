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

package org.deri.any23.extractor.rdfa;

import org.deri.any23.RDFHelper;
import org.junit.Test;

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.html.AbstractExtractorTestCase;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.repository.RepositoryException;

/**
 * Reference Test Class for {@link org.deri.any23.extractor.rdfa.RDFaExtractor}.
 */
public class RDFaExtractorTest extends AbstractExtractorTestCase {

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return RDFaExtractor.factory;
    }

    @Test
    public void testSimple() throws RepositoryException {
        assertExtracts("html/rdfa/dummy.html");
        assertContains(null, DCTERMS.creator, RDFHelper.literal("Alice", "en") );
        assertContains(null, DCTERMS.title  , RDFHelper.literal("The trouble with Bob", "en") );
    }

    /**
     * Tests that the default parser settings enable tolerance in data type parsing.
     */
    // TODO: investigate on this issue. @Test
    public void testTolerantParsing() {
        assertExtracts("html/rdfa/oreilly-invalid-datatype.html");
    }

}
