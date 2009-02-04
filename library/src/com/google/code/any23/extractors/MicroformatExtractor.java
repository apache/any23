package com.google.code.any23.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import org.deri.any23.extractor.html.HTMLDocument;




import com.google.code.any23.HTMLParser;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The abstract base class for any Microformat extractor.
 * It requires a method that returns the name of the microformat,
 * and a method that performs the extraction and writes the results
 * to an RDF model.
 * 
 * The nodes generated in the model can have any name or implicit label
 * but if possible they SHOULD have names (either URIs or AnonId) that
 * are uniquely derivable from their position in the DOM tree, so that
 * multiple extractors can merge information.
 */
public abstract class MicroformatExtractor {

	/**
	 * A utility method to allow writing main() methods for smoke testing an extractor
	 * it simply creates a DOM object from a file whose name was passed on the command line.
	 */
	protected static HTMLDocument getDocumentFromArgs(String[] args) {
		FileInputStream fs;
		try {
			fs= new FileInputStream(new File(args[0]));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return new HTMLDocument(new HTMLParser(fs, true).getDocumentNode());		
	}
	
	/**
	 * Apply an extractor to the current model and output it as turtle. 
	 * Complementary method to getdocumentFromArgs() for smoke testing. 
	 */
	protected static void doExtraction(MicroformatExtractor e) {
		Model model = ModelFactory.createDefaultModel();
		e.extractTo(model);
		model.write(System.out,"TURTLE");
	}

	protected URI baseURI;
	protected HTMLDocument document;

	public MicroformatExtractor(URI baseURI, HTMLDocument document) {
		this.baseURI = baseURI;
		this.document = document;
	}

	/**
	 * Performs the extraction of the data and writes them to the model.
	 */
	public abstract boolean extractTo(Model model);
	
	/**
	 * Returns the format name for this extractor so that it can be added as a metadatum.
	 * This method MAY return an empty string, cause some extractors may do stuff that is unrelated 
	 * to a specific microformat, such as title extraction or metadata merging.
	 */
	public abstract String getFormatName();
	
	/**
	 * If uri is absolute, return that, otherwise an absolute uri relative to base, or "" if invalid.
	 * @param uri a uri or fragment
	 * @return The URI in absolute form 
	 */
	protected String absolutizeURI(String uri) {
		try {
			return baseURI.resolve(uri).toString();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Helper method that adds a literal property to a node.
	 */
	protected void conditionallyAddStringProperty(Resource blank, Property p, String value) {
		if ("".equals(value.trim()))
			return;
		blank.addProperty(p, value.trim());
	}

	/**
	 * Helper method to conditionally add a schema to a URI unless it's there, or "" if link is empty.
	 */
	protected String fixSchema(String schema, String link) {
		if ("".equals(link))
			return "";
		if (link.startsWith(schema+":"))
				return link;
		return schema+":"+link;
	}

	/**
	 * Helper method that adds a URI property to a node.
	 */
	protected void conditionallyAddResourceProperty(Resource card,
			Property property, String uri) {
		if ("".equals(uri.trim()))
				return;
		card.addProperty(property, card.getModel().createResource(uri));
	}
}