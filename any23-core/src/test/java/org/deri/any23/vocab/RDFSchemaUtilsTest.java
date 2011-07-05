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

package org.deri.any23.vocab;

import org.deri.any23.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * Test case for {@link RDFSchemaUtils}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFSchemaUtilsTest {

    /**
     * Test case for {@link org.deri.any23.vocab.RDFSchemaUtils#serializeVocabulariesToNQuads(java.io.OutputStream)}
     */
    @Test
    public void testSerializeVocabularies() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RDFSchemaUtils.serializeVocabulariesToNQuads(baos);
        final String output = baos.toString();
        System.out.println(output);
        final int occurrences= StringUtils.countOccurrences(output, "\n");
        Assert.assertEquals(802, occurrences);
    }

}
