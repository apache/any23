package org.deri.any23.vocab;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class GEO {

	public static final String NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	
	public static final Resource Point = ResourceFactory.createResource(NS + "Point");
	
	public static final Property lat = ResourceFactory.createProperty(NS, "lat");
	public static final Property long_ = ResourceFactory.createProperty(NS, "long");
}
