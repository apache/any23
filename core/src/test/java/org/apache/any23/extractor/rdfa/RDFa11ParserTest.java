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

import org.apache.any23.rdf.RDFUtils;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.mockito.Mockito.mock;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @deprecated since 2.3
 */
@Deprecated
public class RDFa11ParserTest {

    @Test
    public void testGetDocumentBase() throws MalformedURLException {
        final URL in  = new URL("http://fake.doc/url");
        final URL out = RDFa11Parser.getDocumentBase(in , mock(Document.class) );
        Assert.assertEquals(in, out);
    }

    @Test
    public void testExtractPrefixSections() {
        final String[] sections = RDFa11Parser.extractPrefixSections("p1:v1 p2: v2 p3:   v3\np4:v4      p5:     v5");
        Assert.assertEquals(5, sections.length);
        int i = 0;
        Assert.assertEquals("p1:v1", sections[i++]);
        Assert.assertEquals("p2:v2", sections[i++]);
        Assert.assertEquals("p3:v3", sections[i++]);
        Assert.assertEquals("p4:v4", sections[i++]);
        Assert.assertEquals("p5:v5", sections[i]);
    }

    @Test
    public void testIsCURIEPositive() {
        Assert.assertTrue( RDFa11Parser.isCURIE("[dbr:Albert_Einstein]") );
    }

    @Test
    public void testIsCURIENegative() {
        Assert.assertFalse(RDFa11Parser.isCURIE("[Albert_Einstein]"));
    }

    @Test
    public void testIsCURIEBNodePositive() {
        Assert.assertTrue( RDFa11Parser.isCURIEBNode("[_:john]") );
    }

    @Test
    public void testIsCURIEBNodeNegative() {
        Assert.assertFalse(RDFa11Parser.isCURIEBNode("[:john]"));
    }

    @Test
    public void testIsRelativeNegative() {
        Assert.assertFalse( RDFa11Parser.isRelativeNode( mock(Document.class) ) );
    }

    @Test
    public void testIsRelativePositive1() throws ParserConfigurationException {
        Element div = getRootDocument().createElement("DIV");
        div.setAttribute("rel", "http://fake");
        Assert.assertTrue(RDFa11Parser.isRelativeNode(div));
    }

    @Test
    public void testIsRelativePositive2() throws ParserConfigurationException {
        Element div = getRootDocument().createElement("DIV");
        div.setAttribute("rev", "http://fake");
        Assert.assertTrue(RDFa11Parser.isRelativeNode(div));
    }

    @Test
    public void testUpdateIRIMapping() throws ParserConfigurationException {
        Element div = getRootDocument().createElement("DIV");
        div.setAttribute("xmlns:dc"  , "http://purl.org/dc/terms/");
        div.setAttribute("xmlns:fake", "http://fake.org/");
        final RDFa11Parser parser = new RDFa11Parser();
        parser.updateIRIMapping(div);
        Assert.assertEquals("http://purl.org/dc/terms/", parser.getMapping("dc").toString());
        Assert.assertEquals("http://fake.org/", parser.getMapping("fake").toString());
    }

    @Test
    public void testGetAsPlainLiteral() throws ParserConfigurationException {
        Document doc = getRootDocument();
        Element div = doc.createElement("DIV");
        div.setTextContent("text");

        final Literal literal = RDFa11Parser.getAsPlainLiteral(div, null);
        Assert.assertEquals(RDFUtils.literal("text"), literal);
    }

    @Test
    public void testGetAsXMLLiteral() throws ParserConfigurationException, IOException, TransformerException {
        Document doc = getRootDocument();
        Element root = doc.createElement("DIV");
        Element child1 = doc.createElement("DIV");
        Element child2 = doc.createElement("DIV");
        root.setAttribute(RDFa11Parser.DATATYPE_ATTRIBUTE, RDFa11Parser.XML_LITERAL_DATATYPE);
        child1.setTextContent("text 1");
        child2.setTextContent("text 2");
        root.appendChild(child1);
        root.appendChild(child2);

        final Literal literal = RDFa11Parser.getAsXMLLiteral(root);
        final String value =
                "<DIV datatype=\"rdf:XMLLiteral\">" +
                "<DIV>text 1</DIV><DIV>text 2</DIV>" +
                "</DIV>";
        Assert.assertEquals(RDFUtils.literal(value, RDF.XMLLITERAL), literal);
    }

    private Document getRootDocument() throws ParserConfigurationException {
        final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        return docBuilder.newDocument();
    }

}
