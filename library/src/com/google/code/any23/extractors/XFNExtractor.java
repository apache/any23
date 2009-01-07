package com.google.code.any23.extractors;


import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.code.any23.HTMLDocument;
import com.google.code.any23.vocab.FOAF;
import com.google.code.any23.vocab.XFN;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class XFNExtractor extends MicroformatExtractor {
	public XFNExtractor(URI baseURI, HTMLDocument doc) {
		super(baseURI, doc);
	}
	public boolean extractTo(Model model) {
		NodeList links = document.findAll("//A[@rel][@href]");
		if (links.getLength() == 0) {
			return false;
		}
		Resource subject = model.createResource();
		boolean foundAnyXFN = false;
		for (int i = 0; i < links.getLength(); i++) {
			foundAnyXFN |= extractLink(links.item(i), subject, model);
		}
		if (!foundAnyXFN) {
			return false;
		}
		model.add(subject, RDF.type, FOAF.Person);
		model.add(subject, FOAF.isPrimaryTopicOf, model.createResource(baseURI.toString()));
		model.add(subject, FOAF.weblog, model.createResource(baseURI.toString()));
		return true;
	}

	private boolean extractLink(Node firstLink, Resource subject, Model model) {
		String href = firstLink.getAttributes().getNamedItem("href").getNodeValue();
		String rel = firstLink.getAttributes().getNamedItem("rel").getNodeValue();

		String[] rels = rel.split("\\s+");
		if (containsRelMe(rels)) {
			if (containsXFNRelExceptMe(rels)) {
				return false;	// "me" cannot be combined with any other XFN values
			}
			model.add(subject, FOAF.isPrimaryTopicOf, model.createResource(absolutizeURI(href)));
			model.add(subject, FOAF.weblog, model.createResource(absolutizeURI(href)));
		} else {
			Resource person2 = model.createResource();
			boolean foundAnyXFNRel = false;
			for (String aRel : rels) {
				foundAnyXFNRel |= extractRel(aRel, subject, person2, model);
				//we dont' care about saving this things 
				extractExtendedRel(aRel,baseURI.toString(), absolutizeURI(href), model);
			}
			if (!foundAnyXFNRel) {
				return false;
			}
			model.add(person2, RDF.type, FOAF.Person);
			model.add(person2, FOAF.isPrimaryTopicOf, model.createResource(absolutizeURI(href)));
			model.add(subject, FOAF.weblog, model.createResource(absolutizeURI(href)));
		}
		return true;
	}

	private boolean containsRelMe(String[] rels) {
		for (String rel : rels) {
			if ("me".equals(rel.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean containsXFNRelExceptMe(String[] rels) {
		for (String rel : rels) {
			if (!"me".equals(rel.toLowerCase()) && XFN.isXFNLocalName(rel)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean extractRel(String rel, Resource person1, Resource person2, Model model) {
		Property property = XFN.getPropertyByLocalName(rel);
		if (property == null) {
			return false;
		}
		model.add(person1, property, person2);
		return true;
	}
// TODO: merge with above
	private boolean extractExtendedRel(String rel, String url1, String url2, Model model) {
		Property property = XFN.getExtendedProperty(rel);
		if (property == null) {
			return false;
		}
		model.add(model.createResource(url1), property, model.createResource(url2));
		return true;
	}

	
	public static void main(String[] args) throws URISyntaxException {
		doExtraction(new XFNExtractor(new URI("foo"),getDocumentFromArgs(new String[]{"/home/rff/Desktop/PDI.html"})));
	}

	@Override
	public String getFormatName() {
		return "XFN";
	}

}
