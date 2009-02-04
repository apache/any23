package org.deri.any23.vocab;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

// TODO: Vocabulary class should probably be refactored away
public class Vocabulary {
	public final static String ANY23_NAMESPACE = "http://any23.deri.org/"; 

	public final static Property EXTRACTOR = 
		ResourceFactory.createProperty(ANY23_NAMESPACE + "vocab#extractor");

	public static Resource getExtractorResource(String extractorName) {
		return ResourceFactory.createResource(
				ANY23_NAMESPACE + "extractors/" + extractorName);
	}
}
