package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

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
	
	public void closeContext(ExtractionContext context) {
		// ignore
	}

	public void openContext(ExtractionContext context) {
		try {
			for (String prefix: context.getPrefixes().allPrefixes()) {
				writer.handleNamespace(prefix, 
						context.getPrefixes().getNamespaceURIFor(prefix));
			}
		} catch (RDFHandlerException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		try {
			writer.handleStatement(
					ValueFactoryImpl.getInstance().createStatement(s, p, o));
		} catch (RDFHandlerException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void receiveLabel(String label, ExtractionContext context) {
		// ignore metadata
	}

	public void close() {
		try {
			writer.endRDF();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}
}
