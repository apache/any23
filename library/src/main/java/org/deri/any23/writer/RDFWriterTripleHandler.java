package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

/**
 * A {@link TripleHandler} that writes triples to a Sesame
 * {@link RDFWriter}, e.g. for serialization using one of
 * Sesame's writers.
 *  
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
class RDFWriterTripleHandler implements TripleHandler {
	private final RDFWriter writer;
	
	RDFWriterTripleHandler(RDFWriter destination) {
		writer = destination;
		try {
			writer.startRDF();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void startDocument(URI documentURI) {
		// ignore
	}
	
	public void openContext(ExtractionContext context) {
		// ignore
	}

	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		try {
			writer.handleStatement(
					ValueFactoryImpl.getInstance().createStatement(s, p, o));
		} catch (RDFHandlerException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
		try {
			writer.handleNamespace(prefix, uri);
		} catch (RDFHandlerException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void closeContext(ExtractionContext context) {
		// ignore
	}
	
	public void close() {
		try {
			writer.endRDF();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void endDocument(URI documentURI) {
		;
	}
	@Override
	public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
		//ignore
		;
	}
}
