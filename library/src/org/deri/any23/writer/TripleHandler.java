package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;

import com.hp.hpl.jena.graph.Node;

//implemented by consumers. Simple triple handlers can ignore
// all the context stuff and just have to deal with receiveTriple
public interface TripleHandler {
	
	// informs the handler that a new context has been established.
	// Contexts are not guaranteed to receive any triples, so they
	// might be closed without any triples
	void openContext(ExtractionContext context);
	
	// informs the handler that no more triples will come from a
	// previously opened context. All contexts are guaranteed to
	// be closed before the final close(). The document context
	// for each document is guaranteed to be closed after all
	// local contexts of that document.
	void closeContext(ExtractionContext context);
	
	// will be invoked with a currently open context
	void receiveTriple(Node s, Node p, Node o, ExtractionContext context);

	// labels a currently open context; a context may remain unlabelled
	void receiveLabel(String label, ExtractionContext context);
	
	// will be called last and exactly once
	void close();
}