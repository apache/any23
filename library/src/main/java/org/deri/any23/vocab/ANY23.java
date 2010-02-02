package org.deri.any23.vocab;

import org.openrdf.model.URI;

// TODO: HIGH - Vocabulary class should probably be refactored away
public class ANY23 extends Ontology {

    public final static String NS = "http://any23.deri.org/";

    public final static URI EXTRACTOR = createURI(NS, "vocab#extractor");

    public static URI getExtractorResource(String extractorName) {
        return createURI(NS, "extractors/" + extractorName);
    }

    private ANY23(){}

}
