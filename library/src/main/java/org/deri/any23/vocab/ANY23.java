package org.deri.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

// TODO: Vocabulary class should probably be refactored away
public class ANY23 {
    public final static String NS = "http://any23.deri.org/";

    private static final ValueFactory factory = ValueFactoryImpl.getInstance();

    public final static URI EXTRACTOR = createURI("vocab#extractor");

    public static URI getExtractorResource(String extractorName) {
        return createURI("extractors/" + extractorName);
    }

    private static URI createURI(String localName) {
        return factory.createURI(NS, localName);
    }
}
