package org.deri.any23.writer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;


public class LoggingTripleHandler implements TripleHandler {
	private final TripleHandler underlyingHandler;
	private final PrintWriter destination;
	private final Map<String,Integer> contextTripleMap = new HashMap<String, Integer>();
	private long _startTime;
	private long _contentLength;
	
	public LoggingTripleHandler(TripleHandler tripleHandler, PrintWriter destination) {
		underlyingHandler = tripleHandler;
		this.destination = destination;
	}

	public void startDocument(URI documentURI) {
		underlyingHandler.startDocument(documentURI);
		_startTime = System.currentTimeMillis();
	}
	
	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#close()
	 */
	public void close() {
		underlyingHandler.close();
		destination.flush();
		destination.close();
	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#closeContext(org.deri.any23.extractor.ExtractionContext)
	 */
	public void closeContext(ExtractionContext context) {
		underlyingHandler.closeContext(context);
	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#openContext(org.deri.any23.extractor.ExtractionContext)
	 */
	public void openContext(ExtractionContext context) {
		underlyingHandler.openContext(context);
	}

	/* (non-Javadoc)
	 * @see org.deri.any23.writer.TripleHandler#receiveTriple(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, org.deri.any23.extractor.ExtractionContext)
	 */
	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		underlyingHandler.receiveTriple(s, p, o, context);
		Integer i = contextTripleMap.get(context.getExtractorName());
		if(i ==null) i = 0;
		contextTripleMap.put(context.getExtractorName(), (i+1));
	}

	public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
		underlyingHandler.receiveNamespace(prefix, uri, context);
	}

	@Override
	public void endDocument(URI documentURI) {
		long elapsedTime = System.currentTimeMillis()- _startTime;
		boolean success = true;
//		if(underlyingHandler instanceof ExtractionContextBlocker){
//			success= ((ExtractionContextBlocker)underlyingHandler).isDocBlocked();
//		}
//		if(success){
		StringBuffer sb = new StringBuffer("[");
		for(Entry<String,Integer> ent: contextTripleMap.entrySet()){
			sb.append(" ").append(ent.getKey()).append(":").append(ent.getValue());
			if(ent.getValue()>0){
				success =true;
			}
		}
		sb.append("]");
		destination.println(documentURI+"\t"+_contentLength+"\t"+elapsedTime+"\t"+success+"\t"+sb.toString());
		contextTripleMap.clear();
	}
	
	@Override
	public void setContentLength(long contentLength) {
		_contentLength = contentLength;
		//ignore
		;
	}
}