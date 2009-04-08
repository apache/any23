package org.deri.any23.extractor.html;

import java.util.Arrays;
import java.util.List;

import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DOAC;
import org.deri.any23.vocab.FOAF;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hresume">hResume</a>
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class HResumeExtractor extends EntityBasedMicroformatExtractor {
	
	public String getBaseClassName() {
		return "hresume";
	}

	@Override
	protected boolean extractEntity(Node node, ExtractionResult out) {
		if (null == node) return false;
		BNode person = getBlankNodeFor(node);
		// we have a person, at least
		out.writeTriple(person, RDF.TYPE, FOAF.Person);
		HTMLDocument fragment = new HTMLDocument(node);
		addSummary(fragment,person);
		addContact(fragment,person);
		addExperiences(fragment, person);
		addEducations(fragment, person);
		addAffiliations(fragment,person);
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
			out.writeTriple(person, FOAF.isPrimaryTopicOf, getBlankNodeFor(nodes.get(0)));
	}
	
	private void addExperiences(HTMLDocument doc, Resource person) {
		List<Node> nodes = doc.findAllByClassName("experience");
		for (Node node : nodes) {
			Resource exp = valueFactory.createBNode();
			if (addExperience(exp,new HTMLDocument(node)));
				out.writeTriple(person, DOAC.experience, exp);
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
			Resource exp = valueFactory.createBNode();
			if (addExperience(exp,new HTMLDocument(node)));
				out.writeTriple(person, DOAC.education, exp);
		}
	}
	
	private void addAffiliations(HTMLDocument doc, Resource person) {
		List<Node> nodes = doc.findAllByClassName("affiliation");
		for (Node node : nodes) {
			out.writeTriple(person, DOAC.affiliation, getBlankNodeFor(node));
		}
	}

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<HResumeExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-mf-hresume",
				PopularPrefixes.createSubset("rdf", "doac", "foaf"),
				Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
				null,
				HResumeExtractor.class);
}
