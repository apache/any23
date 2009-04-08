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
import org.deri.any23.vocab.XHTML;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class HeadLinkExtractor implements TagSoupDOMExtractor {
	
	public void run(Document in, URI documentURI, ExtractionResult out) throws IOException,
			ExtractionException {
		HTMLDocument html = new HTMLDocument(in);
		ValueFactory vf = ValueFactoryImpl.getInstance();
		
		for (Node node: DomUtils.findAll(in,
				"/HTML/HEAD/LINK[(" +
				"@type='application/rdf+xml' or " +
				"@type='text/rdf' or " +
				"@type='application/x-turtle' or " +
				"@type='application/turtle' or " +
				"@type='text/turtle' or " +
				"@type='text/rdf+n3'" +
				") and @href and @rel]")) {
			URI href = html.resolveURI(DomUtils.find(node, "@href"));
			String rel = DomUtils.find(node, "@rel");
			out.writeTriple(
					documentURI, 
					vf.createURI(XHTML.NS + rel), 
					href);
			String title = DomUtils.find(node, "@title");
			if (title != null && !"".equals(title)) {
				out.writeTriple(
						href, 
						factory.getPrefixes().expand("dcterms:title"), 
						vf.createLiteral(title));
			}
		}
	}

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<HeadLinkExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-head-rdflinks",
				PopularPrefixes.createSubset("xhtml", "dcterms"),
				Arrays.asList("text/html;q=0.05", "application/xhtml+xml;q=0.05"),
				null,
				HeadLinkExtractor.class);
}
