package com.google.code.any23.vocab;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/*
 * the jena vocabularies are somewhat outdated and miss some stuff.
 * This vocabulary should be used to put stuff that is supposed to be in those
 *  namespaces but is actually not there
 */
public class MISSING {
	public static class DCTerms {
		public static final String dcTerms = "http://purl.org/dc/terms/";
		public static final Property license = ResourceFactory.createProperty(dcTerms,"license");
		public static final Property image = ResourceFactory.createProperty(dcTerms,"image");
	}
}
