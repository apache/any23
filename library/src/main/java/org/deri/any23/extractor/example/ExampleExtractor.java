package org.deri.any23.extractor.example;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.FOAF;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

import java.io.IOException;
import java.util.Collections;

/**
 * Example concrete implementation of {@link org.deri.any23.extractor.Extractor.BlindExtractor}.
 * 
 */
//TODO: MED - move in test module. 
public class ExampleExtractor implements BlindExtractor {

    public void run(URI in, URI documentURI, ExtractionResult out)
            throws IOException, ExtractionException {
        out.writeTriple(documentURI, RDF.TYPE, FOAF.Document);
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
