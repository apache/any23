package org.deri.any23.extractor.html;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.deri.any23.extractor.html.DomUtils;
import org.w3c.dom.Node;


public class DomUtilsTest extends TestCase {

	private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

	private void check(String file, String xpath, String reverseXPath) {
		Node dom = new HTMLFixture(file).getDOM();
		assertNotNull(dom);
		Node node;
		try {
			node = (Node) xPathEngine.evaluate(xpath, dom, XPathConstants.NODE);
			assertNotNull(node);
			assertEquals(Node.ELEMENT_NODE, node.getNodeType());
			String newPath = DomUtils.getXPathForNode(node);
			assertEquals(reverseXPath, newPath);
			Node newNode = (Node) xPathEngine.evaluate(newPath, dom, XPathConstants.NODE);
			assertEquals(node, newNode);

		} catch (XPathExpressionException ex) {
			fail(ex.getMessage());
		}
	}
	
	// ok I should use something else I know
	public void testGetXPathForNode() {
		check("hcard/01-tantek-basic.html", "//DIV[@class='vcard']", "/HTML[1]/BODY[1]/DIV[1]");
		check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN[@class='fn n']", "/HTML[1]/BODY[1]/DIV[1]/SPAN[1]");
		check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN/SPAN[@class='fn n']", "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]");
		check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN/SPAN/*[@class='given-name']", "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]/SPAN[1]");
		check("hcard/02-multiple-class-names-on-vcard.html", "//SPAN/SPAN/*[@class='family-name']", "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]/SPAN[2]");
	}
//
//	public void testFindAllByClassName() {
//		fail("Not yet implemented");
//	}
//
//	public void testFindAllByTag() {
//		fail("Not yet implemented");
//	}
//
//	public void testFindAllByTagAndClassName() {
//		fail("Not yet implemented");
//	}
//
//	public void testFindNodeById() {
//		fail("Not yet implemented");
//	}
//
//	public void testFindAll() {
//		fail("Not yet implemented");
//	}
//
//	public void testFind() {
//		fail("Not yet implemented");
//	}
//
//	public void testHasClassName() {
//		fail("Not yet implemented");
//	}
//
//	public void testHasAttribute() {
//		fail("Not yet implemented");
//	}
//
//	public void testIsElementNode() {
//		fail("Not yet implemented");
//	}
//
//	public void testReadAttribute() {
//		fail("Not yet implemented");
//	}

}
