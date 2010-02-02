package org.deri.any23.extractor.rdf;

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

    public RDFHandlerAdapter(ExtractionResult target) {
        this.target = target;
    }

    public void startRDF() throws RDFHandlerException {
    }

    public void handleNamespace(String prefix, String uri) {
        target.writeNamespace(prefix, uri);
        // TODO (high) figure out the best way of handling namespace declarations in the content        
        // context.getPrefixes().addVolatile(prefix, uri);
    }

    public void handleStatement(Statement stmt) {
        target.writeTriple(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
    }

    public void handleComment(String comment) {
    }

    public void endRDF() throws RDFHandlerException {
    }

}
