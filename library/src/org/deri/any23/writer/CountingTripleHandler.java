package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;

import com.hp.hpl.jena.graph.Node;

/**
 * A simple {@link TripleHandler} that merely counts the number
 * of triples it has received.
 *  
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CountingTripleHandler implements TripleHandler {
	private int count = 0;
	
	public int getCount() {
		return count;
	}
	
	public void openContext(ExtractionContext context) {
		// ignore
	}
	
	public void closeContext(ExtractionContext context) {
		// ignore
	}

	public void receiveTriple(Node s, Node p, Node o, ExtractionContext context) {
		count++;
	}
	
	public void receiveLabel(String label, ExtractionContext context) {
		// ignore
	}

	public void close() {
		// ignore
	}
}
