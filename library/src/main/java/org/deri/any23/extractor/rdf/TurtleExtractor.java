package org.deri.any23.extractor.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;

public class TurtleExtractor implements ContentExtractor {

	public void run(InputStream in, URI documentURI, ExtractionResult out)
			throws IOException, ExtractionException {
		try {
			RDFParser parser = new TurtleParser();
			parser.setRDFHandler(new RDFHandlerAdapter(out));
			parser.parse(in, documentURI.stringValue());
		} catch (RDFHandlerException ex) {
			throw new RuntimeException("Should not happen, RDFHandlerAdapter does not throw this", ex);
		} catch (RDFParseException ex) {
			throw new ExtractionException(ex);
		}
	}
	
	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<TurtleExtractor> factory = 
		SimpleExtractorFactory.create(
				"rdf-turtle",
				null,
				Arrays.asList(
						"text/rdf+n3", "text/n3", "application/n3", 
						"application/x-turtle", "application/turtle", "text/turtle"),
				"example-turtle.ttl",
				TurtleExtractor.class);
}
