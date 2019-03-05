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
import org.apache.any23.vocab.WO;
import org.junit.Test;
import org.eclipse.rdf4j.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference test class for {@link SpeciesExtractor}.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class SpeciesExtractorTest extends AbstractExtractorTestCase {

    private static final WO vWO = WO.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(SpeciesExtractorTest.class);

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new SpeciesExtractorFactory();
    }

    /**
     * Test the beahvior against two <a href="http://en.wikipedia.org/wiki/Template:Taxobox">Wikipedia Taxobox</a>.
     *
     * @throws Exception if there is an error asserting the test data.
     */
    @Test
    public void testSpeciesMicroformatExtractOverTaxoBox() throws Exception {
        assertExtract("/microformats/species/species-example-2.html");
        assertModelNotEmpty();
        logger.debug(dumpModelToRDFXML());

        /**
         * here I expect two species
         */
        assertStatementsSize(null, vWO.getProperty("species"), 2);

        /**
         * overall triples amount
         */
        assertStatementsSize(null, (Value) null, 27);
    }

}