package com.google.code.any23;

import java.util.ArrayList;
import java.util.List;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class provides a bit of utility methods for DOM manipulation so that it is less painful
 * It is separated from HTMLDocument so that its methods can be run on single Node instances
 * avoid the wrapping in HTMLDocument.
 * It is currently using a mix of XPath and DOM manipulation, this is likely to be a performance bottleneck
 * but at least everything is localized here.
 * TODO test, albeit running the extractors' tests also tests this.
 */
// TODO import all XPath here
public class DomUtils {
	private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

	/*
	 * does a reverse walking of the dom tree to generate a unique xpath to this node.
	 * Thje XPath generated is the canonical one based on sibling index: 
	 * /html[1]/body[1]/div[2]/span[3] etc.. 
	 */
	public static String getXPathForNode(Node node) {
	    String index = "";
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    int successors = 1;
	        Node previous = node.getPreviousSibling();
	        while (null!=previous){;
		        if (previous.getNodeName().equals(node.getNodeName())) {
		        	successors++;
		        }
		        previous = previous.getPreviousSibling();
	        }
	        index = "/"+node.getNodeName()+"["+successors+"]";
		}
        Node parent = node.getParentNode();
        if (null==parent)
        	return index;
        else
        	return getXPathForNode(parent)+index;

    }


	/*
	 * Find all nodes that have a declared class. 
	 * Note that the className is transformed to lower case to avoid stupid errors
	 */
	
	public static List<Node> findAllByClassName(Node root, String className) {
		return findAllByTagAndClassName(root, "*", className.toLowerCase());
	}

	public static List<Node> findAllByTag(Node root, String tagName) {
		return findAllByTagAndClassName(root, tagName, "");
	}
	
	public static List<Node> findAllByTagAndClassName(Node root,String tagName, String className) {
		List<Node> result = new ArrayList<Node>(0);
		NodeList nodes= findAll(root, "./descendant-or-self::"+tagName+"[contains(@class,'"+className+"')]");
		for(int i=0; i<nodes.getLength();i++) {
			Node node = nodes.item(i);
			if (DomUtils.hasClassName(node, className)) {
				// add this
				result.add(node);
		/*
		 * to mimick the inheritance of CSS classes we should recur here
		 * 
		 * Only one level of depth is cosidered though, so take care.
		 */
//				// add childNodes
//				NodeList children = node.getChildNodes();
//				for(int j=0;j<children.getLength();j++){
//					Node child = children.item(j);
//					if (child.getNodeType()==Node.ELEMENT_NODE)
//						result.add(child);
//				}
			}
		}
		return result;
	}
	
	/*
	 * mimicks JS DOM API, or prototype's $()
	 */
	public static Node findNodeById(Node root, String id) {
		Node node;
		try {
			String xpath= "//*[@id='"+id+"']";
			node = (Node) xPathEngine.evaluate(xpath, root, XPathConstants.NODE);
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		}
		return node;
	}
	
	/*
	 * returns a NodeList composed of all the nodes that match an xpath expression, which must be valid
	 */
	public static NodeList findAll(Node node, String xpath) {
		try {
			return (NodeList) xPathEngine.evaluate(xpath, node, XPathConstants.NODESET);
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/*
	 * gets the string value of an XPath expression
	 */
	
	public static String find(Node node, String xpath) {
		try {
			String val= (String) xPathEngine.evaluate(xpath, node, XPathConstants.STRING);
			if (null==val)
				return "";
			return val;
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/* 
     * This method tells if an element has a class name <b>not checking the parents in the hierarchy</b>
     * mimicking the CSS .foo match
     * 
     * TODO hunt all class check and replace
     */
	
	public static boolean hasClassName(Node node, String className) {
		return hasAttribute(node, "class", className);
	}

	/*
	 * Checks the presence of an attribute with a value.
	 * The semantic is the CSS classes' ones: "foo" matches "bar foo", "foo" but not "foob"
	 */
	public static boolean hasAttribute(Node node, String attributeName, String className) {
		// regex love, maybe faster but less easy ti understand
//		Pattern pattern = Pattern.compile("(^|\\s+)"+className+"(\\s+|$)");
		String attr = readAttribute(node, attributeName);
		for(String c: attr.split("\\s+"))
			if (c.equals(className))
				return true;
		return false;
	}
	
	
	
	public static boolean isElementNode(Node target) {
		return Node.ELEMENT_NODE == target.getNodeType();
	}
	
	/* 
	 * reads the value of an attribute avoiding null handling
	 * @post the result is never null
	 */

	public static String readAttribute(Node document, String attribute) {
		NamedNodeMap attributes = document.getAttributes();
		if (null==attributes)
			return "";
		Node attr = attributes.getNamedItem(attribute);
		if (null==attr)
			return "";
		return attr.getNodeValue();
	}
}
