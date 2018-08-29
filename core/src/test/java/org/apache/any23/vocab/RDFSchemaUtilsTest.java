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

package org.apache.any23.vocab;

import org.apache.any23.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test case for {@link RDFSchemaUtils}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFSchemaUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(RDFSchemaUtilsTest.class);

    /**
     * Test case for
     * {@link RDFSchemaUtils#serializeVocabularies(
     * org.eclipse.rdf4j.rio.RDFFormat, java.io.PrintStream)} with <i>NTriples</i> format.
     */
    @Test
    public void testSerializeVocabulariesNTriples() {
        serializeVocabularies(RDFFormat.NTRIPLES, 2178);
    }

    /**
     * Test case for
     * {@link RDFSchemaUtils#serializeVocabularies(
     * org.eclipse.rdf4j.rio.RDFFormat, java.io.PrintStream)} with <i>RDFXML</i> format.
     */
    @Test
    public void testSerializeVocabulariesRDFXML() {
        serializeVocabularies(RDFFormat.RDFXML, 5709); // Effective lines + separators.
    }

    private void serializeVocabularies(RDFFormat format, int expectedLines) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos)) {
            RDFSchemaUtils.serializeVocabularies(format, ps);
        }
        final String output = baos.toString();
        logger.debug(output);
        final int occurrences = StringUtils.countOccurrences(output, "\n");
        Assert.assertEquals(expectedLines, occurrences);
    }

}
