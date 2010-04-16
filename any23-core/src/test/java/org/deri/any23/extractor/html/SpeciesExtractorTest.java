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

package org.deri.any23.extractor.html;

import junit.framework.Assert;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.WO;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference test class for {@link org.deri.any23.extractor.html.SpeciesExtractor}.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class SpeciesExtractorTest extends AbstractExtractorTestCase {

    private static final Logger logger = LoggerFactory.getLogger(SpeciesExtractorTest.class);

    protected ExtractorFactory<?> getExtractorFactory() {
        return SpeciesExtractor.factory;
    }

    /**
     * Test the behaviour of the extractor aginst the reference <i>HTML</i>
     * <a href="http://www.westmidlandbirdclub.com/records/lists-2004.htm">test page</a>. 
     * @throws RepositoryException
     */
    @Test
    public void testSpeciesMicroformatExtract() throws RepositoryException {
        assertExtracts("microformats/species/species-example-1.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, WO.species);
        RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
        int i = 0;
        try {
            while (result.hasNext()) {
                i++;
                Statement statement = result.next();
                logger.info(String.format("extracted triple: %s", statement));
            }
        } finally {
            result.close();
        }
        Assert.assertTrue(i == 2185);
    }

}