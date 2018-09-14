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

package org.apache.any23.writer;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class TripleFormatTest {

    @Test
    public void testRdf4jRoundTripping() {

        RDFFormat[] formats = {
                RDFFormat.TRIX, RDFFormat.NQUADS, RDFFormat.RDFA, RDFFormat.TRIG,
                RDFFormat.N3, RDFFormat.RDFXML, RDFFormat.TURTLE, RDFFormat.JSONLD,
                RDFFormat.NTRIPLES, RDFFormat.BINARY, RDFFormat.RDFJSON
        };

        for (RDFFormat expected : formats) {
            TripleFormat tf = TripleFormat.of(expected);

            RDFFormat actual = tf.toRDFFormat();
            assertSame(expected, actual);

            tf.rdfFormat = null;
            actual = tf.toRDFFormat();
            assertNotSame(expected, actual);

            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getStandardURI(), actual.getStandardURI());
            assertEquals(expected.getCharset(), actual.getCharset());
            assertEquals(expected.getFileExtensions(), actual.getFileExtensions());
            assertEquals(expected.supportsContexts(), actual.supportsContexts());
            assertEquals(expected.supportsNamespaces(), actual.supportsNamespaces());
        }

    }
}
