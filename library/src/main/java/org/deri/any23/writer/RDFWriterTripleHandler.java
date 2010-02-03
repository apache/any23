package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

/**
 * A {@link org.deri.any23.writer.TripleHandler} that writes
 * triples to a Sesame {@link org.openrdf.rio.RDFWriter},
 * eg for serialization using one of Sesame's writers.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
class RDFWriterTripleHandler implements TripleHandler {

    private final RDFWriter writer;
    private boolean closed = false;

    RDFWriterTripleHandler(RDFWriter destination) {
        writer = destination;
        try {
            writer.startRDF();
        } catch (RDFHandlerException e) {
            throw new RuntimeException(e);
        }
    }

    public void startDocument(URI documentURI) {
        // Empty.
    }

    public void openContext(ExtractionContext context) {
        // Empty.
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        try {
            writer.handleStatement(
                    ValueFactoryImpl.getInstance().createStatement(s, p, o));
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        try {
            writer.handleNamespace(prefix, uri);
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void closeContext(ExtractionContext context) {
        // Empty.
    }

    public void close() {
        if (closed) return;
        closed = true;
        try {
            writer.endRDF();
        } catch (RDFHandlerException e) {
            throw new RuntimeException(e);
        }
    }

    public void endDocument(URI documentURI) {
        // Empty.
    }

    public void setContentLength(long contentLength) {
        // Empty.
    }
    
}
