package org.deri.any23.vocab;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class XHTML {
	public static final String NS = "http://www.w3.org/1999/xhtml/vocab#";
	public static final Property license = ResourceFactory.createProperty(NS, "license"); 
}
