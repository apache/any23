package org.deri.any23.vocab;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class DCTERMS {

	public static final String NS = "http://purl.org/dc/terms/";

	public static final Property license = ResourceFactory.createProperty(NS, "license");
	public static final Property title = ResourceFactory.createProperty(NS, "title");
	public static final Property related = ResourceFactory.createProperty(NS, "related");
	
}
