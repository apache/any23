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
import org.deri.any23.vocab.XHTML;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.graph.Node;

/**
 * Extractor for the <a href="http://microformats.org/wiki/rel-license">rel-license</a>
 * microformat.
 *
 * TODO: What happens to license links that are not valid URIs?
 * 
 * @author Gabriele Renzi
 * @author Richard Cyganiak
 */
public class LicenseExtractor implements TagSoupDOMExtractor {

	public void run(Document in, ExtractionResult out) throws IOException,
	ExtractionException {
		URI baseURI;
		try {
			baseURI = new URI(in.getBaseURI());
		} catch (URISyntaxException ex) {
			throw new ExtractionException("Error in base URI: " + in.getBaseURI(), ex);
		}

		NodeList nodes = DomUtils.findAll(in, "//A[@rel='license']/@href");
		if (nodes.getLength() == 0) {
			return;
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			String link = nodes.item(i).getNodeValue();
			if ("".equals(link)) continue;
			out.writeTriple(
					Node.createURI(out.getDocumentURI()), 
					XHTML.license.asNode(), 
					Node.createURI(baseURI.resolve(link).toString()), 
					out.getDocumentContext(this));
		}
	}

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<LicenseExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-mf-license",
				PopularPrefixes.createSubset("xhtml"),
				Arrays.asList("text/html;q=0.01", "application/xhtml+xml;q=0.01"),
				null,
				LicenseExtractor.class);
}
