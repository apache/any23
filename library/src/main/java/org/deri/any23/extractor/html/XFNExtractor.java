package org.deri.any23.extractor.html;

import java.io.IOException;
import java.util.Arrays;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.XFN;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Extractor for the <a href="http://microformats.org/wiki/xfn">XFN</a>
 * microformat.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XFNExtractor implements TagSoupDOMExtractor {
	private final static ValueFactory vf = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());
	
	private HTMLDocument document;
	private ExtractionResult out;
	private ExtractionContext context;
	
	public void run(Document in, ExtractionResult out) throws IOException,
	ExtractionException {
		document = new HTMLDocument(in);
		this.out = out;
		this.context = out.getDocumentContext(this);
		
		BNode subject = vf.createBNode();
		boolean foundAnyXFN = false;
		for (Node link: document.findAll("//A[@rel][@href]")) {
			foundAnyXFN |= extractLink(link, subject);
		}
		if (!foundAnyXFN) return;
		out.writeTriple(subject, RDF.TYPE, FOAF.Person, context);
		out.writeTriple(subject, XFN.mePage, out.getDocumentURI(), context);
	}

	private boolean extractLink(org.w3c.dom.Node firstLink, BNode subject) throws ExtractionException {
		String href = firstLink.getAttributes().getNamedItem("href").getNodeValue();
		String rel = firstLink.getAttributes().getNamedItem("rel").getNodeValue();

		String[] rels = rel.split("\\s+");
		URI link = document.resolveURI(href);
		if (containsRelMe(rels)) {
			if (containsXFNRelExceptMe(rels)) {
				return false;	// "me" cannot be combined with any other XFN values
			}
			out.writeTriple(subject, XFN.me, link, context);
		} else {
			BNode person2 = vf.createBNode();
			boolean foundAnyXFNRel = false;
			for (String aRel : rels) {
				foundAnyXFNRel |= extractRel(aRel, subject, out.getDocumentURI(), person2, link);
			}
			if (!foundAnyXFNRel) {
				return false;
			}
			out.writeTriple(person2, RDF.TYPE, FOAF.Person, context);
			out.writeTriple(person2, XFN.mePage, link, context);
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
	
	private boolean extractRel(String rel, BNode person1, URI uri1, BNode person2, URI uri2) {
		URI peopleProp = XFN.getPropertyByLocalName(rel);
		URI hyperlinkProp = XFN.getExtendedProperty(rel);
		if (peopleProp == null) {
			return false;
		}
		out.writeTriple(person1, peopleProp, person2, context);
		out.writeTriple(uri1, hyperlinkProp, uri2, context);
		return true;
	}

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<XFNExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-mf-xfn",
				PopularPrefixes.createSubset("rdf", "foaf", "xfn"),
				Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
				null,
				XFNExtractor.class);
}