package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.vocab.ANY23;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

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
		void writeQuad(Resource s, URI p, Value o, URI g);
		void close();
	}

	private final QuadHandler quadHandler;
	private final URI metaGraph;
	
	public QuadWriter(QuadHandler quadHandler) {
		this(quadHandler, null);
	}
	
	public QuadWriter(QuadHandler quadHandler, URI metadataGraphURI) {
		this.quadHandler = quadHandler;
		this.metaGraph = (metadataGraphURI == null) ? null : metadataGraphURI;
	}

	public void startDocument(URI documentURI) {
		// ignore
	}
	
	public void openContext(ExtractionContext context) {
		if (metaGraph == null) return;
		quadHandler.writeQuad(
				context.getDocumentURI(), 
				ANY23.EXTRACTOR, 
				ANY23.getExtractorResource(context.getExtractorName()), 
				metaGraph);
	}
	
	public void closeContext(ExtractionContext context) {
		// do nothing
	}

	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		quadHandler.writeQuad(s, p, o, context.getDocumentURI());
	}
	
	public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
		// ignore prefix mappings
	}
	
	public void close() {
		quadHandler.close();
	}
}
