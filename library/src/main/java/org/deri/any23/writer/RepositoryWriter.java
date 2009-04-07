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
	
	public void close() {
		// ignore
	}

	public void closeContext(ExtractionContext context) {
		// ignore
	}

	public void openContext(ExtractionContext context) {
		try {
			for (String prefix: context.getPrefixes().allPrefixes()) {
				conn.setNamespace(prefix, 
						context.getPrefixes().getNamespaceURIFor(prefix));
			}
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void receiveLabel(String label, ExtractionContext context) {
		// ignore
	}

	public void receiveTriple(Resource s, URI p, Value o,
			ExtractionContext context) {
		try {
			conn.add(conn.getValueFactory().createStatement(s, p, o));
		} catch (RepositoryException ex) {
			throw new RuntimeException(ex);
		}
	}
}
