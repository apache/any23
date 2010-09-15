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

import org.deri.any23.extractor.DocumentContext;
import org.deri.any23.extractor.ErrorReporter;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.writer.TripleHandler;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Test case for {@link org.deri.any23.extractor.ExtractionException}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionExceptionTest {

    @Test
    public void testPrintStackTrace() throws ExtractionException, IOException {
        final Extractor extractor = mock(Extractor.class);
        final ExtractorDescription ed = mock(ExtractorDescription.class);
        when(ed.getExtractorName()).thenReturn("fake-extractor-name");
        when(extractor.getDescription()).thenReturn(ed);

        final DocumentContext dc = mock(DocumentContext.class);
        final TripleHandler th = mock(TripleHandler.class);
        final ExtractionResult er = new ExtractionResultImpl(dc, new URIImpl("http://fake.document.uri"), extractor, th);
        er.notifyError(ErrorReporter.ErrorLevel.FATAL, "Fake fatal error.", 1, 2);
        er.notifyError(ErrorReporter.ErrorLevel.ERROR, "Fake error."      , 3, 4);
        er.notifyError(ErrorReporter.ErrorLevel.WARN , "Fake warning."    , 5, 6);

        ExtractionException ee = new ExtractionException("Fake message.", new RuntimeException("Fake cause"), er);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ee.printStackTrace(new PrintWriter(baos));
        final String bufferContent = baos.toString();
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("fake-extractor-name"));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("http://fake.document.uri"));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains(
                Integer.toHexString(ExtractionResultImpl.ROOT_EXTRACTION_RESULT_ID.hashCode())
        ));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("Fake fatal error."));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("(1,2)"));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("Fake error."));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("(3,4)"));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("Fake warning."));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("(5,6)"));
        baos.close();
    }
}
