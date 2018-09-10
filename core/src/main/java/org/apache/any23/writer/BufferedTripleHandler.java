package org.apache.any23.writer;

import com.google.common.base.Throwables;
import org.apache.any23.extractor.ExtractionContext;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.model.impl.TreeModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Collects all statements until end document.
 *
 * All statements are kept within {@link Model}.
 *
 * @author Jacek Grzebyta (jgrzebyta@apache.org)
 */
public class BufferedTripleHandler implements TripleHandler {

    private static final Logger log = LoggerFactory.getLogger(BufferedTripleHandler.class);
    private TripleHandler underlying;
    private static boolean isDocumentFinish = false;

    private static class ContextHandler {
        ContextHandler(ExtractionContext ctx, Model m) {
            extractionContext = ctx;
            extractionModel = m;
        }
        ExtractionContext extractionContext;
        Model extractionModel;
    }

    private static class WorkflowContext {
        WorkflowContext(TripleHandler underlying) {
            this.rootHandler = underlying;
        }


        Stack<String> extractors = new Stack<>();
        Map<String, ContextHandler> modelMap = new TreeMap<>();
        IRI documentIRI = null;
        TripleHandler rootHandler ;
    }

    public BufferedTripleHandler(TripleHandler underlying) {
        this.underlying = underlying;

        // hide model in the thread
        WorkflowContext wc = new WorkflowContext(underlying);
        BufferedTripleHandler.workflowContext.set(wc);
    }

    private static final ThreadLocal<WorkflowContext> workflowContext = new ThreadLocal<>();

    /**
     * Returns model which contains all other models.
     * @return
     */
    public static Model getModel() {
        return BufferedTripleHandler.workflowContext.get().modelMap.values().stream()
                .map(ch -> ch.extractionModel)
                .reduce(new LinkedHashModelFactory().createEmptyModel(), (mf, exm) -> {
                    mf.addAll(exm);
                    return mf;
                });
    }

    @Override
    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        BufferedTripleHandler.workflowContext.get().documentIRI = documentIRI;
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        //
    }

    @Override
    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context) throws TripleHandlerException {
        getModelForContext(context).add(s,p,o,g);
    }

    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        getModelForContext(context).setNamespace(prefix, uri);
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

        String lastExtractor = ((Stack<String>) workflowContext.extractors).peek();

        Map<String, ContextHandler> models = workflowContext.modelMap;
        TripleHandler underlying = workflowContext.rootHandler;

        // final populate underlying rdf handler.
        underlying.startDocument(workflowContext.documentIRI);

        ExtractionContext outContext = models.get(lastExtractor).extractionContext;
        Model outModel = models.get(lastExtractor).extractionModel;

        outModel.stream().forEach( st -> {
            try {
                underlying.receiveTriple(st.getSubject(), st.getPredicate(), st.getObject(), (IRI) st.getContext(), outContext);
            } catch (TripleHandlerException e) {
                Throwables.propagateIfPossible(e, RuntimeException.class);
            }
        });

        underlying.endDocument(workflowContext.documentIRI);
        underlying.close();
    }

    private static Model getModelForContext(ExtractionContext ctx) {
        Map<String, ContextHandler> modelMap = BufferedTripleHandler.workflowContext.get().modelMap;
        Stack<String> extractors = BufferedTripleHandler.workflowContext.get().extractors;

        if (modelMap.containsKey(ctx.getUniqueID())) {
            return  modelMap.get(ctx.getUniqueID()).extractionModel;
        } else {
            Model empty = new TreeModelFactory().createEmptyModel();
            modelMap.put(ctx.getUniqueID(), new ContextHandler(ctx, empty));
            extractors.push(ctx.getUniqueID());
            return empty;
        }
    }
}
