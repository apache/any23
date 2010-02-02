package org.deri.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class XHTML {
    public static final String NS = "http://www.w3.org/1999/xhtml/vocab#";

    private static final ValueFactory factory = ValueFactoryImpl.getInstance();

    public static final URI license = createURI("license");
    public static final URI meta = createURI("meta");
    public static final URI alternate = createURI("alternate");

    private static URI createURI(String localName) {
        return factory.createURI(NS, localName);
    }
}
