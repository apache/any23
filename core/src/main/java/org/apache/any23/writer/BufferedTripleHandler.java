package org.apache.any23.writer;

import com.google.common.base.Throwables;
import org.apache.any23.extractor.ExtractionContext;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.TreeModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collects all statements until end document.
 *
 * All statements are kept within {@link Model}.
 *
 * @author Jacek Grzebyta (jgrzebyta@apache.org)
 */
public class BufferedTripleHandler implements TripleHandler {

    private final Logger log = LoggerFactory.getLogger(BufferedTripleHandler.class);
    private Model buffer;

    private final TripleHandler underlying;
    private ExtractionContext extractionContext;

    private static final ThreadLocal<Model> globalModel = new ThreadLocal<>();

    public static Model getModel() {
        return BufferedTripleHandler.globalModel.get();
    }

    public BufferedTripleHandler(TripleHandler underlying, Model buffer) {
        this.buffer = buffer;
        this.underlying = underlying;

        // hide model in the thread
        globalModel.set(buffer);
    }

    public BufferedTripleHandler(TripleHandler underlying) {
        this(underlying, new TreeModelFactory().createEmptyModel());
    }

    @Override
    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        underlying.startDocument(documentIRI);
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        this.extractionContext = context;
        underlying.openContext(context);
    }

    @Override
    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context) throws TripleHandlerException {
        buffer.add(s, p, o, g);
    }

    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        buffer.setNamespace(new SimpleNamespace(prefix, uri));
        underlying.receiveNamespace(prefix, uri, context);
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        underlying.closeContext(context);
    }

    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        // final populate underlying rdf handler.
        buffer.stream().forEach(st->{
            try {
                underlying.receiveTriple(st.getSubject(), st.getPredicate(), st.getObject(), (IRI) st.getContext(), extractionContext);
            } catch (TripleHandlerException e) {
                Throwables.propagateIfPossible(e, RuntimeException.class);
            }
        });
        underlying.endDocument(documentIRI);
    }

    @Override
    public void setContentLength(long contentLength) {
        underlying.setContentLength(contentLength);
    }

    @Override
    public void close() throws TripleHandlerException {
        underlying.close();
    }
}
