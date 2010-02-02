package org.deri.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class DCTERMS {
    public static final String NS = "http://purl.org/dc/terms/";

    private static final ValueFactory factory = ValueFactoryImpl.getInstance();

    // Properties
    public static final URI license = createURI("license");
    public static final URI title = createURI("title");
    public static final URI creator = createURI("creator");
    public static final URI related = createURI("related");
    public static final URI date = createURI("date");
    public static final URI source = createURI("source");

    private static URI createURI(String localName) {
        return factory.createURI(NS, localName);
    }
}
