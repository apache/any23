package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A triple handler that converts triples to quads by using the
 * document URI of each triple's context as the graph name.
 * Optionally, a metadata graph can be specified; for each
 * document URI, it will record which extractors were used on
 * it, and the document title if any.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QuadWriter implements TripleHandler {

    private OutputStream out;

    public QuadWriter(OutputStream os) {
        out = os;
    }

    public void startDocument(URI documentURI) {
        // Empty.
    }

    public void openContext(ExtractionContext context) {
        // Empty.
    }

    public void closeContext(ExtractionContext context) {
        // Empty.
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(NTriplesUtil.toNTriplesString(s)).append(" ");
            sb.append(NTriplesUtil.toNTriplesString(p)).append(" ");
            sb.append(NTriplesUtil.toNTriplesString(o)).append(" ");
            sb.append(NTriplesUtil.toNTriplesString(context.getDocumentURI())).append(" .\n");
            out.write(sb.toString().getBytes());
            out.flush();
        } catch (IOException ioe) {
            throw new RuntimeException("Error while writing on output stream.", ioe);
        }
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        // ignore prefix mappings
    }

    public void close() {
        try {
            out.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Error while closing output stream.", ioe);
        }
    }

    public void endDocument(URI documentURI) {
        // Empty.
    }

    public void setContentLength(long contentLength) {
        // Empty.
    }
}
