package org.deri.any23.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deri.any23.TestHelper;
import org.deri.any23.rdf.Prefixes;
import org.junit.Assert;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class MockExtractionResult implements ExtractionResult {
	private final List<ExtractionContext> localContexts = new ArrayList<ExtractionContext>();
	private final List<Boolean> contextsClosed = new ArrayList<Boolean>();
	private final URI documentURI;
	private ExtractionContext documentContext = null;

	private final Collection<Expectation> expectations = new ArrayList<Expectation>();

	private boolean usedDocumentContext = false;
	private int usedLocalContexts = 0;
	private Collection<Statement> writtenTriples = new ArrayList<Statement>();
	
	public MockExtractionResult(URI documentURI) {
		this.documentURI = documentURI;
	}
	
	public void expectContexts(boolean defaultContext, int localContextCount) {
		expectations.add(new ContextExpectation(defaultContext, localContextCount));
	}

	public void expectTripleCount(int expectedTripleCount) {
		expectations.add(new TripleCountExpectation(expectedTripleCount));
	}
	
	public void expectTriple(String s, String p, String o) {
		expectations.add(new TripleExpectation(TestHelper.toTriple(s, p, o)));
	}
	
	public void closeContext(ExtractionContext context) {
		int index = localContexts.indexOf(context);
		if (index == -1 || contextsClosed.get(index)) {
			throw new IllegalStateException("Context not open: " + context);
		}
		contextsClosed.set(index, true);
	}

	public URI getDocumentURI() {
		return documentURI;
	}
	
	public ExtractionContext getDocumentContext(Extractor<?> extractor) {
		usedDocumentContext = true;
		if (documentContext == null) {
			documentContext = new ExtractionContext(
					extractor.getDescription(), documentURI, null);
		}
		return documentContext;
	}
	
	public ExtractionContext getDocumentContext(Extractor<?> extractor, Prefixes prefixes) {
		return getDocumentContext(extractor);
	}
	
	public ExtractionContext createContext(Extractor<?> extractor) {
		usedLocalContexts++;
		ExtractionContext result = new ExtractionContext(
				extractor.getDescription(), 
				documentURI, null, Integer.toString(usedLocalContexts));
		localContexts.add(result);
		contextsClosed.add(false);
		verifyNotViolated();
		return result;
	}

	public ExtractionContext createContext(Extractor<?> extractor, Prefixes prefixes) {
		return createContext(extractor);
	}

	@Override
	public void writeTriple(Resource s, URI p, Value o) {
		writtenTriples.add(TestHelper.triple(s, p, o));
		verifyNotViolated();
	}

	public void verify() {
		for (Expectation expectation : expectations) {
			expectation.verifyFulfilled();
		}
	}
	
	private void verifyNotViolated() {
		for (Expectation expectation : expectations) {
			expectation.verifyNotViolated();
		}
	}
	
	private interface Expectation {
		void verifyNotViolated();
		void verifyFulfilled();
	}
	
	private class ContextExpectation implements Expectation {
		private final boolean expectDocumentContext;
		private final int expectLocalContextCount;
		ContextExpectation(boolean expectDefault, int expectLocal) {
			this.expectDocumentContext = expectDefault;
			this.expectLocalContextCount = expectLocal;
		}
		public void verifyNotViolated() {
			if (usedDocumentContext && !expectDocumentContext) {
				Assert.fail("Expectation violated: Extractor should not request default context");
			}
			if (usedLocalContexts > expectLocalContextCount) {
				Assert.fail("Expectation violated: Used more than " + expectLocalContextCount + " local contexts");
			}
		}
		public void verifyFulfilled() {
			if (usedLocalContexts != expectLocalContextCount) {
				Assert.fail("Expected " + expectLocalContextCount + " contexts used, was " + usedLocalContexts);
			}
			if (!usedDocumentContext && expectDocumentContext) {
				Assert.fail("Expectation violated: Extractor should request default context");
			}
			verifyNotViolated();
		}
	}
	
	private class TripleCountExpectation implements Expectation {
		private final int expectedTripleCount;
		TripleCountExpectation(int expected) {
			this.expectedTripleCount = expected;
		}
		public void verifyNotViolated() {
			if (writtenTriples.size() > expectedTripleCount) {
				Assert.fail("Expectation violated: Written more than " + expectedTripleCount + " triples");
			}
		}
		public void verifyFulfilled() {
			if (writtenTriples.size() != expectedTripleCount) {
				Assert.fail("Expected " + expectedTripleCount + " triples, was " + writtenTriples.size());
			}
		}
	}
	
	private class TripleExpectation implements Expectation {
		private final Statement triple;
		TripleExpectation(Statement triple) {
			this.triple = triple;
		}
		public void verifyNotViolated() { }
		public void verifyFulfilled() {
			for (Statement t : writtenTriples) {
				if (triple.equals(t)) {
					return;
				}
			}
			Assert.fail("Expected triple not written: " + triple);
		}
	}
}
