package org.deri.any23.filter;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper around a {@link TripleHandler} that can block and unblock
 * calls to the handler, either for the entire document, or for
 * individual {@link ExtractionContext}s. A document is initially
 * blocked and must be explicitly unblocked. Contexts are initially
 * unblocked and must be explicitly blocked. Unblocking a document
 * unblocks all contexts as well.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExtractionContextBlocker implements TripleHandler {

    private TripleHandler wrapped;
    private Map<String, ValvedTriplePipe> contextQueues = new HashMap<String, ValvedTriplePipe>();
    private boolean documentBlocked;

    public ExtractionContextBlocker(TripleHandler wrapped) {
        this.wrapped = wrapped;
    }

    public boolean isDocBlocked() {
        return documentBlocked;
    }

    public void startDocument(URI documentURI) {
        wrapped.startDocument(documentURI);
        documentBlocked = true;
    }

    public void openContext(ExtractionContext context) {
        contextQueues.put(context.getUniqueID(), new ValvedTriplePipe(context));
    }

    public void blockContext(ExtractionContext context) {
        if (!documentBlocked) return;
        contextQueues.get(context.getUniqueID()).block();
    }

    public void unblockContext(ExtractionContext context) {
        contextQueues.get(context.getUniqueID()).unblock();
    }

    public void closeContext(ExtractionContext context) {
        // We'll close all contexts when the document is finished.
    }

    public void unblockDocument() {
        if (!documentBlocked) return;
        documentBlocked = false;
        for (ValvedTriplePipe pipe : contextQueues.values()) {
            pipe.unblock();
        }
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        contextQueues.get(context.getUniqueID()).receiveTriple(s, p, o);
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        contextQueues.get(context.getUniqueID()).receiveNamespace(prefix, uri);
    }

    public void close() {
        closeDocument();
        wrapped.close();
    }

    public void endDocument(URI documentURI) {
        closeDocument();
        wrapped.endDocument(documentURI);
    }

    private void closeDocument() {
        for (ValvedTriplePipe pipe : contextQueues.values()) {
            pipe.close();
        }
        contextQueues.clear();
    }

    private class ValvedTriplePipe {

        private final ExtractionContext context;
        private final List<Resource> subjects = new ArrayList<Resource>();
        private final List<URI> predicates = new ArrayList<URI>();
        private final List<Value> objects = new ArrayList<Value>();
        private final List<String> prefixes = new ArrayList<String>();
        private final List<String> uris = new ArrayList<String>();
        private boolean blocked = false;
        private boolean hasReceivedTriples = false;

        ValvedTriplePipe(ExtractionContext context) {
            this.context = context;
        }

        void receiveTriple(Resource s, URI p, Value o) {
            if (blocked) {
                subjects.add(s);
                predicates.add(p);
                objects.add(o);
            } else {
                sendTriple(s, p, o);
            }
        }

        void receiveNamespace(String prefix, String uri) {
            if (blocked) {
                prefixes.add(prefix);
                uris.add(uri);
            } else {
                sendNamespace(prefix, uri);
            }
        }

        void block() {
            if (blocked) return;
            blocked = true;
        }

        void unblock() {
            if (!blocked) return;
            blocked = false;
            for (int i = 0; i < prefixes.size(); i++) {
                sendNamespace(prefixes.get(i), uris.get(i));
            }
            for (int i = 0; i < subjects.size(); i++) {
                sendTriple(subjects.get(i), predicates.get(i), objects.get(i));
            }
        }

        void close() {
            if (hasReceivedTriples) {
                wrapped.closeContext(context);
            }
        }

        private void sendTriple(Resource s, URI p, Value o) {
            if (!hasReceivedTriples) {
                wrapped.openContext(context);
                hasReceivedTriples = true;
            }
            wrapped.receiveTriple(s, p, o, context);
        }

        private void sendNamespace(String prefix, String uri) {
            if (!hasReceivedTriples) {
                wrapped.openContext(context);
                hasReceivedTriples = true;
            }
            wrapped.receiveNamespace(prefix, uri, context);
        }
    }

    public void setContentLength(long contentLength) {
        // Ignore.
    }
}
