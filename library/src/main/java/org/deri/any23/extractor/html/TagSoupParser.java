package org.deri.any23.extractor.html;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;

import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses an InputStream into an HTML DOM tree using a tagsoup parser.
 * <p>
 * <strong>Note:</strong> The resulting DOM tree will not be namespace
 * aware, and all element names will be upper case, while attributes
 * will be lower case. This is because the NekoHTML tagsoup parser
 * by default uses the Xerces HTML DOM implementation, which doesn't
 * support namespaces and forces uppercase element names. This works
 * with the RDFa XSLT and with XPath, so we left it this way.
 * 
 * TODO: We should pass encoding from the Content-Type HTTP header if available
 * TODO: Check if NekoHTML's encoding handling is sane
 *  
 * @author Richard Cyganiak (richard at cyganiak dot de)
 */
public class TagSoupParser {
	private final static Logger log = LoggerFactory.getLogger(TagSoupParser.class);
	
	private final InputStream input;
	private final String documentURI;
	private Document result = null;
	
	public TagSoupParser(InputStream input, String documentURI) {
		this.input = input;
		this.documentURI = documentURI;
	}
	
	public Document getDOM() throws IOException {
		if (result == null) {
			long startTime = System.currentTimeMillis();
			try {
				result = parse();
			} catch (SAXException ex) {
				// should not happen, it's a tag soup parser
				throw new RuntimeException("Shouldn not happen, it's a tag soup parser", ex);
			} catch (TransformerException ex) {
				// should not happen, it's a tag soup parser
				throw new RuntimeException("Shouldn not happen, it's a tag soup parser", ex);
			} catch (NullPointerException ex) {
				if (ex.getStackTrace()[0].getClassName().equals("java.io.Reader")) {
					throw new RuntimeException("Bug in NekoHTML, try upgrading to newer release!", ex);
				} else {
					throw ex;
				}
			} finally {
				long elapsed = System.currentTimeMillis() - startTime;
				log.debug("Parsed " + documentURI + " with NekoHTML, " + elapsed + "ms");
			}
		}
		result.setDocumentURI(documentURI);
		return result;
	}
	
	private Document parse() throws IOException, SAXException, TransformerException {
		DOMParser parser = new DOMParser();
		// must set this to false if we want to use Xerces HTML DOM
		parser.setFeature("http://xml.org/sax/features/namespaces", false);
		parser.parse(new InputSource(input));
		return parser.getDocument();
	}
}