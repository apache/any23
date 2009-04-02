package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionResult;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 * An RDFHandler that relays statements and prefix definitions to
 * an {@link ExtractionResult}. Used to feed output from Sesame's
 * RDF parsers into Any23.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class RDFHandlerAdapter implements RDFHandler {
	private ExtractionResult target;
	private ExtractionContext context;
	
	public RDFHandlerAdapter(ExtractionResult target, ExtractionContext context) {
		this.target = target;
		this.context = context;
	}
	
	public void startRDF() throws RDFHandlerException { }
	
	public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
		// TODO figure out a way of handling namespace declarations in the content
//		context.getPrefixes().addVolatile(prefix, uri);
	}
	
	public void handleStatement(Statement stmt) throws RDFHandlerException {
		target.writeTriple(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), context);
	}
	
	public void handleComment(String comment) throws RDFHandlerException { }
	
	public void endRDF() throws RDFHandlerException { }
}
