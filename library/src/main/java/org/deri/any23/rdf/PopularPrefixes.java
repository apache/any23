package org.deri.any23.rdf;

/**
 * This class act as a container for various well-known and adopted RDF Vocabulary prefixes
 *
 * TODO (high): this way of hardcoding prefixes in a class is an anti-pattern: must try a more flexible solution. 
 *
 */
public class PopularPrefixes {

    private final static Prefixes popularPrefixes = new Prefixes() {
        {
            add("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            add("rdfs", "ttp://www.w3.org/2000/01/rdf-schema#");
            add("xhtml", "http://www.w3.org/1999/xhtml/vocab#");
            add("dcterms", "http://purl.org/dc/terms/");
            add("foaf", "http://xmlns.com/foaf/0.1/");
            add("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
            add("xfn", "http://vocab.sindice.com/xfn#");
            add("vcard", "http://www.w3.org/2006/vcard/ns#");
            add("ical", "http://www.w3.org/2002/12/cal/icaltzd#");
            add("hlisting", "http://sindice.com/hlisting/0.1/");
            add("rev", "http://purl.org/stuff/rev#");
            add("doac", "http://ramonantonio.net/doac/0.1/#");
            add("ex", "http://example.com/ns#");
        }
    };


    /**
     *
     * This method perform a prefix lookup. Given a set of prefixes it returns {@link org.deri.any23.rdf.Prefixes} bag
     * class containing them.
     *
     * @param prefixes the input prefixes where perform the lookup
     * @return a {@link org.deri.any23.rdf.Prefixes} containing all the prefixes mathing the input parameter
     */
    public static Prefixes createSubset(String... prefixes) {
        return popularPrefixes.createSubset(prefixes);
    }

    /**
     * @return a {@link org.deri.any23.rdf.Prefixes} with a set of well-known prefixes 
     */
    public static Prefixes get() {
        return popularPrefixes;
    }


}
