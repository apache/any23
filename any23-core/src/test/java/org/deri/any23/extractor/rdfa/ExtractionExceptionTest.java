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

package org.deri.any23.extractor.rdfa;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Test case for {@link org.deri.any23.extractor.ExtractionException}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionExceptionTest {

    @Test
    public void testPrintStackTrace() throws ExtractionException, IOException {
        final ExtractionContext ec = new ExtractionContext(
                "fake-extractor-name",
                new URIImpl("http://fake.document.uri"), 
                "fake-local-id"
        );
        try {
            throw new ExtractionException("Fake message.", new RuntimeException("Fake cause"), ec );
        } catch (ExtractionException ee) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ee.printStackTrace( new PrintWriter(baos) );
            final String bufferContent = baos.toString();
            Assert.assertTrue("Unexpected message content.", bufferContent.contains("fake-extractor-name"));
            Assert.assertTrue("Unexpected message content.", bufferContent.contains("http://fake.document.uri"));
            Assert.assertTrue("Unexpected message content.", bufferContent.contains("fake-local-id"));
            baos.close();
        }
    }

}
