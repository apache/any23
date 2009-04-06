package org.deri.any23.extractor.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A wrapper around the DOM representation of an HTML document.
 * Provides convenience access to various parts of the document.
 * 
 * @author Gabriele Renzi
 */
public class HTMLDocument {
	private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

	/**
	 * A utility method to allow writing main() methods for smoke testing an extractor
	 * it simply creates a DOM object from a file whose name was passed on the command line.
	 */
	public static HTMLDocument createFromFile(String filename) {
		try {
			File f = new File(filename);
			String uri = f.toURI().normalize().toString();
			FileInputStream fs = new FileInputStream(new File(filename));
			return new HTMLDocument(new TagSoupParser(fs, uri).getDOM());		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private Node document;
	private java.net.URI baseURI;
	private Any23ValueFactoryWrapper valueFactory = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());
	
	public HTMLDocument(Node document) {
		if(null==document)
			throw new RuntimeException("node cannot be null when constructing an HTMLDocument");
		this.document = document;
	}

	private java.net.URI getBaseURI() throws ExtractionException {
		if (baseURI == null) {
			try {
				baseURI = new java.net.URI(Any23ValueFactoryWrapper.fixAbsoluteURI(document.getBaseURI()));
			} catch (IllegalArgumentException ex) {
				throw new ExtractionException("Error in base URI: " + document.getBaseURI(), ex);
			} catch (URISyntaxException ex) {
				throw new ExtractionException("Error in base URI: " + document.getBaseURI(), ex);
			}
		}
		return baseURI;
	}
	
	/**
	 * @return An absolute URI, or null if the URI is not fixable
	 * @throws ExtractionException If the base URI is invalid
	 */
	public URI resolveURI(String uri) throws ExtractionException {
		return valueFactory.resolveURI(uri, getBaseURI());
	}
	
	public String find(String xpath) {
		return DomUtils.find(getDocument(), xpath);
	}
	
	public Node findNodeById(String id) {
		return DomUtils.findNodeById(getDocument(), id);
	}
	
	public List<Node> findAll(String xpath) {
		return DomUtils.findAll(getDocument(),xpath);
	}

	/* 
	 * @pre: the values are always correct to generate sensible XPATH expressions
	 * @post: always returns non-null strings
	 */
	public String findMicroformattedValue(String objectTag, String object, String fieldTag, String field, String key) {
    	Node node = findMicroformattedObjectNode(objectTag, object);
		if (null==node)
			return "";
    	// try to check if it is inline
		if (DomUtils.hasClassName(node, field))
				return node.getTextContent();

		// failed, try to find it in a child
		try {
			String xpath= ".//"+fieldTag+"[contains(@class, '"+field+"')]/"+key;
			String value = (String) xPathEngine.evaluate(xpath, node, XPathConstants.STRING);
			if (null==value) {
				return "";
			}
			return value;
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		}

	}
	public Node getDocument() {
		return document;
	}

	
	/*TODO: use one single node lookup and multiple attr and class checks
	 * @pre: className is a a valid class name
	 * @post: no null values are returned
	 * @post: if multiple values are found just the first is returned, if we want to check that there are no n-ary values use plural finder
	 */
	public String getSingularTextField(String className) {
		String[] res = getPluralTextField(className);
		if (res.length<1)
			return "";
		return res[0];
	}
	
	public String[] getPluralTextField(String className) {
		List<String> res = new ArrayList<String>(0);
		List<Node> nodes= DomUtils.findAllByClassName(getDocument(),className);
		for(Node node: nodes)
			readTextField(res, node);
		return res.toArray(new String[]{});
	}

	public static void readTextField(List<String> res, Node node) {
		String name= node.getNodeName();
		NamedNodeMap attributes = node.getAttributes();
		// excess of safety check, should be impossible
		if(null==attributes) {
			res.add(node.getTextContent());
			return;
		}
		// first check if there are values inside
		List<Node> values = DomUtils.findAllByClassName(node, "value");
		if (!values.isEmpty()) {
			String val = "";
			for(Node n: values)
				val += n.getTextContent();
			res.add(val.trim());
			return;
		}
		if ("ABBR".equals(name) && (null!=attributes.getNamedItem("title")))
				res.add(attributes.getNamedItem("title").getNodeValue());
		else if ("A".equals(name)) {
			if (DomUtils.hasAttribute(node, "rel", "tag")) {

				String href = extractRelTag(attributes);
				res.add(href);
			}
			else
				res.add(node.getTextContent());
		}
		else if ("IMG".equals(name) || "AREA".equals(name))
			res.add(attributes.getNamedItem("alt").getNodeValue());
		else
			res.add(node.getTextContent());
	}

	private static String extractRelTag(NamedNodeMap attributes) {
		String[] all = attributes.getNamedItem("href").getNodeValue().split("[#?]");
		//cleanup spurious segments
		String path = all[0];
		// get last 
		all = path.split("/");
		return all[all.length-1];
	}

	/*TODO: use one single node lookup and multiple attr and class checks
	 * @pre: className is a a valid class name
	 * @post: no null values are returned
	 * @post: if multiple values are found just the first is returned, if we want to check that there are no n-ary values use plural finder
	 */

	public String getSingularUrlField(String className) {
		String[] res = getPluralUrlField(className);
		if (res.length<1)
			return "";
		return res[0];
	}
	
	public String[] getPluralUrlField(String className) {
		List<String> res = new ArrayList<String>(0);
		List<Node> nodes= DomUtils.findAllByClassName(getDocument(),className);
		for(Node node: nodes)
			readUrlField(res, node);
		return res.toArray(new String[]{});
	}


	public static void readUrlField(List<String> res, Node node) {
		String name= node.getNodeName();
		NamedNodeMap attributes = node.getAttributes();
		if(null==attributes) {
			res.add(node.getTextContent());
			return;
		}
		if ("A".equals(name)|| "AREA".equals(name))
				res.add(attributes.getNamedItem("href").getNodeValue());
		else if ("ABBR".equals(name))
			res.add(attributes.getNamedItem("title").getNodeValue());
		else if ("IMG".equals(name))
			res.add(attributes.getNamedItem("src").getNodeValue());
		else if ("OBJECT".equals(name))
			res.add(attributes.getNamedItem("data").getNodeValue());
		else
			res.add(node.getTextContent().trim());

	}
	
	
	public Node findMicroformattedObjectNode(String objectTag, String name) {
		List<Node> nodes = DomUtils.findAllByTagAndClassName(getDocument(), objectTag, name);
		if (nodes.isEmpty())
			return null;
		return nodes.get(0);
	}

	/*
	 * read an attribute avoiding NullPointerExceptions, if the attr is 
	 * missing it just returns an empty String
	 */

	public String readAttribute(String string) {
		return DomUtils.readAttribute(getDocument(),string);
	}


	public List<Node> findAllByClassName(String string) {
		return DomUtils.findAllByClassName(getDocument(), string);
	}
}
