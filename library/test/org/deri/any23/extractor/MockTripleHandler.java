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

	public void expectClose() {
		expectations.add("close()");
	}
	
	public void expectOpenContext(String extractorName, String documentURI) {
		expectations.add("openContext(" + extractorName + ", " + documentURI + ", default)");
	}
	
	public void expectOpenContext(String extractorName, String documentURI, String contextLocalName) {
		expectations.add("openContext(" + extractorName + ", " + documentURI + ", " + contextLocalName + ")");
	}
	
	public void expectCloseContext(String extractorName, String documentURI) {
		expectations.add("closeContext(" + extractorName + ", " + documentURI + ", default)");
	}
	
	public void expectCloseContext(String extractorName, String documentURI, String contextLocalName) {
		expectations.add("closeContext(" + extractorName + ", " + documentURI + ", " + contextLocalName + ")");
	}

	public void expectTriple(Resource s, URI p, Value o) {
		expectations.add("triple(" + TestHelper.triple(s, p, o) + ", default)");
	}
	
	public void expectTriple(Resource s, URI p, Value o, String contextLocalName) {
		expectations.add("triple(" + TestHelper.triple(s, p, o) + ", " + contextLocalName + ")");
	}
	
	public void verify() {
		if (!expectations.isEmpty()) {
			Assert.fail("Expected " + expectations.size() + 
				" more invocation(s), first: " + expectations.get(0));
		}
	}
	
	public void openContext(ExtractionContext context) {
		assertNextExpectation("openContext(" + context.getExtractorName()
				+ ", " + context.getDocumentURI() + ", "
				+ (context.isDocumentContext() ? "default" : context.getLocalID()) + ")");
	}

	public void closeContext(ExtractionContext context) {
		assertNextExpectation("closeContext(" + context.getExtractorName()
				+ ", " + context.getDocumentURI() + ", "
				+ (context.isDocumentContext() ? "default" : context.getLocalID()) + ")");
	}

	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		assertNextExpectation("triple(" + TestHelper.triple(s, p, o) + ", "
				+ (context.isDocumentContext() ? "default" : context.getLocalID()) + ")");
	}
	
	public void receiveLabel(String label, ExtractionContext context) {
		// TODO Auto-generated method stub
	}

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
