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
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.XHTML;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
	private final ValueFactory vFactory = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());
	
	
	public void run(Document in, ExtractionResult out) throws IOException,
	ExtractionException {
		java.net.URI baseURI;
		try {
			baseURI = new java.net.URI(vFactory.createURI(in.getBaseURI()).toString());
		} catch (URISyntaxException ex) {
			throw new ExtractionException("Error in base URI: " + in.getBaseURI(), ex);
		}

		for (Node node: DomUtils.findAll(in, "//A[@rel='license']/@href")) {
			String link = node.getNodeValue();
			if ("".equals(link)) continue;
			out.writeTriple(
					vFactory.createURI(out.getDocumentURI()), 
					XHTML.license, 
					vFactory.createURI(baseURI.resolve(link).toString()), 
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
