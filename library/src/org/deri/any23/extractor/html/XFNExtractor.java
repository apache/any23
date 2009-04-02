package org.deri.any23.extractor.html;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.XFN;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
	private final static ValueFactory vf = ValueFactoryImpl.getInstance();
	
	private Document document;
	private ExtractionResult out;
	private java.net.URI baseURI;
	
	public void run(Document in, ExtractionResult out) throws IOException,
	ExtractionException {
		document = in;
		this.out = out;
		try {
			baseURI = new java.net.URI(in.getBaseURI());
		} catch (URISyntaxException ex) {
			throw new ExtractionException("Error in base URI: " + in.getBaseURI(), ex);
		}
		
		BNode subject = vf.createBNode();
		boolean foundAnyXFN = false;
		for (Node link: DomUtils.findAll(document, "//A[@rel][@href]")) {
			foundAnyXFN |= extractLink(link, subject);
		}
		if (!foundAnyXFN) return;
		writeTriple(subject, RDF.TYPE, FOAF.Person);
		writeTriple(subject, XFN.mePage, vf.createURI(out.getDocumentURI()));
	}

	private void writeTriple(Resource s, URI p, Value o) {
		out.writeTriple(s, p, o, out.getDocumentContext(this));
	}
	
	private boolean extractLink(org.w3c.dom.Node firstLink, BNode subject) {
		String href = DomUtils.find(firstLink, "@href");
		String rel = DomUtils.find(firstLink, "@rel");

		String[] rels = rel.split("\\s+");
		String link = baseURI.resolve(href).toString();
		if (containsRelMe(rels)) {
			if (containsXFNRelExceptMe(rels)) {
				return false;	// "me" cannot be combined with any other XFN values
			}
			writeTriple(subject, XFN.me, vf.createURI(link));
		} else {
			BNode person2 = vf.createBNode();
			boolean foundAnyXFNRel = false;
			for (String aRel : rels) {
				foundAnyXFNRel |= extractRel(aRel, subject, out.getDocumentURI(), person2, link);
			}
			if (!foundAnyXFNRel) {
				return false;
			}
			writeTriple(person2, RDF.TYPE, FOAF.Person);
			writeTriple(person2, XFN.mePage, vf.createURI(link));
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
	
	private boolean extractRel(String rel, BNode person1, String uri1, BNode person2, String uri2) {
		URI peopleProp = XFN.getPropertyByLocalName(rel);
		URI hyperlinkProp = XFN.getExtendedProperty(rel);
		if (peopleProp == null) {
			return false;
		}
		writeTriple(person1, peopleProp, person2);
		writeTriple(vf.createURI(uri1), hyperlinkProp, vf.createURI(uri2));
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