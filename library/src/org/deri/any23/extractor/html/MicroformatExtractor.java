package org.deri.any23.extractor.html;

import java.io.IOException;
import java.net.URISyntaxException;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;

/**
 * TODO Validate comments/documentation throughout this file
 * 
 * The abstract base class for any Microformat extractor.
 * It requires a method that returns the name of the microformat,
 * and a method that performs the extraction and writes the results
 * to an RDF model.
 * 
 * The nodes generated in the model can have any name or implicit label
 * but if possible they SHOULD have names (either URIs or AnonId) that
 * are uniquely derivable from their position in the DOM tree, so that
 * multiple extractors can merge information.
 * 
 * TODO: Deep class hierarchies are ugly, we should do something without protected fields
 */
public abstract class MicroformatExtractor implements TagSoupDOMExtractor {
	protected HTMLDocument document;
	protected ExtractionResult out;
	protected java.net.URI baseURI;
	protected final ValueFactory valueFactory =new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());
		
	public void run(Document in, ExtractionResult out) throws IOException,
			ExtractionException {
		try {
			this.document = new HTMLDocument(in);
			this.out = out;
			this.baseURI = new java.net.URI(valueFactory.createURI(out.getDocumentURI()).toString());
			extract(out.getDocumentContext(this));
		} catch (URISyntaxException ex) {
			throw new ExtractionException(ex);
		}
	}

	/**
	 * Performs the extraction of the data and writes them to the model.
	 */
	protected abstract boolean extract(ExtractionContext context);
	
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
	protected void conditionallyAddStringProperty(Resource subject, URI p, String value) {
		if ("".equals(value.trim()))
			return;
		out.writeTriple(subject, p, valueFactory.createLiteral(value.trim()), out.getDocumentContext(this));
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
	protected void conditionallyAddResourceProperty(Resource subject,
			URI property, String uri) {
		if ("".equals(uri.trim()))
				return;
		out.writeTriple(subject, property, valueFactory.createURI(uri), out.getDocumentContext(this));
	}

	public abstract ExtractorDescription getDescription();
}