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
import org.deri.any23.RDFHelper;
import org.deri.any23.extractor.html.TitleExtractor;
import org.deri.any23.writer.TripleHandler;
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

    private ByteArrayOutputStream baos;
    private PrintStream ps;

    @Before
    public void setUp() {
        if(baos != null) { baos.reset(); }
        extractor = new TitleExtractor();
        mockTripleHandler = new FakeTripleHandler();
        extractionResult  = new ExtractionResultImpl(TEST_URI, extractor, mockTripleHandler );
    }

    @After
    public void tearDown() {
        extractionResult.close();
        mockTripleHandler.close();
        extractor         = null;
        mockTripleHandler = null;
        extractionResult  = null;
    }

    @Test
    public void testNotifyErrors() throws IOException {
        notifyErrors(extractionResult);
        ExtractionResult subExtractionResult = extractionResult.openSubResult("sub-id");
        notifyErrors(subExtractionResult);

        Assert.assertEquals("Unespected number of errors.", 6, extractionResult.getErrorsCount()   );
        Assert.assertEquals("Unspected errors list size." , 6, extractionResult.getErrors().size() );

        extractionResult.printErrorsReport( getPrintStream() );
        assertOutputString("ERROR");
        assertOutputString("WARN");
        assertOutputString("FATAL");
        assertOutputString("errors: 6");
    }

    private void notifyErrors(ExtractionResult er) {
        extractionResult.notifyError(ExtractionResult.ErrorLevel.ERROR, "Error message"  , 1, 2);
        extractionResult.notifyError(ExtractionResult.ErrorLevel.WARN,  "Warning message", 3, 4);
        extractionResult.notifyError(ExtractionResult.ErrorLevel.FATAL, "Fatal message"  , 5, 6);
    }

    private PrintStream getPrintStream() {
        if(baos == null) {
            baos = new ByteArrayOutputStream();
            ps   = new PrintStream(baos);
        }
        return ps;
    }

    private void assertOutputString(String s) {
        ps.flush();
        Assert.assertTrue( String.format("Cannot find string '%s' in output stream.", s), baos.toString().contains(s) );
    }

}
