package org.deri.any23.filter;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.html.TitleExtractor;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * A {@link TripleHandler} that suppresses output of the
 * {@link TitleExtractor} unless some other triples could
 * be parsed from the document. This is used when we don't
 * want to have single-triple RDF documents around that
 * contain only the title triple.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class IgnoreTitlesOfEmptyDocuments implements TripleHandler {
    private final ExtractionContextBlocker blocker;

    public IgnoreTitlesOfEmptyDocuments(TripleHandler wrapped) {
        blocker = new ExtractionContextBlocker(wrapped);
    }

    public void startDocument(URI documentURI) {
        blocker.startDocument(documentURI);
    }

    public void openContext(ExtractionContext context) {
        blocker.openContext(context);
        if (isTitleContext(context)) {
            blocker.blockContext(context);
        }
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        if (!isTitleContext(context)) {
            blocker.unblockDocument();
        }
        blocker.receiveTriple(s, p, o, context);
    }

    public void receiveNamespace(String prefix, String uri,
                                 ExtractionContext context) {
        blocker.receiveNamespace(prefix, uri, context);
    }

    public void closeContext(ExtractionContext context) {
        blocker.closeContext(context);
    }

    public void close() {
        blocker.close();
    }

    private boolean isTitleContext(ExtractionContext context) {
        return context.getExtractorName().equals(TitleExtractor.NAME);
    }

    public void endDocument(URI documentURI) {
        blocker.endDocument(documentURI);
    }

    public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
        //ignore
        ;
    }
}
