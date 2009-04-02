package org.deri.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class GEO {
	public static final String NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	
	private static final ValueFactory factory = ValueFactoryImpl.getInstance();

	// Classes
	public static final URI Point = createURI("Point");
	
	// Properties
	public static final URI lat = createURI("lat");
	public static final URI long_ = createURI("long");

	private static URI createURI(String localName) {
		return factory.createURI(NS, localName);
	}
}
