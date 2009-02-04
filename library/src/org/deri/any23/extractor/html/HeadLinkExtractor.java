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
import org.deri.any23.vocab.DCTERMS;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.vocabulary.RDFS;

public class HeadLinkExtractor implements TagSoupDOMExtractor {
	
	public void run(Document in, ExtractionResult out) throws IOException,
			ExtractionException {
		URI baseURI;
		try {
			baseURI = new URI(in.getBaseURI());
		} catch (URISyntaxException ex) {
			throw new ExtractionException("Error in base URI: " + in.getBaseURI(), ex);
		}

		NodeList links = DomUtils.findAll(in,
				"/HTML/HEAD/LINK[(" +
				"@type='application/rdf+xml' or " +
				"@type='text/rdf' or " +
				"@type='application/x-turtle' or " +
				"@type='application/turtle' or " +
				"@type='text/turtle' or " +
				"@type='text/rdf+n3'" +
				") and @href]");

		for (int i = 0; i < links.getLength(); i++) {
			Node href = Node.createURI(baseURI.resolve(DomUtils.find(links.item(i), "@href")).toString());
			out.writeTriple(
					Node.createURI(out.getDocumentURI()), 
					RDFS.seeAlso.asNode(), 
					href, 
					out.getDocumentContext(this));
			String title = DomUtils.find(links.item(i), "@title");
			if (title != null && !"".equals(title)) {
				out.writeTriple(
						href, 
						DCTERMS.title.asNode(), 
						Node.createLiteral(title), 
						out.getDocumentContext(this));
			}
		}
	}

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<HeadLinkExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-head-rdflinks",
				PopularPrefixes.createSubset("rdfs", "dcterms"),
				Arrays.asList("text/html;q=0.05", "application/xhtml+xml;q=0.05"),
				null,
				HeadLinkExtractor.class);
}
