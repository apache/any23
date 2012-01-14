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

package org.deri.any23.io.nquads;

import org.deri.any23.rdf.RDFUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test case for {@link org.deri.any23.io.nquads.NQuadsWriter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsWriterTest {

    private static final Logger logger  = LoggerFactory.getLogger(NQuadsWriterTest.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private NQuadsWriter writer;

    @Before
    public void setUp() {
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
        Statement s1 = RDFUtils.quad(
                RDFUtils.uri("http://sub"),
                RDFUtils.uri("http://pre"),
                RDFUtils.uri("http://obj"),
                RDFUtils.uri("http://gra1")
        );
        Statement s2 = RDFUtils.quad(
                RDFUtils.getBNode("1"),
                RDFUtils.uri("http://pre"),
                RDFUtils.getBNode("2"),
                RDFUtils.uri("http://gra2")
        );
        Statement s3 = RDFUtils.quad(
                RDFUtils.getBNode("3"),
                RDFUtils.uri("http://pre"),
                RDFUtils.literal("Sample text 1"),
                RDFUtils.uri("http://gra2")
        );
        Statement s4 = RDFUtils.quad(
                RDFUtils.getBNode("4"),
                RDFUtils.uri("http://pre"),
                RDFUtils.literal("Sample text 2", "en"),
                RDFUtils.uri("http://gra2")
        );
        Statement s5 = RDFUtils.quad(
                RDFUtils.getBNode("5"),
                RDFUtils.uri("http://pre"),
                RDFUtils.literal("12345", new URIImpl("http://www.w3.org/2001/XMLSchema#integer")),
                RDFUtils.uri("http://gra2")
        );
        Statement s6 = RDFUtils.quad(
                RDFUtils.uri("p1:sub"),
                RDFUtils.uri("p1:pre"),
                RDFUtils.uri("p1:obj"),
                RDFUtils.uri("p1:gra2")
        );
        Statement s7 = RDFUtils.quad(
                RDFUtils.uri("http://sub"),
                RDFUtils.uri("http://pre"),
                RDFUtils.literal("This is line 1.\nThis is line 2.\n"),
                RDFUtils.uri("http://gra3")
        );

        // Sending events.
        writer.startRDF();
        writer.handleNamespace("p1", "http://test.com/");
        writer.handleStatement(s1);
        writer.handleStatement(s2);
        writer.handleStatement(s3);
        writer.handleStatement(s4);
        writer.handleStatement(s5);
        writer.handleStatement(s6);
        writer.handleStatement(s7);
        writer.endRDF();

        // Checking content.
        String content = baos.toString();
        String[] lines = content.split("\n");
        Assert.assertEquals("Unexpected number of lines.", 7, lines.length);
        Assert.assertTrue( lines[0].matches("<.*> <.*> <.*> <.*> \\.") );
        Assert.assertTrue( lines[1].matches("_:.* <.*> _:.* <.*> \\.") );
        Assert.assertTrue( lines[2].matches("_:.* <.*> \".*\" <.*> \\.") );
        Assert.assertTrue( lines[3].matches("_:.* <.*> \".*\"@en <.*> \\.") );
        Assert.assertTrue( lines[4].matches("_:.* <.*> \".*\"\\^\\^<.*> <.*> \\.") );
        Assert.assertTrue( lines[5].matches("<http://.*> <http://.*> <http://.*> <http://.*> \\.") );
        Assert.assertEquals(
                "<http://sub> <http://pre> \"This is line 1.\\nThis is line 2.\\n\" <http://gra3> .",
                lines[6]
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

}
