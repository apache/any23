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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;

public class NTriplesExtractor implements ContentExtractor {

	public void run(InputStream in, ExtractionResult out)
			throws IOException, ExtractionException {
		Model m = ModelFactory.createDefaultModel();
		try {
			m.read(in, out.getDocumentURI(), "N-TRIPLE");
			StmtIterator it = m.listStatements();
			while (it.hasNext()) {
				Statement stmt = it.nextStatement();
				out.writeTriple(stmt.getSubject().asNode(), 
						stmt.getPredicate().asNode(), 
						stmt.getObject().asNode(), 
						out.getDocumentContext(this));
			}
		} catch (JenaException ex) {
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
