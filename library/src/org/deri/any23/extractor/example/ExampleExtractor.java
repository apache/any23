package org.deri.any23.extractor.example;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.rdf.PopularPrefixes;
import org.openrdf.model.impl.ValueFactoryImpl;

public class ExampleExtractor implements BlindExtractor {

	public void run(URI in, ExtractionResult out)
			throws IOException, ExtractionException {
		ExtractionContext context = out.getDocumentContext(this);
		out.writeTriple(
				ValueFactoryImpl.getInstance().createURI(out.getDocumentURI()),
				context.getPrefixes().expand("rdf:type"), context.getPrefixes().expand("foaf:Document"), 
				out.getDocumentContext(this));
	}
	
	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public static final ExtractorFactory<ExampleExtractor> factory =
			SimpleExtractorFactory.create(
					"example", 
					PopularPrefixes.createSubset("rdf", "foaf"), 
					Collections.singleton("*/*;q=0.01"),
					"http://example.com/",
					ExampleExtractor.class);
}
