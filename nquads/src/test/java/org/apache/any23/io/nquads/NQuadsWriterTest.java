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
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test case for {@link NQuadsWriter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsWriterTest {

    private static final Logger logger  = LoggerFactory.getLogger(NQuadsWriterTest.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private NQuadsWriter writer;

    private ValueFactory vf;
    
    @Before
    public void setUp() {
        vf = ValueFactoryImpl.getInstance();
        writer = new NQuadsWriter(baos);
    }

    @After
    public void tearDown() {
        logger.debug( "\n" + baos.toString() );
        baos.reset();
        writer = null;
    }

    @Test
    public void testWrite() throws RDFHandlerException {
        Statement s1 = quad(
                uri("http://sub"),
                uri("http://pre"),
                uri("http://obj"),
                uri("http://gra1")
        );
        Statement s2 = quad(
                bnode("1"),
                uri("http://pre"),
                bnode("2"),
                uri("http://gra2")
        );
        Statement s3 = quad(
                bnode("3"),
                uri("http://pre"),
                literal("Sample text 1"),
                uri("http://gra2")
        );
        Statement s4 = quad(
                bnode("4"),
                uri("http://pre"),
                literal("Sample text 2", "en"),
                uri("http://gra2")
        );
        Statement s5 = quad(
                bnode("5"),
                uri("http://pre"),
                literal("12345", uri("http://www.w3.org/2001/XMLSchema#integer")),
                uri("http://gra2")
        );
//        Statement s6 = quad(
//                uri("p1:sub"),
//                uri("p1:pre"),
//                uri("p1:obj"),
//                uri("p1:gra2")
//        );
        Statement s7 = quad(
                uri("http://sub"),
                uri("http://pre"),
                literal("This is line 1.\nThis is line 2.\n"),
                uri("http://gra3")
        );

        // Sending events.
        writer.startRDF();
        //writer.handleNamespace("p1", "http://test.com/");
        writer.handleStatement(s1);
        writer.handleStatement(s2);
        writer.handleStatement(s3);
        writer.handleStatement(s4);
        writer.handleStatement(s5);
        //writer.handleStatement(s6);
        writer.handleStatement(s7);
        writer.endRDF();

        // Checking content.
        String content = baos.toString();
        logger.info("output={}", content);
        String[] lines = content.split("\n");
        Assert.assertEquals("Unexpected number of lines.", 6, lines.length);
        Assert.assertTrue( lines[0].matches("<.*> <.*> <.*> <.*> \\.") );
        Assert.assertTrue( lines[1].matches("_:.* <.*> _:.* <.*> \\.") );
        Assert.assertTrue( lines[2].matches("_:.* <.*> \".*\" <.*> \\.") );
        Assert.assertTrue( lines[3].matches("_:.* <.*> \".*\"@en <.*> \\.") );
        Assert.assertTrue( lines[4].matches("_:.* <.*> \".*\"\\^\\^<.*> <.*> \\.") );
        //Assert.assertTrue( lines[5].matches("<http://.*> <http://.*> <http://.*> <http://.*> \\.") );
        Assert.assertEquals(
                "<http://sub> <http://pre> \"This is line 1.\\nThis is line 2.\\n\" <http://gra3> .",
                lines[5]
        );
    }

    @Test
    public void testReadWrite() throws RDFHandlerException, IOException, RDFParseException {
        NQuadsParser parser = new NQuadsParser();
        parser.setRDFHandler(writer);
        parser.parse(
            this.getClass().getClassLoader().getResourceAsStream("application/nquads/test2.nq"),
            "http://test.base.uri"
        );

        Assert.assertEquals("Unexpected number of lines.", 400, baos.toString().split("\n").length);
    }
    
    private Statement quad(Resource subject, URI predicate, Value object, Resource context) {
        return this.vf.createStatement(subject, predicate, object, context);
    }

    private URI uri(String uri) {
        return this.vf.createURI(uri);
    }

    private BNode bnode(String testID) {
        return this.vf.createBNode(testID);
    }

    private Literal literal(String literalValue) {
        return this.vf.createLiteral(literalValue);
    }

    private Literal literal(String literalValue, URI datatype) {
        return this.vf.createLiteral(literalValue, datatype);
    }

    private Literal literal(String literalValue, String language) {
        return this.vf.createLiteral(literalValue, language);
    }
}
