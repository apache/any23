package org.deri.any23.vocab;

import org.openrdf.model.URI;

public class GEO extends Ontology {

    public static final String NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";

    // Classes
    public static final URI Point = createURI(NS, "Point");

    // Properties
    public static final URI lat = createURI(NS, "lat" );
    public static final URI lon = createURI(NS, "long");

    private GEO(){}

}
