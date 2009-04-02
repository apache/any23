package org.deri.any23.extractor;

import java.util.Collection;
import java.util.LinkedList;

import org.deri.any23.rdf.Prefixes;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * A default implementation of {@link ExtractionResult}; it receives
 * extraction output from one {@link Extractor} working on one document,
 * and passes the output on to a {@link TripleHandler}. It deals with
 * details such as creation of {@link ExtractionContext} objects
 * and closing any open contexts at the end of extraction.
 * 
 * The {@link #close()} method must be invoked after the extractor has
 * finished processing.
 *
 * There is usually no need to provide additional implementations
 * of the ExtractionWriter interface.
 * 
 * TODO: Implementation doesn't ensure that openContext() is reported
 * to the tripleHandler for the document context. It's only reported
 * if the extractor actually requests the document context. That might
 * be bad, because the TripleHandler might want to know exactly which
 * extractors have been run on which files, for reporting purposes.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExtractionResultImpl implements ExtractionResult {
	private final String documentURI;
	private final TripleHandler tripleHandler;
	private final Collection<ExtractionContext> openLocalContexts = 
			new LinkedList<ExtractionContext>();
	private int nextLocalContextID = 0;
	
	private ExtractionContext documentContext = null;	// lazy initialization
	
	public ExtractionResultImpl(String documentURI, TripleHandler tripleHandler) {
		this.documentURI = documentURI;
		this.tripleHandler = tripleHandler;
	}
	
	public String getDocumentURI() {
		return documentURI;
	}
	
	public ExtractionContext getDocumentContext(Extractor<?> extractor) {
		return getDocumentContext(extractor, null);
	}
	
	public ExtractionContext getDocumentContext(Extractor<?> extractor, Prefixes contextPrefixes) {
		if (documentContext == null) {
			documentContext = new ExtractionContext(extractor.getDescription(), documentURI, contextPrefixes);
			tripleHandler.openContext(documentContext);
		}
		return documentContext;
	}

	public ExtractionContext createContext(Extractor<?> extractor) {
		return createContext(extractor, null);
	}
	
	public ExtractionContext createContext(Extractor<?> extractor, Prefixes contextPrefixes) {
		nextLocalContextID++;
		ExtractionContext result = new ExtractionContext(
				extractor.getDescription(), documentURI, contextPrefixes, "item" + Integer.toString(nextLocalContextID));
		openLocalContexts.add(result);
		tripleHandler.openContext(result);
		return result;
	}
	
	public void closeContext(ExtractionContext context) {
		if (!openLocalContexts.remove(context)) {
			throw new IllegalArgumentException("Not an open context: " + context);
		}
		tripleHandler.closeContext(context);
	}

	public void setLabel(String label, ExtractionContext context) {
		if (!context.isDocumentContext()) {
			checkOpen(context);
		}
		tripleHandler.receiveLabel(label, context);
	}

	public void writeTriple(Resource s, URI p, Value o, ExtractionContext context) {
		if (!context.isDocumentContext()) {
			checkOpen(context);
		}
		tripleHandler.receiveTriple(s, p, o, context);
	}

	public void close() {
		if (documentContext != null) {
			tripleHandler.closeContext(documentContext);
		}
		for (ExtractionContext context : openLocalContexts) {
			tripleHandler.closeContext(context);
		}
	}
	
	private void checkOpen(ExtractionContext context) {
		if (!openLocalContexts.contains(context)) {
			throw new IllegalStateException("Not an open context: " + context);
		}		
	}
}
