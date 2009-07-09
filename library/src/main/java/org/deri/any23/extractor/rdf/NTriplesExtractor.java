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
import org.openrdf.rio.RDFParser.DatatypeHandling;
import org.openrdf.rio.ntriples.NTriplesParser;

public class NTriplesExtractor implements ContentExtractor {

	public void run(InputStream in, URI documentURI, final ExtractionResult out)
			throws IOException, ExtractionException {
		try {
			RDFParser parser = new NTriplesParser();
			parser.setDatatypeHandling(DatatypeHandling.IGNORE);
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
	
	public final static ExtractorFactory<NTriplesExtractor> factory = 
		SimpleExtractorFactory.create(
				"rdf-nt",
				null,
				Arrays.asList("text/plain;q=0.1"),
				"example-ntriples.nt",
				NTriplesExtractor.class);
}
