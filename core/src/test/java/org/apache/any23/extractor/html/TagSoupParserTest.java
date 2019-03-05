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

package org.apache.any23.extractor.html;

import org.apache.any23.extractor.ExtractionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Reference Test class for {@link TagSoupParser} parser.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 *
 */
public class TagSoupParserTest {

    private static final String page = "http://semanticweb.org/wiki/Knud_M%C3%B6ller";

    private TagSoupParser tagSoupParser;

    @After
    public void tearDown() throws RepositoryException {
        this.tagSoupParser = null;
        
    }

    @Test
    public void testParseSimpleHTML() throws IOException {
        String html = "<html><head><title>Test</title></head><body><h1>Hello!</h1></body></html>";
        InputStream input = new ByteArrayInputStream(html.getBytes());
        Node document = new TagSoupParser(input, "http://example.com/").getDOM();
        Assert.assertEquals("Test", new HTMLDocument(document).find("//TITLE"));
        Assert.assertEquals("Hello!", new HTMLDocument(document).find("//H1"));
    }

    @Test
    public void testExplicitEncodingBehavior()
    throws IOException, ExtractionException, RepositoryException {
        this.tagSoupParser = new TagSoupParser(
                new BufferedInputStream(this.getClass().getResourceAsStream("/html/encoding-test.html")),
                page,
                "UTF-8"
        );

        Assert.assertEquals(
            this.tagSoupParser.getDOM().getElementsByTagName("title").item(0).getTextContent(),
            "Knud M\u00F6ller - semanticweb.org"
        );
    }

    /**
     * This tests the Neko HTML parser without forcing it on using a specific encoding charset.
     * We expect that this test may fail if something changes in the Neko library, as an auto-detection of
     * the encoding.
     *
     * @throws IOException if there is an error interpreting the input data
     * @throws ExtractionException if there is an exception during extraction
     * @throws org.eclipse.rdf4j.repository.RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    public void testImplicitEncodingBehavior() throws IOException, ExtractionException, RepositoryException {
        this.tagSoupParser = new TagSoupParser(
                new BufferedInputStream(this.getClass().getResourceAsStream("/html/encoding-test.html")),
                page
        );
        Assert.assertNotSame(
                this.tagSoupParser.getDOM().getElementsByTagName("title").item(0).getTextContent(),
                "Knud M\u00F6ller - semanticweb.org"
        );
    }


    /**
     * Test related to the issue 78 and disabled until the underlying <i>NekoHTML</i>
     * bug has been fixed.
     * 
     * @throws IOException if there is an error interpreting the input data
     */
    @Test
    public void testEmptySpanElements() throws IOException {
        final String page = "http://example.com/test-page";
        InputStream brokenEmptySpanHtml = 
                new BufferedInputStream(this.getClass().getResourceAsStream("/html/empty-span-broken.html"))
        ;
        InputStream worksEmptySpanHtml = 
                new BufferedInputStream(this.getClass().getResourceAsStream("/html/empty-span-works.html"))
        ;
        this.tagSoupParser = new TagSoupParser(brokenEmptySpanHtml, page);
        Document brokenElementDom = this.tagSoupParser.getDOM();
        this.tagSoupParser = null; // useless but force GC

        this.tagSoupParser = new TagSoupParser(worksEmptySpanHtml, page);
        Document worksElementDom = this.tagSoupParser.getDOM();

        NodeList brokenNodeList = brokenElementDom.getElementsByTagName("span");
        Assert.assertEquals(3, brokenNodeList.getLength());

        NodeList worksNodeList = worksElementDom.getElementsByTagName("span");
        Assert.assertEquals(3, worksNodeList.getLength());

        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        PrintStream psOut1 = new PrintStream(out1);
        for (int i = 0; i < worksNodeList.getLength(); i++) {
            printNode(worksNodeList.item(i), psOut1);
        }
        psOut1.close();

        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        PrintStream psOut2 = new PrintStream(out2);
        for (int i = 0; i < brokenNodeList.getLength(); i++) {
            printNode(brokenNodeList.item(i), psOut2);
        }
        psOut2.close();

        Assert.assertEquals(out1.toString(), out2.toString());
    }

    private void printNode(Node node, PrintStream printStream) {
        printStream.println("node name:" + node.getNodeName());
        printStream.println("node value:" + node.getNodeValue());
        printStream.println("node has child:" + node.hasChildNodes());
        printStream.println("node # child:" + node.getChildNodes().getLength());

        printStream.println("node child:");
        NodeList childNodes = node.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node brokenChild = childNodes.item(j);
            printStream.println("    node name:" + brokenChild.getNodeName());
            printStream.println("    node type:" + brokenChild.getNodeType());
            printStream.println("    node value:" + trimValue(brokenChild.getNodeValue()));
        }

        printStream.println("node attributes:");
        NamedNodeMap namedNodeMap = node.getAttributes();
        for (int j = 0; j < namedNodeMap.getLength(); j++) {
            Node attribute = namedNodeMap.item(j);
            printStream.println("    attribute name:" + attribute.getNodeName());
            printStream.println("    attribute value:" + trimValue(attribute.getNodeValue()));
        }
        printStream.println();
    }

    private String trimValue(String in) {
        return in == null ? "" : in.trim();
    }

}