package org.deri.any23.vocab;

import org.openrdf.model.URI;

public class DCTERMS extends Ontology {

    public static final String NS = "http://purl.org/dc/terms/";

    // Properties
    public static final URI license = createURI(NS, "license");
    public static final URI title   = createURI(NS, "title"  );
    public static final URI creator = createURI(NS, "creator");
    public static final URI related = createURI(NS, "related");
    public static final URI date    = createURI(NS, "date"   );
    public static final URI source  = createURI(NS, "source" );

    private DCTERMS(){}
}
