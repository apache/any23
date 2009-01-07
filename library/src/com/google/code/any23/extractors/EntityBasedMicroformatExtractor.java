package com.google.code.any23.extractors;

import java.net.URI;
import java.util.List;

import org.w3c.dom.Node;

import com.google.code.any23.DomUtils;
import com.google.code.any23.HTMLDocument;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class EntityBasedMicroformatExtractor extends
		MicroformatExtractor {

	
	private String baseClassName;

	public EntityBasedMicroformatExtractor(URI baseURI, HTMLDocument document, String className) {
		super(baseURI, document);
		this.baseClassName = className;
	}

	protected abstract boolean extractEntity(Node node, Model model);

	public boolean extractTo(Model model) {
		
		List<Node> nodes = DomUtils.findAllByClassName(document.getDocument(), baseClassName);
	
		boolean foundAny = false;
		for (Node node: nodes) {
			foundAny |= extractEntity(node, model);
		}
		return foundAny;
	}

	/**
	 * @param model a jena model
	 * @param node a DOM node representing a blank node
	 * @return  a resource in the model corresponding to that node, by using a name like _:xpath/to/node
	 */
	protected Resource getBlankNodeFor(Model model, Node node) {
		// XXX
		// this works cause I looked in the jena source, but may be incorrect. 
		// If problems arise switch 
		// to internally using a Map<Node,Resource> to always return the same blankNode 
		return model.createResource(new AnonId(DomUtils.getXPathForNode(node)));
	}

}