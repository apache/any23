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

package org.apache.any23.extractor;

import org.junit.Assert;
import org.apache.any23.extractor.html.TitleExtractor;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.eclipse.rdf4j.model.IRI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Test case for {@link ExtractionResultImpl} class.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class ExtractionResultImplTest {

    private static final IRI TEST_IRI = RDFUtils.iri("http://host/test/service");

    private ExtractionResultImpl extractionResult;
    private Extractor extractor;
    private TripleHandler mockTripleHandler;

    @Before
    public void setUp() {
        extractor = new TitleExtractor();
        mockTripleHandler = Mockito.mock(TripleHandler.class);
        extractionResult  = new ExtractionResultImpl(
                new ExtractionContext("test-extractor-name", TEST_IRI),
                extractor,
                mockTripleHandler
        );
    }

    @After
    public void tearDown() throws TripleHandlerException {
        extractionResult.close();
        mockTripleHandler.close();
        extractor         = null;
        mockTripleHandler = null;
        extractionResult  = null;
    }

    @Test
    public void testNotifyErrors() throws IOException {
        notifyErrors(extractionResult);
        assertContent(extractionResult, 3);

        final ExtractionResult subExtractionResult = extractionResult.openSubResult(
                new ExtractionContext("sub-id", RDFUtils.iri("http://sub/uri") )
        );

        notifyErrors(subExtractionResult);
        assertContent(subExtractionResult, 6);
    }

    private void notifyErrors(ExtractionResult er) {
        er.notifyIssue(IssueReport.IssueLevel.ERROR  , "Error message"  , 1, 2);
        er.notifyIssue(IssueReport.IssueLevel.WARNING, "Warning message", 3, 4);
        er.notifyIssue(IssueReport.IssueLevel.FATAL  , "Fatal message"  , 5, 6);
    }

    private void assertContent(ExtractionResult er, int errorCount) {
        Assert.assertEquals("Unexpected errors list size." , errorCount, er.getIssues().size() );
        assertOutputString(er, IssueReport.IssueLevel.ERROR.toString());
        assertOutputString(er, IssueReport.IssueLevel.WARNING.toString());
        assertOutputString(er, IssueReport.IssueLevel.FATAL.toString());
        assertOutputString(er, "errors: " + errorCount);
    }

    private void assertOutputString(ExtractionResult er, String s) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        er.printReport(ps);
        ps.flush();
        Assert.assertTrue( String.format("Cannot find string '%s' in output stream.", s), baos.toString().contains(s) );
    }

}
