package org.deri.any23.writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.deri.any23.extractor.ExtractionContext;

import com.hp.hpl.jena.graph.Node;


public class BenchmarkTripleHandler implements TripleHandler {

	private TripleHandler	_underlyingHandler;

	private class StatObject{
		int methodCalls = 0;
		int triples = 0;
		long runtime =0;
		long intStart =0;
		/**
		 * 
		 */
		public void interimStart() {
			intStart = System.currentTimeMillis();
		}
		/**
		 * 
		 */
		public void interimStop() {
			runtime+=(System.currentTimeMillis()-intStart);
			intStart=0;
		}
	}
	
	Map<String,StatObject> stats = new HashMap<String, StatObject>();
	
	/**
	 * 
	 */
	public BenchmarkTripleHandler(TripleHandler tripleHandler) {
		_underlyingHandler = tripleHandler;

	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#close()
	 */
	@Override
	public void close() {
		_underlyingHandler.close();
		
	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#closeContext(org.deri.any23.extractor.ExtractionContext)
	 */
	@Override
	public void closeContext(ExtractionContext context) {
		if(!stats.containsKey(context.getExtractorName())){stats.put(context.getExtractorName(), new StatObject());}
		stats.get(context.getExtractorName()).interimStop();
		_underlyingHandler.closeContext(context);
		
	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#openContext(org.deri.any23.extractor.ExtractionContext)
	 */
	@Override
	public void openContext(ExtractionContext context) {
		if(!stats.containsKey(context.getExtractorName())){stats.put(context.getExtractorName(), new StatObject());}
		stats.get(context.getExtractorName()).methodCalls++;
		stats.get(context.getExtractorName()).interimStart();
		_underlyingHandler.openContext(context);
	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#receiveLabel(java.lang.String, org.deri.any23.extractor.ExtractionContext)
	 */
	@Override
	public void receiveLabel(String label, ExtractionContext context) {
		_underlyingHandler.receiveLabel(label, context);
		
	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#receiveTriple(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, org.deri.any23.extractor.ExtractionContext)
	 */
	@Override
	public void receiveTriple(Node s, Node p, Node o, ExtractionContext context) {
		if(!stats.containsKey(context.getExtractorName())){stats.put(context.getExtractorName(), new StatObject());}
		stats.get(context.getExtractorName()).triples++;
		_underlyingHandler.receiveTriple(s, p, o, context);
		
	}

	/**
	 * @return
	 */
	public String report() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, StatObject>ent: stats.entrySet()) {
			sb.append("\n>Extractor: ").append(ent.getKey());
			sb.append("\n   -total calls: ").append(ent.getValue().methodCalls);
			sb.append("\n   -total triples: ").append(ent.getValue().triples);
			sb.append("\n   -total runtime: ").append(ent.getValue().runtime).append(" ms!");
			if(ent.getValue().runtime != 0)
			sb.append("\n   -tripls/ms: ").append(ent.getValue().triples/ent.getValue().runtime);
			if(ent.getValue().methodCalls != 0)
			sb.append("\n   -ms/calls: ").append(ent.getValue().runtime/ent.getValue().methodCalls);
			
		}
		
		return sb.toString();
	}
	
}