package org.deri.any23.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * A {@link TripleHandler} that wraps zero or more other triple handlers
 * and dispatches all events to each of them.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CompositeTripleHandler implements TripleHandler {
    private Collection<TripleHandler> children = new ArrayList<TripleHandler>();

    public CompositeTripleHandler() {
        this(Collections.<TripleHandler>emptyList());
    }

    public CompositeTripleHandler(Collection<TripleHandler> children) {
        this.children.addAll(children);
    }

    public void addChild(TripleHandler child) {
        children.add(child);
    }

    public void startDocument(URI documentURI) {
        for (TripleHandler handler : children) {
            handler.startDocument(documentURI);
        }
    }

    public void openContext(ExtractionContext context) {
        for (TripleHandler handler : children) {
            handler.openContext(context);
        }
    }

    public void closeContext(ExtractionContext context) {
        for (TripleHandler handler : children) {
            handler.closeContext(context);
        }
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        for (TripleHandler handler : children) {
            handler.receiveTriple(s, p, o, context);
        }
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        for (TripleHandler handler : children) {
            handler.receiveNamespace(prefix, uri, context);
        }
    }

    public void close() {
        for (TripleHandler handler : children) {
            handler.close();
        }
    }

    public void endDocument(URI documentURI) {
        for (TripleHandler handler : children) {
            handler.endDocument(documentURI);
        }
    }

    public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
        //ignore
        ;
    }
}
