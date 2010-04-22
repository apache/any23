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

package org.deri.any23.parser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.ParseLocationListener;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Test case for {@link org.deri.any23.parser.NQuadsParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsParserTest {

    private static final Logger logger = LoggerFactory.getLogger(NQuadsParser.class);

    private NQuadsParser parser;

    @Before
    public void setUp() {
        parser = new NQuadsParser();
        parser.setVerifyData(true);
        parser.setDatatypeHandling(RDFParser.DatatypeHandling.VERIFY);
        parser.setStopAtFirstError(true);
    }

    @After
    public void tearDown() {
        parser = null;
    }

    @Test
    public void testParserWithAllScenarios()
    throws IOException, RDFParseException, RDFHandlerException {

        TestParseLocationListener parseLocationListerner = new TestParseLocationListener();
        SpecificTestRDFHandler rdfHandler = new SpecificTestRDFHandler();
        parser.setParseLocationListener(parseLocationListerner);
        parser.setRDFHandler(rdfHandler);

        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        this.getClass().getClassLoader().getResourceAsStream("application/nquads/test1.nq")
                ) 
        );
        parser.parse(
            br,
            "http://test.base.uri"
        );

        rdfHandler.assertHandler(5);
        parseLocationListerner.assertListener(7, 108);
    }

    @Test
    public void testParserWithRealData()
    throws IOException, RDFParseException, RDFHandlerException {

        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        TestRDFHandler rdfHandler = new TestRDFHandler();
        parser.setParseLocationListener(parseLocationListener);
        parser.setRDFHandler(rdfHandler);

        parser.parse(
            this.getClass().getClassLoader().getResourceAsStream("application/nquads/test2.nq"),
            "http://test.base.uri"
        );

        rdfHandler.assertHandler(400);
        parseLocationListener.assertListener(400, 349);
    }

    private class TestParseLocationListener implements ParseLocationListener {

        private int lastRow, lastCol;

        public void parseLocationUpdate(int r, int c) {
            lastRow = r;
            lastCol = c;
        }

        private void assertListener(int row, int col) {
            Assert.assertEquals("Unexpected last row", row , lastRow);
            Assert.assertEquals("Unexpected last col", col , lastCol);
        }

    }

    private class TestRDFHandler implements RDFHandler {

        private boolean started = false;
        private boolean ended   = false;

        private int statements;

        protected int getStatements() {
            return statements;
        }

        public void startRDF() throws RDFHandlerException {
            started = true;
        }

        public void endRDF() throws RDFHandlerException {
            ended = true;
        }

        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void handleStatement(Statement statement) throws RDFHandlerException {
            logger.info(statement.toString());
            statements++;
        }

        public void handleComment(String s) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void assertHandler(int expected) {
            Assert.assertTrue("Never stated.", started);
            Assert.assertTrue("Never ended." , ended  );
            Assert.assertEquals("Unexpected number of statements.", expected, statements);
        }
    }

    private class SpecificTestRDFHandler extends TestRDFHandler {

        public void handleStatement(Statement statement) throws RDFHandlerException {
            int statements = getStatements();
            if(statements == 0){
                Assert.assertEquals(new URIImpl("http://example.org/alice/foaf.rdf#me"), statement.getSubject() );

            } else {
                Assert.assertTrue(statement.getSubject() instanceof BNode);
            }
            if( statements == 5 ) {
                Assert.assertEquals(new URIImpl("http://test.base.uri#like"), statement.getPredicate() );
            }
            Assert.assertEquals(
                    new URIImpl( String.format("http://example.org/alice/foaf%s.rdf", statements + 1) ),
                    statement.getContext()
            );

            super.handleStatement(statement);
        }
    }


}
