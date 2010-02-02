package org.deri.any23.vocab;

import org.openrdf.model.URI;

public class XHTML extends Ontology {

    public static final String NS = "http://www.w3.org/1999/xhtml/vocab#";

    public static final URI license   = createURI(NS, "license"  );
    public static final URI meta      = createURI(NS, "meta"     );
    public static final URI alternate = createURI(NS, "alternate");

    private XHTML(){}
    
}
