package org.deri.any23.extractor.rdfa;

import org.deri.any23.rdf.RDFUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.vocabulary.RDF;
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
 */
public class RDFa11ParserTest {

    @Test
    public void testGetDocumentBase() throws MalformedURLException {
        final URL in  = new URL("http://fake.doc/url");
        final URL out = RDFa11Parser.getDocumentBase(in , mock(Document.class) );
        Assert.assertEquals(in, out);
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
    public void testUpdateURIMapping() throws ParserConfigurationException {
        Element div = getRootDocument().createElement("DIV");
        div.setAttribute("xmlns:dc"  , "http://purl.org/dc/terms/");
        div.setAttribute("xmlns:fake", "http://fake.org/");
        final RDFa11Parser parser = new RDFa11Parser();
        parser.updateURIMapping(div);
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
