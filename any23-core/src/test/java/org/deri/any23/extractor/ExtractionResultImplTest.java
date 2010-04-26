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

package org.deri.any23.extractor;

import junit.framework.Assert;
import org.deri.any23.extractor.html.TitleExtractor;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Test case for {@link org.deri.any23.extractor.ExtractionResultImpl} class.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class ExtractionResultImplTest {

    private static final URI TEST_URI = RDFHelper.uri("http://host/test/service");

    private ExtractionResultImpl extractionResult;
    private Extractor extractor;
    private TripleHandler mockTripleHandler;

    @Before
    public void setUp() {
        extractor = new TitleExtractor();
        mockTripleHandler = new FakeTripleHandler();
        extractionResult  = new ExtractionResultImpl(TEST_URI, extractor, mockTripleHandler );
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
        final ExtractionResult subExtractionResult = extractionResult.openSubResult("sub-id");
        notifyErrors(extractionResult);
        notifyErrors(subExtractionResult);

        assertContent(extractionResult);
        assertContent(subExtractionResult);
    }

    private void notifyErrors(ExtractionResult er) {
        er.notifyError(ExtractionResult.ErrorLevel.ERROR, "Error message"  , 1, 2);
        er.notifyError(ExtractionResult.ErrorLevel.WARN,  "Warning message", 3, 4);
        er.notifyError(ExtractionResult.ErrorLevel.FATAL, "Fatal message"  , 5, 6);
    }

    private void assertContent(ExtractionResult er) {
        Assert.assertEquals("Unexpected errors list size." , 3, er.getErrors().size() );
        assertOutputString(er, "ERROR");
        assertOutputString(er, "WARN");
        assertOutputString(er, "FATAL");
        assertOutputString(er, "errors: 3");
    }

    private void assertOutputString(ExtractionResult er, String s) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        er.printErrorsReport(ps);
        ps.flush();
        Assert.assertTrue( String.format("Cannot find string '%s' in output stream.", s), baos.toString().contains(s) );
    }

}
