package com.google.code.any23.extractors;

import java.net.URI;
import java.util.List;

import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.vocab.DOAC;
import org.deri.any23.vocab.FOAF;
import org.w3c.dom.Node;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hresume">hResume</a>
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class HResumeExtractor extends EntityBasedMicroformatExtractor {

	public HResumeExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document, "hresume");
	}

	@Override
	protected boolean extractEntity(Node _node, Model model) {
		if (null== _node)
			return false;
		Resource person = getBlankNodeFor(model, _node);
		// we have a person, at least
		person.addProperty(RDF.type, FOAF.Person);
		HTMLDocument doc = new HTMLDocument(_node);
		addSummary(doc,person);
		addContact(doc,person);
		addExperiences(doc, person);
		addEducations(doc, person);
		addAffiliations(doc,person);
		//addSkills //reltag
		return true;
	}

	private void addSummary(HTMLDocument doc, Resource person) {
		String summary = doc.getSingularTextField("summary");
		conditionallyAddStringProperty(person, DOAC.summary, summary);
	}
	
	private void addContact(HTMLDocument doc, Resource person) {
		List<Node> nodes = doc.findAllByClassName("contact");
		if (nodes.size()>0)
			person.addProperty(FOAF.isPrimaryTopicOf, getBlankNodeFor(person.getModel(), nodes.get(0)));
	}
	
	private void addExperiences(HTMLDocument doc, Resource person) {
		List<Node> nodes = doc.findAllByClassName("experience");
		for (Node node : nodes) {
			Resource exp = person.getModel().createResource();
			if (addExperience(exp,new HTMLDocument(node)));
				person.addProperty(DOAC.experience, exp);
		}
	}

	private boolean addExperience(Resource exp, HTMLDocument document) {
		String check = "";
		String value = document.getSingularTextField("title");
		check += value;
		conditionallyAddStringProperty(exp, DOAC.title, value.trim());
		value = document.getSingularTextField("dtstart");
		check += value;

		conditionallyAddStringProperty(exp, DOAC.start_date, value.trim());	
		value = document.getSingularTextField("dtend");
		check += value;

		conditionallyAddStringProperty(exp, DOAC.end_date, value.trim());
		value = document.getSingularTextField("summary");
		check += value;

		conditionallyAddStringProperty(exp, DOAC.organization, value.trim());

		return !"".equals(check);
			
		//TODO: positon = role? activity=?
	}

	private void addEducations(HTMLDocument doc, Resource person) {
		List<Node> nodes = doc.findAllByClassName("education");
		for (Node node : nodes) {
			Resource exp = person.getModel().createResource();
			if (addExperience(exp,new HTMLDocument(node)));
				person.addProperty(DOAC.education, exp);
		}
	}
	
	private void addAffiliations(HTMLDocument doc, Resource person) {
		List<Node> nodes = doc.findAllByClassName("affiliation");
		for (Node node : nodes) {
			person.addProperty(DOAC.affiliation, getBlankNodeFor(person.getModel(), node));
		}
	}

	@Override
	public String getFormatName() {
		return "HRESUME";
	}
	
}
