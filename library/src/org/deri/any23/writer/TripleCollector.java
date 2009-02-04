package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.rdf.Prefixes;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * A {@link TripleHandler} that collects all triples into
 * an RDF graph, ignoring context and metadata. The RDF
 * graph can be passed to the constructor, or by default
 * an in-memory graph will be used.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TripleCollector implements TripleHandler {
	private final Model model;
	private final Prefixes prefixes = new Prefixes();
	
	public TripleCollector() {
		this(ModelFactory.createDefaultModel());
	}
	
	public TripleCollector(Model destination) {
		model = destination;
	}
	
	public Model getModel() {
		return model;
	}
	
	public void closeContext(ExtractionContext context) {
		// ignore
	}

	public void openContext(ExtractionContext context) {
		prefixes.add(context.getPrefixes());
	}

	public void receiveTriple(Node s, Node p, Node o, ExtractionContext context) {
		model.getGraph().add(Triple.create(s, p, o));
	}
	
	public void receiveLabel(String label, ExtractionContext context) {
		// ignore metadata
	}

	public void close() {
		model.setNsPrefixes(prefixes.asMap());
	}
}
