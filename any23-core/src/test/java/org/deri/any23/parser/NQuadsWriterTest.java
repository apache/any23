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

import org.deri.any23.util.RDFHelper;
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
 * Test case for {@link org.deri.any23.parser.NQuadsWriter}.
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
        logger.info( "\n" + baos.toString() );
        baos.reset();
        writer = null;
    }

    @Test
    public void testWrite() throws RDFHandlerException {
        Statement s1 = RDFHelper.quad(
                RDFHelper.uri("http://sub"),
                RDFHelper.uri("http://pre"),
                RDFHelper.uri("http://obj"),
                RDFHelper.uri("http://gra1")
        );
        Statement s2 = RDFHelper.quad(
                RDFHelper.getBNode("1"),
                RDFHelper.uri("http://pre"),
                RDFHelper.getBNode("2"),
                RDFHelper.uri("http://gra2")
        );
        Statement s3 = RDFHelper.quad(
                RDFHelper.getBNode("3"),
                RDFHelper.uri("http://pre"),
                RDFHelper.literal("Sample text 1"),
                RDFHelper.uri("http://gra2")
        );
        Statement s4 = RDFHelper.quad(
                RDFHelper.getBNode("4"),
                RDFHelper.uri("http://pre"),
                RDFHelper.literal("Sample text 2", "en"),
                RDFHelper.uri("http://gra2")
        );
        Statement s5 = RDFHelper.quad(
                RDFHelper.getBNode("5"),
                RDFHelper.uri("http://pre"),
                RDFHelper.literal("12345", new URIImpl("http://www.w3.org/2001/XMLSchema#integer")),
                RDFHelper.uri("http://gra2")
        );
        Statement s6 = RDFHelper.quad(
                RDFHelper.uri("p1:sub"),
                RDFHelper.uri("p1:pre"),
                RDFHelper.uri("p1:obj"),
                RDFHelper.uri("p1:gra2")
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
        writer.endRDF();

        // Checking content.
        String content = baos.toString();
        String[] lines = content.split("\n");
        Assert.assertEquals("Unexpected number of lines.", 6, lines.length);
        Assert.assertTrue( lines[0].matches("<.*> <.*> <.*> <.*> \\.") );
        Assert.assertTrue( lines[1].matches("_:.* <.*> _:.* <.*> \\.") );
        Assert.assertTrue( lines[2].matches("_:.* <.*> \".*\" <.*> \\.") );
        Assert.assertTrue( lines[3].matches("_:.* <.*> \".*\"@en <.*> \\.") );
        Assert.assertTrue( lines[4].matches("_:.* <.*> \".*\"\\^\\^<.*> <.*> \\.") );
        Assert.assertTrue( lines[5].matches("<http://.*> <http://.*> <http://.*> <http://.*> \\.") );
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
