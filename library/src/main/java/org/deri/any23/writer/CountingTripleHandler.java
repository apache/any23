package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * A simple {@link TripleHandler} that merely counts the number
 * of triples it has received.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CountingTripleHandler implements TripleHandler {
    private int count = 0;

    public int getCount() {
        return count;
    }

    public void startDocument(URI documentURI) {
        // ignore
    }

    public void openContext(ExtractionContext context) {
        // ignore
    }

    public void closeContext(ExtractionContext context) {
        // ignore
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        count++;
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        // ignore
    }

    public void close() {
        // ignore
    }

    public void endDocument(URI documentURI) {
        //ignore
        ;
    }

    public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
        //ignore
        ;
    }
}
