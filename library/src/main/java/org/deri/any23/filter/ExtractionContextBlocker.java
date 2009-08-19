package org.deri.any23.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class ExtractionContextBlocker implements TripleHandler {
	private TripleHandler wrapped;
	private Map<String, ValvedTriplePipe> queues = new HashMap<String, ValvedTriplePipe>();
	private boolean documentUnblocked = false;

	
	
	public ExtractionContextBlocker(TripleHandler wrapped) {
		this.wrapped = wrapped;
	}
	
	public boolean isDocBlocked(){
		return !documentUnblocked;
	}
	
	@Override
	public void startDocument(URI documentURI) {
		closeDocument();
	}
	
	@Override
	public void openContext(ExtractionContext context) {
		queues.put(context.getUniqueID(), new ValvedTriplePipe(context));
	}
	
	public void blockContext(ExtractionContext context) {
		if (documentUnblocked) return;
		queues.get(context.getUniqueID()).block();
	}
	
	public void unblockContext(ExtractionContext context) {
		queues.get(context.getUniqueID()).unblock();
	}
	
	@Override
	public void closeContext(ExtractionContext context) {
		// We'll close all contexts when the document is finished
	}
	
	public void unblockDocument() {
		if (documentUnblocked) return;
		documentUnblocked = true;
		for (ValvedTriplePipe pipe: queues.values()) {
			pipe.unblock();
		}
	}
	
	@Override
	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		queues.get(context.getUniqueID()).receiveTriple(s, p, o);
	}

	@Override
	public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
		queues.get(context.getUniqueID()).receiveNamespace(prefix, uri);
	}

	@Override
	public void close() {
		closeDocument();
		wrapped.close();
	}

	private void closeDocument() {
		documentUnblocked = false;
		for (ValvedTriplePipe pipe: queues.values()) {
			pipe.close();
		}
		queues.clear();
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

	@Override
	public void endDocument(URI documentURI) {
		wrapped.endDocument(documentURI);
	}
	
	@Override
	public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
		//ignore
		;
	}
}
