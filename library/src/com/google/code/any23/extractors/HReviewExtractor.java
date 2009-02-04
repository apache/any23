package com.google.code.any23.extractors;

import java.net.URI;
import java.util.List;

import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.vocab.REVIEW;
import org.w3c.dom.Node;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hreview">hReview</a>
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class HReviewExtractor extends EntityBasedMicroformatExtractor {

	public HReviewExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document, "hreview");
	}

	protected boolean extractEntity(Node node, Model model) {
		Resource rev = model.createResource(getBlankNodeFor(model, node));
		rev.addProperty(RDF.type, REVIEW.Review);
		HTMLDocument doc = new HTMLDocument(node);
		addRating(doc,rev);
		addSummary(doc,rev);
		addTime(doc,rev);
		addType(doc,rev);
		addDescription(doc,rev);
		addItem(doc,rev);
		addReviewer(doc,rev);
		return true;
	}
	
	
	private void addType(HTMLDocument doc, Resource rev) {
		String value = doc.getSingularTextField("type");
		conditionallyAddStringProperty(rev, REVIEW.type, value);
	}

	private void addReviewer(HTMLDocument doc, Resource rev) {
		List<Node> nodes = doc.findAllByClassName("reviewer");
		if (nodes.size()>0)
			rev.addProperty(REVIEW.reviewer, getBlankNodeFor(rev.getModel(), nodes.get(0)));
	}

	private void addItem(HTMLDocument root, Resource rev) {
		List<Node> nodes = root.findAllByClassName("item");
		for(Node node: nodes) {
			Resource item = findDummy(new HTMLDocument(node),rev.getModel());
			item.addProperty(REVIEW.hasReview, rev);
		}
	}
	
	private Resource findDummy(HTMLDocument item, Model model) {
		Resource blank = getBlankNodeFor(model, item.getDocument());
		String val = item.getSingularTextField("fn");
		conditionallyAddStringProperty(blank, REVIEW.fn, val);
		val = item.getSingularUrlField("url");
		conditionallyAddResourceProperty(blank, REVIEW.url, val);
		String pics[] = item.getPluralUrlField("photo");
		for(String pic:pics) {
			blank.addProperty(REVIEW.photo, pic);
		}
		return blank;
	}
	
	private void addRating(HTMLDocument doc, Resource rev) {
		String value = doc.getSingularTextField("rating");
		conditionallyAddStringProperty(rev, REVIEW.rating, value);
	}

	
	private void addSummary(HTMLDocument doc, Resource rev) {
		String value = doc.getSingularTextField("summary");
		conditionallyAddStringProperty(rev, REVIEW.title, value);
	}

	private void addTime(HTMLDocument doc, Resource rev) {
		String value = doc.getSingularTextField("dtreviewed");
		conditionallyAddStringProperty(rev, REVIEW.date, value);
	}


	private void addDescription(HTMLDocument doc, Resource rev) {
		String value = doc.getSingularTextField("description");
		conditionallyAddStringProperty(rev, REVIEW.text, value);
	}

	@Override
	public String getFormatName() {
		return "HREVIEW";
	}
}


