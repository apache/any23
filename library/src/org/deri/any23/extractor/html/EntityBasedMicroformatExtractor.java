package org.deri.any23.extractor.html;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.openrdf.model.BNode;
import org.w3c.dom.Node;

/**
 * Work in progress
 * 
 * @author Gabriele Renzi
 */
public abstract class EntityBasedMicroformatExtractor extends
		MicroformatExtractor {
	
	protected abstract String getBaseClassName();
	
	protected abstract boolean extractEntity(Node node, ExtractionContext context) throws ExtractionException ;

	@Override
	public boolean extract(ExtractionContext context) throws ExtractionException {
		List<Node> nodes = DomUtils.findAllByClassName(document.getDocument(), getBaseClassName());
		boolean foundAny = false;
		for (Node node: nodes) {
			foundAny |= extractEntity(node, out.createContext(this));
		}
		return foundAny;
	}

	/**
	 * @param node a DOM node representing a blank node
	 * @return an RDF blank node corresponding to that DOM node, by using a 
	 * blank node ID like "MD5 of http://doc-uri/#xpath/to/node"
	 */
	protected BNode getBlankNodeFor(Node node) {
		return valueFactory.createBNode(md5(out.getDocumentURI() + "#" + DomUtils.getXPathForNode(node)));
	}
	
	private String md5(String s) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(s.getBytes());
			byte[] digest = md5.digest();
			StringBuffer result = new StringBuffer();
			for (byte b: digest) {
				result.append(Integer.toHexString(0xFF & b));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);	// should never happen, MD5 is supported
		}
	}
}