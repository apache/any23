/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test case for {@link org.deri.any23.extractor.rdf.NTriplesExtractor}.
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
     * @throws IOException
     * @throws ExtractionException
     * @throws TripleHandlerException
     */
    @Test
    public void testTypedLiteralIncompatibleValueSupport()
    throws IOException, ExtractionException, TripleHandlerException {
        final URI uri = RDFHelper.uri("http://host.com/test-malformed-literal.turtle");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TripleHandler th = new RDFXMLWriter(baos);
        ExtractionResult result = new ExtractionResultImpl(uri, extractor, th);
        extractor.setStopAtFirstError(false);
        try {
            extractor.run(
                    this.getClass().getResourceAsStream("/org/deri/any23/extractor/rdf/testMalformedLiteral"),
                    uri,
                    result
            );
        } finally {
            logger.info(baos.toString());
            th.close();
            result.close();
        }
    }

}
