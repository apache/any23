/**
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
 *
 */

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Test case for {@link org.deri.any23.extractor.html.TurtleHTMLExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TurtleHTMLExtractorTest extends AbstractExtractorTestCase {

    private static final Logger logger = LoggerFactory.getLogger(TurtleHTMLExtractor.class);

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return TurtleHTMLExtractor.factory;
    }

    /**
     * Tests the extraction of the RDF content from the sample HTML file.
     * 
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testExtraction() throws IOException, ExtractionException, RepositoryException {
        assertExtracts("html/html-turtle.html");
        logger.debug( dumpModelToRDFXML() );
        assertStatementsSize( null, (Value) null, 10);
    }

}
