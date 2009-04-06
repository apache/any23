package org.deri.any23.extractor.html;

import java.util.List;

import org.deri.any23.extractor.ExtractionContext;
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
	
	protected abstract boolean extractEntity(Node node, ExtractionContext context);

	@Override
	public boolean extract(ExtractionContext context) {
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
	 * blank node ID like _:http://doc-uri/#xpath/to/node
	 */
	protected BNode getBlankNodeFor(Node node) {
		return valueFactory.createBNode(out.getDocumentURI() + "#" + DomUtils.getXPathForNode(node));
	}
}