package org.deri.any23.extractor.html;


import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class DomUtilsTest {

    private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

    private void check(String file, String xpath, String reverseXPath) {
        Node dom = new HTMLFixture(file).getDOM();
        Assert.assertNotNull(dom);
        Node node;
        try {
            node = (Node) xPathEngine.evaluate(xpath, dom, XPathConstants.NODE);
            Assert.assertNotNull(node);
            Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
            String newPath = DomUtils.getXPathForNode(node);
            Assert.assertEquals(reverseXPath, newPath);
            Node newNode = (Node) xPathEngine.evaluate(newPath, dom, XPathConstants.NODE);
            Assert.assertEquals(node, newNode);

        } catch (XPathExpressionException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testGetXPathForNode() {
        check("hcard/01-tantek-basic.html", "//DIV[@class='vcard']", "/HTML[1]/BODY[1]/DIV[1]");
        check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN[@class='fn n']", "/HTML[1]/BODY[1]/DIV[1]/SPAN[1]");
        check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN/SPAN[@class='fn n']", "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]");
        check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN/SPAN/*[@class='given-name']", "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]/SPAN[1]");
        check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN/SPAN/*[@class='family-name']", "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]/SPAN[2]");
    }

    public void testFindAllByClassName() {
    // TODO (medium): to be implemented
    }

	public void testFindAllByTag() {
        // TODO (medium): to be implemented
	}

	public void testFindAllByTagAndClassName() {
        // TODO (medium): to be implemented
	}

	public void testFindNodeById() {
        // TODO (medium): to be implemented
	}

	public void testFindAll() {
        // TODO (medium): to be implemented
	}

	public void testFind() {
        // TODO (medium): to be implemented
	}

    public void testHasClassName() {
        // TODO (medium): to be implemented
    }

	public void testHasAttribute() {
        // TODO (medium): to be implemented
	}

	public void testIsElementNode() {
        // TODO (medium): to be implemented
	}

	public void testReadAttribute() {
        // TODO (medium): to be implemented
	}

}
