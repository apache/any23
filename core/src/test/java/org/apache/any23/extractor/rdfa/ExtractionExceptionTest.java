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

package org.apache.any23.extractor.rdfa;

import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractionResultImpl;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.writer.TripleHandler;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Assert;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Test case for {@link org.apache.any23.extractor.ExtractionException}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionExceptionTest {

    @Test
    public void testPrintStackTrace() throws ExtractionException, IOException {
        final String FAKE_EXTRACTOR_NAME = "fake-extractor-name";
        final Extractor extractor = mock(Extractor.class);
        final ExtractorDescription ed = mock(ExtractorDescription.class);
        when(ed.getExtractorName()).thenReturn(FAKE_EXTRACTOR_NAME);
        when(extractor.getDescription()).thenReturn(ed);

        final TripleHandler th = mock(TripleHandler.class);
        final ExtractionContext extractionContext = new ExtractionContext(
                extractor.getDescription().getExtractorName(),
                SimpleValueFactory.getInstance().createIRI("http://fake.document.uri")
        );
        final ExtractionResult er = new ExtractionResultImpl(extractionContext, extractor, th);
        er.notifyIssue(IssueReport.IssueLevel.FATAL  , "Fake fatal error.", 1, 2);
        er.notifyIssue(IssueReport.IssueLevel.ERROR  , "Fake error."      , 3, 4);
        er.notifyIssue(IssueReport.IssueLevel.WARNING, "Fake warning."    , 5, 6);

        ExtractionException ee = new ExtractionException("Fake message.", new RuntimeException("Fake cause"), er);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ee.printStackTrace(new PrintWriter(baos));
        final String bufferContent = baos.toString();
        Assert.assertTrue("Unexpected message content.", bufferContent.contains(FAKE_EXTRACTOR_NAME));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains("http://fake.document.uri"));
        Assert.assertTrue("Unexpected message content.", bufferContent.contains(
            ExtractionContext.ROOT_EXTRACTION_RESULT_ID
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
