package org.deri.any23.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.deri.any23.rdf.Prefixes;

import com.hp.hpl.jena.graph.Node;

/**
 * An implementation of {@link ExtractionResult} that stores
 * all calls and allows playback of the calls onto another
 * ExtractionResult object at a later time.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExtractionResultStore implements ExtractionResult {
	private final String documentURI;
	private final Collection<ExtractionContext> openLocalContexts = 
			new LinkedList<ExtractionContext>();
	private final List<ExtractionResultEvent> events = new ArrayList<ExtractionResultEvent>();
	private int nextLocalContextID = 0;
	private ExtractionContext documentContext = null;	// lazy initialization
	private boolean invalid = false;
	
	public ExtractionResultStore(String documentURI) {
		this.documentURI = documentURI;
	}
	
	public boolean isEmpty() {
		for (ExtractionResultEvent event: events) {
			if (!event.isEmpty()) return false;
		}
		return true;
	}
	
	public void activate() {
		this.invalid = false;
	}
	
	public void deactivate() {
		this.invalid = true;
	}
	
	public void add(final ExtractionResultStore otherStore) {
		events.add(new ExtractionResultEvent() {
			public void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts) {
				otherStore.writeTo(out);
			}
			public boolean isEmpty() {
				return otherStore.isEmpty();
			}
		});
	}
	
	public void writeTo(ExtractionResult out) {
		if (invalid) return;
		for (ExtractionContext context: openLocalContexts) {
			closeContext(context);
		}
		if (documentContext != null) {
			events.add(new ExtractionResultEvent() {
				public void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts) {
					out.closeContext(contexts.get(documentContext.getUniqueID()));
				}
				public boolean isEmpty() {
					return true;
				}
			});
			documentContext = null;
		}
		Map<String, ExtractionContext> contexts = new HashMap<String, ExtractionContext>();
		for (ExtractionResultEvent event: events) {
			event.writeTo(out, contexts);
		}
	}
	
	public String getDocumentURI() {
		return documentURI;
	}
	
	public ExtractionContext getDocumentContext(Extractor<?> extractor) {
		return getDocumentContext(extractor, null);
	}
	
	public ExtractionContext getDocumentContext(final Extractor<?> extractor, final Prefixes contextPrefixes) {
		if (documentContext == null) {
			documentContext = new ExtractionContext(extractor.getDescription(), documentURI, contextPrefixes);
			events.add(new ExtractionResultEvent() {
				public void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts) {
					contexts.put(documentContext.getUniqueID(), out.getDocumentContext(extractor, contextPrefixes));
				}
				public boolean isEmpty() {
					return true;
				}
			});
		}
		return documentContext;
	}

	public ExtractionContext createContext(Extractor<?> extractor) {
		return createContext(extractor, null);
	}
	
	public ExtractionContext createContext(final Extractor<?> extractor, final Prefixes contextPrefixes) {
		nextLocalContextID++;
		final ExtractionContext result = new ExtractionContext(
				extractor.getDescription(), documentURI, contextPrefixes, "item" + Integer.toString(nextLocalContextID));
		openLocalContexts.add(result);
		events.add(new ExtractionResultEvent() {
			public void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts) {
				contexts.put(result.getUniqueID(), out.createContext(extractor, contextPrefixes));
			}
			public boolean isEmpty() {
				return true;
			}
		});
		return result;
	}
	
	public void closeContext(final ExtractionContext context) {
		if (!openLocalContexts.remove(context)) {
			throw new IllegalArgumentException("Not an open context: " + context);
		}
		events.add(new ExtractionResultEvent() {
			public void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts) {
				out.closeContext(contexts.get(context.getUniqueID()));
			}
			public boolean isEmpty() {
				return true;
			}
		});
	}

	public void setLabel(final String label, final ExtractionContext context) {
		if (!context.isDocumentContext()) {
			checkOpen(context);
		}
		events.add(new ExtractionResultEvent() {
			public void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts) {
				out.setLabel(label, contexts.get(context.getUniqueID()));
			}
			public boolean isEmpty() {
				return false;
			}
		});
	}

	public void writeTriple(final Node s, final Node p, final Node o, final ExtractionContext context) {
		if (!context.isDocumentContext()) {
			checkOpen(context);
		}
		events.add(new ExtractionResultEvent() {
			public void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts) {
				out.writeTriple(s, p, o, contexts.get(context.getUniqueID()));
			}
			public boolean isEmpty() {
				return false;
			}
		});
	}

	private void checkOpen(ExtractionContext context) {
		if (!openLocalContexts.contains(context)) {
			throw new IllegalStateException("Not an open context: " + context);
		}		
	}
	
	private interface ExtractionResultEvent {
		void writeTo(ExtractionResult out, Map<String, ExtractionContext> contexts);
		boolean isEmpty();
	}
}
