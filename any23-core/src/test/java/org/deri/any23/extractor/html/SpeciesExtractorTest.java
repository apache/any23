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

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.WO;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference test class for {@link org.deri.any23.extractor.html.SpeciesExtractor}.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class SpeciesExtractorTest extends AbstractExtractorTestCase {

    private static final WO vWO = WO.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(SpeciesExtractorTest.class);

    protected ExtractorFactory<?> getExtractorFactory() {
        return SpeciesExtractor.factory;
    }

    /**
     * Test the beahvior against two <a href="http://en.wikipedia.org/wiki/Template:Taxobox">Wikipedia Taxobox</a>.
     *
     * @throws RepositoryException
     */
    @Test
    public void testSpeciesMicroformatExtractOverTaxoBox() throws RepositoryException {
        assertExtracts("microformats/species/species-example-2.html");
        assertModelNotEmpty();
        logger.debug(dumpModelToRDFXML());

        /**
         * here I expect two species
         */
        assertStatementsSize(null, vWO.getProperty("species"), 2);

        /**
         * overall triples amount
         */
        assertStatementsSize(null, (Value) null, 29);
    }

}