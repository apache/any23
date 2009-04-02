package org.deri.any23.extractor.html;

import java.io.IOException;
import java.util.Arrays;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;

/**
 * Extracts the value of the &lt;title&gt; element of an 
 * HTML or XHTML page. 
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TitleExtractor implements TagSoupDOMExtractor {
	
	public void run(Document in, ExtractionResult out) throws IOException,
	ExtractionException {
		String title = DomUtils.find(in, "/HTML/HEAD/TITLE/text()").trim();
		if (title != null && !"".equals(title)) {
			out.writeTriple(
					ValueFactoryImpl.getInstance().createURI(out.getDocumentURI()), 
					DCTERMS.title, 
					ValueFactoryImpl.getInstance().createLiteral(title), 
					out.getDocumentContext(this));
		}
	}
	

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<TitleExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-head-title",
				PopularPrefixes.createSubset("dcterms"),
				Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
				"example-title.html",
				TitleExtractor.class);
}
