package org.deri.any23.extractor.html;

import java.io.IOException;
import java.net.URI;
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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for the <a href="http://microformats.org/wiki/xfn">XFN</a>
 * microformat.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XFNExtractor implements TagSoupDOMExtractor {
	private Document document;
	private ExtractionResult out;
	private URI baseURI;
	
	public void run(Document in, ExtractionResult out) throws IOException,
	ExtractionException {
		document = in;
		this.out = out;
		try {
			baseURI = new URI(in.getBaseURI());
		} catch (URISyntaxException ex) {
			throw new ExtractionException("Error in base URI: " + in.getBaseURI(), ex);
		}
		
		NodeList links = DomUtils.findAll(document, "//A[@rel][@href]");
		Node subject = Node.createAnon();
		boolean foundAnyXFN = false;
		for (int i = 0; i < links.getLength(); i++) {
			foundAnyXFN |= extractLink(links.item(i), subject);
		}
		if (!foundAnyXFN) return;
		writeTriple(subject, RDF.type.asNode(), FOAF.Person.asNode());
		writeTriple(subject, XFN.mePage.asNode(), Node.createURI(out.getDocumentURI()));
	}

	private void writeTriple(Node s, Node p, Node o) {
		out.writeTriple(s, p, o, out.getDocumentContext(this));
	}
	
	private boolean extractLink(org.w3c.dom.Node firstLink, Node subject) {
		String href = DomUtils.find(firstLink, "@href");
		String rel = DomUtils.find(firstLink, "@rel");

		String[] rels = rel.split("\\s+");
		String link = baseURI.resolve(href).toString();
		if (containsRelMe(rels)) {
			if (containsXFNRelExceptMe(rels)) {
				return false;	// "me" cannot be combined with any other XFN values
			}
			writeTriple(subject, XFN.me.asNode(), Node.createURI(link));
		} else {
			Node person2 = Node.createAnon();
			boolean foundAnyXFNRel = false;
			for (String aRel : rels) {
				foundAnyXFNRel |= extractRel(aRel, subject, out.getDocumentURI(), person2, link);
			}
			if (!foundAnyXFNRel) {
				return false;
			}
			writeTriple(person2, RDF.type.asNode(), FOAF.Person.asNode());
			writeTriple(person2, XFN.mePage.asNode(), Node.createURI(link));
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
	
	private boolean extractRel(String rel, Node person1, String uri1, Node person2, String uri2) {
		Property peopleProp = XFN.getPropertyByLocalName(rel);
		Property hyperlinkProp = XFN.getExtendedProperty(rel);
		if (peopleProp == null) {
			return false;
		}
		writeTriple(person1, peopleProp.asNode(), person2);
		writeTriple(Node.createURI(uri1), hyperlinkProp.asNode(), Node.createURI(uri2));
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