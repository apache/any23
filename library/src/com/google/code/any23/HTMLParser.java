package com.google.code.any23;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses an InputStream into an HTML DOM tree using a tagsoup parser.
 * 
 * TODO: We should pass Content-Type from the HTTP header if available
 * TODO: Charset detection is questionable
 * TODO: If Content-Type is application/xhtml+xml, we should use an XML parser, not TagSoup
 * TODO: This should work without explicitly distinguishing fragment mode vs. doc mode
 *  
 * @version $Id$
 * @author Richard Cyganiak (richard at cyganiak dot de)
 */
public class HTMLParser {
	private final String defaultCharEncoding = "UTF-8";
	private Node node;

	public HTMLParser(InputStream input, boolean fragment) {
		try {
			if (fragment) {
				this.node = parseHTMLFragment(input);
			} else {
				this.node = parseHTMLDocument(input);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (TransformerException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public HTMLParser(InputStream input) {
		this(input, false);
	}

	public Node getDocumentNode() {
		return this.node;
	}

	private Node parseHTMLFragment(InputStream input) throws IOException, SAXException, TransformerException {
		BufferedInputStream buffered = new BufferedInputStream(input);
		String encoding = detectEncoding(buffered);
		InputSource source = new InputSource(new InputStreamReader(buffered, encoding));

		DOMFragmentParser parser = new DOMFragmentParser();
//		DOMParser parser = new DOMParser();
		try {
			parser.setFeature("http://cyberneko.org/html/features/augmentations",
					true);
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding",
					defaultCharEncoding);
			parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset",
					true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
					false);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment",
					true);
		} catch (SAXException e) {throw new RuntimeException(e);}
//		parser.parse(source);
//		return parser.getDocument();
		// convert Document to DocumentFragment
		HTMLDocumentImpl doc = new HTMLDocumentImpl();
		doc.setErrorChecking(false);
		DocumentFragment res = doc.createDocumentFragment();
		DocumentFragment frag = doc.createDocumentFragment();
		parser.parse(source, frag);
		res.appendChild(frag);

		while(true) {
			frag = doc.createDocumentFragment();
			parser.parse(source, frag);
			if (!frag.hasChildNodes()) 
				break;
			res.appendChild(frag);
		}
		return res;
	}

	private Document parseHTMLDocument(InputStream input) throws IOException, SAXException, TransformerException {
		BufferedInputStream buffered = new BufferedInputStream(input);
		String encoding = detectEncoding(buffered);
		InputSource source = new InputSource(new InputStreamReader(buffered, encoding));

		DOMParser parser = new DOMParser();
		try {
			parser.setFeature("http://cyberneko.org/html/features/augmentations",
					true);
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding",
					defaultCharEncoding);
			parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset",
					true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
					false);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment",
					true);
			parser.setProperty("http://cyberneko.org/html/properties/names/elems", 
					"lower");
		} catch (SAXException e) {throw new RuntimeException(e);}
		parser.parse(source);
		return parser.getDocument();
	}

	private String detectEncoding(BufferedInputStream input) throws IOException {
		input.mark(1000);
		byte[] buffer = new byte[1000];
		int bytesRead = input.read(buffer);
		input.reset();
		String s = new String(buffer, 0, bytesRead, "iso-8859-1");
		String candidate = detectEncoding(s);
		try {
			if (candidate != null && Charset.isSupported(candidate)) {
				return candidate;
			}
		} catch (IllegalArgumentException ex) {
			// ignore syntactically invalid encoding names
		}
		return "iso-8859-1";
	}

	private String detectEncoding(String htmlFragment) {
		Matcher m = encodingRegex1.matcher(htmlFragment);
		if (m.find()) {
			return m.group(1);
		}
		m = encodingRegex2.matcher(htmlFragment);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	private final static Pattern encodingRegex1 = Pattern.compile(
			";\\s*charset\\s*=\\s*([^\\s'\";]+)", Pattern.CASE_INSENSITIVE);
	private final static Pattern encodingRegex2 = Pattern.compile(
			"encoding\\s*=\\s*['\"]\\s*([^\\s'\";]+)\\s*['\"]",
			Pattern.CASE_INSENSITIVE);
}
