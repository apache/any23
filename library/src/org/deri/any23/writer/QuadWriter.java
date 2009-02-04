package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.vocab.Vocabulary;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * A triple handler that converts triples to quads by using the
 * document URI of each triple's context as the graph name.
 * Optionally, a metadata graph can be specified; for each
 * document URI, it will record which extractors were used on
 * it, and the document title if any.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QuadWriter implements TripleHandler {
	
	public static interface QuadHandler {
		void writeQuad(Node s, Node p, Node o, Node g);
		void close();
	}

	private final QuadHandler quadHandler;
	private final Node metaGraph;
	
	public QuadWriter(QuadHandler quadHandler) {
		this(quadHandler, null);
	}
	
	public QuadWriter(QuadHandler quadHandler, String metadataGraphURI) {
		this.quadHandler = quadHandler;
		this.metaGraph = (metadataGraphURI == null) 
				? null : Node.createURI(metadataGraphURI);
	}
	
	public void openContext(ExtractionContext context) {
		if (metaGraph == null) return;
		quadHandler.writeQuad(
				Node.createURI(context.getDocumentURI()), 
				Vocabulary.EXTRACTOR.asNode(), 
				Vocabulary.getExtractorResource(context.getExtractorName()).asNode(), 
				metaGraph);
	}
	
	public void closeContext(ExtractionContext context) {
		// do nothing
	}

	public void receiveTriple(Node s, Node p, Node o, ExtractionContext context) {
		quadHandler.writeQuad(s, p, o, Node.createURI(context.getDocumentURI()));
	}
	
	public void receiveLabel(String label, ExtractionContext context) {
		if (metaGraph == null || !context.isDocumentContext()) return;
		quadHandler.writeQuad(
				Node.createURI(context.getDocumentURI()), 
				RDFS.label.asNode(), 
				Node.createURI(label), 
				metaGraph);
	}

	public void close() {
		quadHandler.close();
	}
}
