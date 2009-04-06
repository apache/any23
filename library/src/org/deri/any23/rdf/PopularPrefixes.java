package org.deri.any23.rdf;

public class PopularPrefixes {

	public static Prefixes createSubset(String... prefixes) {
		return popularPrefixes.createSubset(prefixes);
	}
	
	public static Prefixes get() {
		return popularPrefixes;
	}
	
	private final static Prefixes popularPrefixes = new Prefixes() {{
		add("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		add("rdfs", "ttp://www.w3.org/2000/01/rdf-schema#");
		add("xhtml", "http://www.w3.org/1999/xhtml#");
		add("dcterms", "http://purl.org/dc/terms/");
		add("foaf", "http://xmlns.com/foaf/0.1/");
		add("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		add("xfn", "http://vocab.sindice.com/xfn#");
		add("vcard", "http://www.w3.org/2006/vcard/ns#");
		add("ical", "http://www.w3.org/2002/12/cal/icaltzd#");
		add("hlisting", "http://sindice.com/hlisting/0.1/");
		add("hreview", "http://purl.org/stuff/rev#");
	}};
}
