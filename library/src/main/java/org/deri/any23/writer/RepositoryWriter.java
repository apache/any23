package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class RepositoryWriter implements TripleHandler {
	private final RepositoryConnection conn;
	
	public RepositoryWriter(RepositoryConnection conn) {
		this.conn = conn;
	}
	
	public void startDocument(URI documentURI) {
		// ignore
	}
	
	public void openContext(ExtractionContext context) {
		// ignore
	}

	public void receiveTriple(Resource s, URI p, Value o,
			ExtractionContext context) {
		try {
			conn.add(conn.getValueFactory().createStatement(s, p, o), context.getDocumentURI());
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void receiveNamespace(String prefix, String uri,
			ExtractionContext context) {
		try {
			conn.setNamespace(prefix, uri);
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void closeContext(ExtractionContext context) {
		// ignore
	}

	public void close() {
		// ignore
	}

	@Override
	public void endDocument(URI documentURI) {
		// ignore
		;
	}	
	
	@Override
	public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
		//ignore
		;
	}
}
