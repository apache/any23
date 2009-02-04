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
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.GEO;
import org.w3c.dom.Document;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for "ICBM coordinates" provided as META headers in the head
 * of an HTML page.
 * 
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ICBMExtractor implements TagSoupDOMExtractor {

	public void run(Document in, ExtractionResult out) throws IOException,
	ExtractionException {
		// ICBM is the preferred method, if two values are available it is meaningless to read both
		String props = DomUtils.find(in, "//META[@name=\"ICBM\" or @name=\"geo.position\"]/@content");
		if ("".equals(props)) return;
		
		String[] coords = props.split("[;,]");
		float lat, lon;
		try {
			lat = Float.parseFloat(coords[0]);
			lon = Float.parseFloat(coords[1]);
		} catch (NumberFormatException nfe) {
			return;
		}

		ExtractionContext context = out.getDocumentContext(this);
		Node point = Node.createAnon();
		out.writeTriple(Node.createURI(out.getDocumentURI()), DCTERMS.related.asNode(), point, context);
		out.writeTriple(point, RDF.type.asNode(), GEO.Point.asNode(), context);
		out.writeTriple(point, GEO.lat.asNode(), Node.createLiteral(Float.toString(lat)), context);
		out.writeTriple(point, GEO.long_.asNode(), Node.createLiteral(Float.toString(lon)), context);
	}
	
	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<ICBMExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-head-icbm",
				PopularPrefixes.createSubset("geo", "rdf"),
				Arrays.asList("text/html;q=0.01", "application/xhtml+xml;q=0.01"),
				null,
				ICBMExtractor.class);
}