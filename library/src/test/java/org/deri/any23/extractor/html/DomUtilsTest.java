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

package org.deri.any23.extractor.html;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Reference Test class for the {@link org.deri.any23.extractor.html.DomUtils} class.
 */
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
        // TODO #11 - to be implemented
    }

	public void testFindAllByTag() {
        // TODO #11 - to be implemented
	}

	public void testFindAllByTagAndClassName() {
        // TODO #11 - to be implemented
	}

	public void testFindNodeById() {
        // TODO #11 - to be implemented
	}

	public void testFindAll() {
        // TODO #11 - to be implemented
	}

	public void testFind() {
        // TODO #11 - to be implemented
	}

    public void testHasClassName() {
        // TODO #11 - to be implemented
    }

	public void testHasAttribute() {
        // TODO #11 - to be implemented
	}

	public void testIsElementNode() {
        // TODO #11 - to be implemented
	}

	public void testReadAttribute() {
        // TODO #11 - to be implemented
	}

}
