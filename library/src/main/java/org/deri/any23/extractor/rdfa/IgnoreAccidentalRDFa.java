package org.deri.any23.extractor.rdfa;

import java.util.ArrayList;
import java.util.List;

import org.deri.any23.vocab.XHTML;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 * An {@link RDFHandler} that suppresses statements from document
 * that only contain "accidental" RDFa, like stylesheet links and
 * other non-RDFa uses of HTML's @@rel and @@rev attributes.
 *   
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class IgnoreAccidentalRDFa implements RDFHandler {
	private final RDFHandler wrapped;
	private final List<Statement> queue = new ArrayList<Statement>();
	private boolean blocked = true;
	
	public IgnoreAccidentalRDFa(RDFHandler wrapped) {
		this.wrapped = wrapped;
	}
	
	public void endRDF() throws RDFHandlerException {
		wrapped.endRDF();
	}

	public void handleComment(String comment) throws RDFHandlerException {
		wrapped.handleComment(comment);
	}

	public void handleNamespace(String prefix, String uri)
			throws RDFHandlerException {
		wrapped.handleNamespace(prefix, uri);
	}

	public void handleStatement(Statement stmt) throws RDFHandlerException {
		if (blocked) {
			queue.add(stmt);
			if (stmt.getPredicate().stringValue().startsWith(XHTML.NS)) {
				unblock();
			}
			return;
		}
		wrapped.handleStatement(stmt);
	}

	public void startRDF() throws RDFHandlerException {
		wrapped.startRDF();
	}
	
	private void unblock() throws RDFHandlerException {
		blocked = false;
		for (Statement s: queue) {
			wrapped.handleStatement(s);
		}
	}
}
