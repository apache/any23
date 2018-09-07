package org.apache.any23.writer;

import com.google.common.base.Throwables;
import org.apache.any23.extractor.ExtractionContext;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
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

    private static final Logger log = LoggerFactory.getLogger(BufferedTripleHandler.class);
    private Model buffer;
    private TripleHandler underlying;
    private static boolean isDocumentFinish = false;

    private static class WorkflowContext {
        WorkflowContext(TripleHandler underlying, Model model) {
            this.model = model;
            this.rootHandler = underlying;
        }

        Model model;
        ExtractionContext context = null;
        IRI documentIRI = null;
        TripleHandler rootHandler ;
    }

    public BufferedTripleHandler(TripleHandler underlying, Model buffer) {
        this.buffer = buffer;
        this.underlying = underlying;

        // hide model in the thread
        WorkflowContext wc = new WorkflowContext(underlying, buffer);
        BufferedTripleHandler.workflowContext.set(wc);
    }

    public BufferedTripleHandler(TripleHandler underlying) {
        this(underlying, new TreeModelFactory().createEmptyModel());
    }

    private static final ThreadLocal<WorkflowContext> workflowContext = new ThreadLocal<>();

    public static Model getModel() {
        return BufferedTripleHandler.workflowContext.get().model;
    }

    @Override
    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        BufferedTripleHandler.workflowContext.get().documentIRI = documentIRI;
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        BufferedTripleHandler.workflowContext.get().context = context;
    }

    @Override
    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context) throws TripleHandlerException {
        buffer.add(s, p, o, g);
    }

    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        buffer.setNamespace(new SimpleNamespace(prefix, uri));
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        //
    }

    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        BufferedTripleHandler.isDocumentFinish = true;
    }

    @Override
    public void setContentLength(long contentLength) {
        underlying.setContentLength(contentLength);
    }

    @Override
    public void close() throws TripleHandlerException {
        underlying.close();
    }

    /**
     * Releases content of the model into underlying writer.
     */
    public static void releaseModel() throws TripleHandlerException {
        if(!BufferedTripleHandler.isDocumentFinish) {
            throw new RuntimeException("Before releasing document should be finished.");
        }

        WorkflowContext workflowContext = BufferedTripleHandler.workflowContext.get();
        Model buffer = workflowContext.model;
        TripleHandler underlying = workflowContext.rootHandler;


        // final populate underlying rdf handler.
        underlying.startDocument(workflowContext.documentIRI);
        buffer.stream().forEach(st -> {
            try {
                underlying.receiveTriple(st.getSubject(), st.getPredicate(), st.getObject(), (IRI) st.getContext(), workflowContext.context);
            } catch (TripleHandlerException e) {
                Throwables.propagateIfPossible(e, RuntimeException.class);
            }
        });
        underlying.endDocument(workflowContext.documentIRI);
        underlying.close();
    }
}
