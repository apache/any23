package org.deri.any23.extractor;

import java.util.LinkedList;
import java.util.List;

import org.deri.any23.TestHelper;
import org.deri.any23.writer.TripleHandler;
import org.junit.Assert;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class MockTripleHandler implements TripleHandler {
	private final List<String> expectations = new LinkedList<String>();

	public void expectStartDocument(URI documentURI) {
		expectations.add("startDocument(" + documentURI + ")");
	}
	
	public void expectClose() {
		expectations.add("close()");
	}
	
	public void expectOpenContext(String extractorName, URI documentURI, String localID) {
		expectations.add("openContext(" + new ExtractionContext(extractorName, documentURI, localID) + ")");
	}
	
	public void expectCloseContext(String extractorName, URI documentURI, String localID) {
		expectations.add("closeContext(" + new ExtractionContext(extractorName, documentURI, localID) + ")");
	}

	public void expectTriple(Resource s, URI p, Value o, String extractorName, URI documentURI, String localID) {
		expectations.add("triple(" + TestHelper.triple(s, p, o) + ", " + 
				new ExtractionContext(extractorName, documentURI, localID) + ")");
	}

	public void expectNamespace(String prefix, String uri, String extractorName, URI documentURI, String localID) {
		expectations.add("namespace(" + prefix + ", " + uri + ", " + 
				new ExtractionContext(extractorName, documentURI, localID) + ")");
	}
	public void verify() {
		if (!expectations.isEmpty()) {
			Assert.fail("Expected " + expectations.size() + 
				" more invocation(s), first: " + expectations.get(0));
		}
	}

	@Override
	public void startDocument(URI documentURI) {
		assertNextExpectation("startDocument(" + documentURI + ")");
	}
	
	@Override
	public void openContext(ExtractionContext context) {
		assertNextExpectation("openContext(" + context + ")");
	}

	@Override
	public void closeContext(ExtractionContext context) {
		assertNextExpectation("closeContext(" + context + ")");
	}

	@Override
	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		assertNextExpectation("triple(" + TestHelper.triple(s, p, o) + ", " + context + ")");
	}
	
	@Override
	public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
		assertNextExpectation("namespace(" + prefix + ", " + uri + ", " + context + ")");
	}
	
	@Override
	public void close() {
		assertNextExpectation("close()");
	}
	
	private void assertNextExpectation(String invocation) {
		if (expectations.isEmpty()) {
			Assert.fail("Next expectation was <null>, invocation was " + invocation);
		}
		String expectation = expectations.remove(0);
		Assert.assertEquals("Invocation doesn't match expectation", expectation, invocation);
	}
}
