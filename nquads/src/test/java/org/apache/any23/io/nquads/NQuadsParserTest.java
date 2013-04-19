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

package org.apache.any23.io.nquads;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.ParseLocationListener;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;

/**
 * Test case for {@link NQuadsParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsParserTest {

    private static final Logger logger = LoggerFactory.getLogger(NQuadsParser.class);

    private NQuadsParser parser;

    private TestRDFHandler rdfHandler;

    @Before
    public void setUp() {
        parser = new NQuadsParser();
        rdfHandler = new TestRDFHandler();
        parser.setRDFHandler(rdfHandler);
        Set<RioSetting<?>> nonFatalErrors = new HashSet<RioSetting<?>>();
        parser.getParserConfig().setNonFatalErrors(nonFatalErrors);
        parser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
        parser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
        parser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
        parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
        parser.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
        parser.getParserConfig().addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);
        
    }

    @After
    public void tearDown() {
        parser = null;
    }

    /**
     * Tests the correct behavior with incomplete input.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test(expected = RDFParseException.class)
    public void testIncompleteParsing() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                "<http://s> <http://p> <http://o> <http://g>".getBytes()
        );
        parser.parse(bais, "http://base-uri");
    }

    /**
     * Tests parsing of empty lines and comments.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testParseEmptyLinesAndComments() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "  \n\n\n# This is a comment\n\n#this is another comment."
            .getBytes()
        );
        parser.parse(bais, "http://test.base.uri");
        Assert.assertEquals(rdfHandler.getStatements().size(), 0);
    }

    /**
     * Tests basic N-Quads parsing.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasic() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://www.v/dat/4b> <http://www.w3.org/20/ica#dtend> <http://sin/value/2> <http://sin.siteserv.org/def/>."
            .getBytes()
        );
        parser.parse(bais, "http://test.base.uri");
        Assert.assertThat(rdfHandler.getStatements().size(), is(1));
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertEquals("http://www.v/dat/4b", statement.getSubject().stringValue());
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof URI);
        Assert.assertEquals("http://sin/value/2", statement.getObject().stringValue());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests basic N-Quads parsing with blank node.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicBNode() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "_:a123456768 <http://www.w3.org/20/ica#dtend> <http://sin/value/2> <http://sin.siteserv.org/def/>."
            .getBytes()
        );
        parser.parse(bais, "http://test.base.uri");
        Assert.assertThat(rdfHandler.getStatements().size(), is(1));
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertTrue(statement.getSubject() instanceof BNode);
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof URI);
        Assert.assertEquals("http://sin/value/2", statement.getObject().stringValue());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests basic N-Quads parsing with literal.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicLiteral() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "_:a123456768 <http://www.w3.org/20/ica#dtend> \"2010-05-02\" <http://sin.siteserv.org/def/>."
            .getBytes()
        );
        parser.parse(bais, "http://test.base.uri");
        Assert.assertThat(rdfHandler.getStatements().size(), is(1));
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertTrue(statement.getSubject() instanceof BNode);
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof Literal);
        Assert.assertEquals("2010-05-02", statement.getObject().stringValue());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests N-Quads parsing with literal and language.
     * 
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicLiteralLang() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://www.v/dat/4b2-21> <http://www.w3.org/20/ica#dtend> \"2010-05-02\"@en <http://sin.siteserv.org/def/>."
            .getBytes()
        );
        parser.parse(bais, "http://test.base.uri");
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertEquals("http://www.v/dat/4b2-21", statement.getSubject().stringValue());
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof Literal);
        Literal object = (Literal) statement.getObject();
        Assert.assertEquals("2010-05-02", object.stringValue());
        Assert.assertEquals("en", object.getLanguage());
        Assert.assertNull("en", object.getDatatype());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests N-Quads parsing with literal and datatype.
     * 
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testParseBasicLiteraDatatype() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            ("<http://www.v/dat/4b2-21> " +
             "<http://www.w3.org/20/ica#dtend> " +
             "\"2010\"^^<http://www.w3.org/2001/XMLSchema#integer> " +
             "<http://sin.siteserv.org/def/>."
            ).getBytes()
        );
        parser.parse(bais, "http://test.base.uri");
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertEquals("http://www.v/dat/4b2-21", statement.getSubject().stringValue());
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof Literal);
        Literal object = (Literal) statement.getObject();
        Assert.assertEquals("2010", object.stringValue());
        Assert.assertNull(object.getLanguage());
        Assert.assertEquals("http://www.w3.org/2001/XMLSchema#integer", object.getDatatype().toString());
        Assert.assertEquals("http://sin.siteserv.org/def/", statement.getContext().stringValue());
    }

    /**
     * Tests the correct support for literal escaping.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testLiteralEscapeManagement1()
    throws RDFHandlerException, IOException, RDFParseException {
        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        parser.setParseLocationListener(parseLocationListener);

        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://a> <http://b> \"\\\\\" <http://c> .".getBytes()
        );
        parser.parse(bais, "http://base-uri");

        rdfHandler.assertHandler(1);
        //parseLocationListener.assertListener(1, 40);
        parseLocationListener.assertListener(1, 1);
    }

    /**
     * Tests the correct support for literal escaping.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testLiteralEscapeManagement2()
    throws RDFHandlerException, IOException, RDFParseException {
        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        parser.setParseLocationListener(parseLocationListener);

        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://a> <http://b> \"Line text 1\\nLine text 2\" <http://c> .".getBytes()
        );
        parser.parse(bais, "http://base-uri");

        rdfHandler.assertHandler(1);
        final Value object = rdfHandler.getStatements().get(0).getObject();
        Assert.assertTrue( object instanceof Literal);
        final String literalContent = ((Literal) object).getLabel();
        Assert.assertEquals("Line text 1\nLine text 2", literalContent);
    }

    /**
     * Tests the correct decoding of UTF-8 encoded chars in URIs.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testURIDecodingManagement() throws RDFHandlerException, IOException, RDFParseException {
        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        parser.setParseLocationListener(parseLocationListener);

        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://s/\\u306F\\u3080> <http://p/\\u306F\\u3080> <http://o/\\u306F\\u3080> <http://g/\\u306F\\u3080> ."
            .getBytes()
        );
        parser.parse(bais, "http://base-uri");

        rdfHandler.assertHandler(1);
        final Statement statement = rdfHandler.getStatements().get(0);

        final Resource subject = statement.getSubject();
        Assert.assertTrue( subject instanceof URI);
        final String subjectURI = subject.toString();
        Assert.assertEquals("http://s/はむ", subjectURI);

        final Resource predicate = statement.getPredicate();
        Assert.assertTrue( predicate instanceof URI);
        final String predicateURI = predicate.toString();
        Assert.assertEquals("http://p/はむ", predicateURI);

        final Value object = statement.getObject();
        Assert.assertTrue( object instanceof URI);
        final String objectURI = object.toString();
        Assert.assertEquals("http://o/はむ", objectURI);

        final Resource graph = statement.getContext();
        Assert.assertTrue( graph instanceof URI);
        final String graphURI = graph.toString();
        Assert.assertEquals("http://g/はむ", graphURI);
    }

    @Test
    public void testUnicodeLiteralManagement() throws RDFHandlerException, IOException, RDFParseException {
        final String INPUT_LITERAL = "[は、イギリスおよびイングランドの首都である] [是大不列顛及北愛爾蘭聯合王國和英格蘭的首都]";
        final String INPUT_STRING = String.format(
                "<http://a> <http://b> \"%s\" <http://c> .",
                INPUT_LITERAL
        );
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            INPUT_STRING.getBytes()
        );
        parser.parse(bais, "http://base-uri");

        rdfHandler.assertHandler(1);
        final Literal obj = (Literal) rdfHandler.getStatements().get(0).getObject();
        Assert.assertEquals(INPUT_LITERAL, obj.getLabel());
    }

    @Test
    public void testUnicodeLiteralDecoding() throws RDFHandlerException, IOException, RDFParseException {
        final String INPUT_LITERAL_PLAIN   = "[は]";
        final String INPUT_LITERAL_ENCODED = "[\\u306F]";
        final String INPUT_STRING = String.format(
                "<http://a> <http://b> \"%s\" <http://c> .",
                INPUT_LITERAL_ENCODED
        );
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            INPUT_STRING.getBytes()
        );
        parser.parse(bais, "http://base-uri");

        rdfHandler.assertHandler(1);
        final Literal obj = (Literal) rdfHandler.getStatements().get(0).getObject();
        Assert.assertEquals(INPUT_LITERAL_PLAIN, obj.getLabel());
    }

    @Test(expected = RDFParseException.class)
    public void testWrongUnicodeEncodedCharFail() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                "<http://s> <http://p> \"\\u123X\" <http://g> .".getBytes()
        );
        parser.parse(bais, "http://base-uri");
    }

    /**
     * Tests the correct support for EOS exception.
     *
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test(expected = RDFParseException.class)
    public void testEndOfStreamReached()
    throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            "<http://a> <http://b> \"\\\" <http://c> .".getBytes()
        );
        parser.parse(bais, "http://base-uri");
    }

    /**
     * Tests the parser with all cases defined by the NQuads grammar.
     *
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    @Test
    public void testFullParseScenario()
    throws IOException, RDFParseException, RDFHandlerException {
        TestParseLocationListener parseLocationListerner = new TestParseLocationListener();
        FullParseScenarioRDFHandler rdfHandler = new FullParseScenarioRDFHandler();
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

        rdfHandler.assertHandler(6);
        parseLocationListerner.assertListener(8, 71);
    }

    /**
     * Tests parser with real data.
     *
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    @Test
    public void testParseRealData()
    throws IOException, RDFParseException, RDFHandlerException {
        TestParseLocationListener parseLocationListener = new TestParseLocationListener();
        parser.setParseLocationListener(parseLocationListener);

        parser.parse(
            this.getClass().getClassLoader().getResourceAsStream("application/nquads/test2.nq"),
            "http://test.base.uri"
        );

        rdfHandler.assertHandler(400);
        parseLocationListener.assertListener(400, 349);
    }

    @Test
    public void testStatementWithInvalidLiteralContentAndIgnoreValidation()
    throws RDFHandlerException, IOException, RDFParseException {
        verifyStatementWithInvalidLiteralContent(RDFParser.DatatypeHandling.IGNORE);
    }

    @Test(expected = RDFParseException.class)
    public void testStatementWithInvalidLiteralContentAndStrictValidation()
    throws RDFHandlerException, IOException, RDFParseException {
        verifyStatementWithInvalidLiteralContent(RDFParser.DatatypeHandling.VERIFY);
    }

    @Test
    public void testStatementWithInvalidDatatypeAndIgnoreValidation()
    throws RDFHandlerException, IOException, RDFParseException {
        verifyStatementWithInvalidDatatype(RDFParser.DatatypeHandling.IGNORE);
    }

    @Test(expected = RDFParseException.class)
    public void testStatementWithInvalidDatatypeAndVerifyValidation()
    throws RDFHandlerException, IOException, RDFParseException {
        verifyStatementWithInvalidDatatype(RDFParser.DatatypeHandling.VERIFY);
    }

    @Test (expected = RDFParseException.class)
    public void testStopAtFirstErrorStrictParsing() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                (
                    "<http://s0> <http://p0> <http://o0> <http://g0> .\n" +
                    "<http://sX>                                     .\n" + // Line with error.
                    "<http://s1> <http://p1> <http://o1> <http://g1> .\n"
                ).getBytes()
        );
        parser.setStopAtFirstError(true);
        parser.parse(bais, "http://base-uri");
    }

    @Test
    public void testStopAtFirstErrorTolerantParsing() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                (
                    "<http://s0> <http://p0> <http://o0> <http://g0> .\n" +
                    "<http://sX>                                     .\n" + // Line with error.
                    "<http://s1> <http://p1> <http://o1> <http://g1> .\n"
                ).getBytes()
        );
        parser.setStopAtFirstError(false);
        parser.parse(bais, "http://base-uri");
        rdfHandler.assertHandler(2);
        final List<Statement> statements = rdfHandler.getStatements();
        final int size = statements.size();
        for(int i = 0; i < size; i++) {
            Assert.assertEquals("http://s" + i, statements.get(i).getSubject().stringValue()  );
            Assert.assertEquals("http://p" + i, statements.get(i).getPredicate().stringValue());
            Assert.assertEquals("http://o" + i, statements.get(i).getObject().stringValue()   );
            Assert.assertEquals("http://g" + i, statements.get(i).getContext().stringValue()  );
        }
    }

    @Test
    public void testReportInvalidLiteralAttribute() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                "<http://a> <http://b> \"literal\"^^xsd:datetime <http://c> .".getBytes()
        );
        try {
            parser.parse(bais, "http://base-uri");
            Assert.fail("Expected failure here.");
        } catch (RDFParseException e) {
            Assert.assertTrue(e.getMessage().contains("Expected '<'"));
            Assert.assertEquals(1 , e.getLineNumber());
            //Assert.assertEquals(35, e.getColumnNumber());
        }
    }

    @Test
    public void testParseWithNoContext() throws RDFHandlerException, IOException, RDFParseException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
            ("<http://www.v/dat/4b2-21>" +
             "<http://www.w3.org/20/ica#dtend>" +
             "\"2010\"^^<http://www.w3.org/2001/XMLSchema#integer> ."
            ).getBytes()
        );
        parser.parse(bais, "http://test.base.uri");
        final Statement statement = rdfHandler.getStatements().get(0);
        Assert.assertEquals("http://www.v/dat/4b2-21", statement.getSubject().stringValue());
        Assert.assertEquals("http://www.w3.org/20/ica#dtend", statement.getPredicate().stringValue());
        Assert.assertTrue(statement.getObject() instanceof Literal);
        Literal object = (Literal) statement.getObject();
        Assert.assertEquals("2010", object.stringValue());
        Assert.assertNull(object.getLanguage());
        Assert.assertEquals("http://www.w3.org/2001/XMLSchema#integer", object.getDatatype().toString());
        Assert.assertNull(statement.getContext());
    }

    private void verifyStatementWithInvalidLiteralContent(RDFParser.DatatypeHandling datatypeHandling)
    throws RDFHandlerException, IOException, RDFParseException {
       final ByteArrayInputStream bais = new ByteArrayInputStream(
                (
                "<http://dbpedia.org/resource/Camillo_Benso,_conte_di_Cavour> " +
                "<http://dbpedia.org/property/mandatofine> " +
                "\"1380.0\"^^<http://www.w3.org/2001/XMLSchema#int> " + // Float declared as int.
                "<http://it.wikipedia.org/wiki/Camillo_Benso,_conte_di_Cavour#absolute-line=20> ."
                ).getBytes()
        );
        if(datatypeHandling == RDFParser.DatatypeHandling.VERIFY) {
            parser.getParserConfig().setNonFatalErrors(new HashSet<RioSetting<?>>());
            parser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
            parser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);
        }
        //parser.setDatatypeHandling(datatypeHandling);
        parser.parse(bais, "http://base-uri");
    }

    private void verifyStatementWithInvalidDatatype(RDFParser.DatatypeHandling datatypeHandling)
    throws RDFHandlerException, IOException, RDFParseException {
        parser.setDatatypeHandling(datatypeHandling);
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                (
                        "<http://dbpedia.org/resource/Camillo_Benso,_conte_di_Cavour> " +
                        "<http://dbpedia.org/property/mandatofine> " +
                        "\"1380.0\"^^<http://dbpedia.org/datatype/second> " +
                        "<http://it.wikipedia.org/wiki/Camillo_Benso,_conte_di_Cavour#absolute-line=20> ."
                ).getBytes()
        );
        if(datatypeHandling == RDFParser.DatatypeHandling.VERIFY) {
            parser.getParserConfig().setNonFatalErrors(new HashSet<RioSetting<?>>());
            parser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
            parser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);
        }
        parser.parse(bais, "http://base-uri");
        rdfHandler.assertHandler(1);
    }

    private class TestParseLocationListener implements ParseLocationListener {

        private int lastRow, lastCol;

        public void parseLocationUpdate(int r, int c) {
            lastRow = r;
            lastCol = c;
        }

        private void assertListener(int row, int col) {
            Assert.assertEquals("Unexpected last row", row , lastRow);
            // Column numbers are not supported by the Rio NQuadsParser currently
            //Assert.assertEquals("Unexpected last col", col , lastCol);
        }

    }

    private class TestRDFHandler implements RDFHandler {

        private boolean started = false;
        private boolean ended   = false;

        private final List<Statement> statements = new ArrayList<Statement>();

        protected List<Statement> getStatements() {
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
            logger.debug(statement.toString());
            statements.add(statement);
        }

        public void handleComment(String s) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void assertHandler(int expected) {
            Assert.assertTrue("Never stated.", started);
            Assert.assertTrue("Never ended." , ended  );
            Assert.assertEquals("Unexpected number of statements.", expected, statements.size());
        }
    }

    private class FullParseScenarioRDFHandler extends TestRDFHandler {

        public void handleStatement(Statement statement) throws RDFHandlerException {
            int statementIndex = getStatements().size();
            if(statementIndex == 0){
                Assert.assertEquals(new URIImpl("http://example.org/alice/foaf.rdf#me"), statement.getSubject() );
            } else {
                Assert.assertTrue(statement.getSubject() instanceof BNode);
            }

            if( statementIndex == 4) {
                Assert.assertEquals(new URIImpl("http://example.org/#like"), statement.getPredicate() );
            }

            if(statementIndex == 5) {
                Assert.assertNull(statement.getContext());
            } else {
                Assert.assertEquals(
                        new URIImpl(String.format("http://example.org/alice/foaf%s.rdf", statementIndex + 1)),
                        statement.getContext()
                );
            }

            super.handleStatement(statement);
        }
    }

}