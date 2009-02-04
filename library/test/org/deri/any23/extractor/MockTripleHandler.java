package org.deri.any23.extractor;

import java.util.LinkedList;
import java.util.List;

import org.deri.any23.writer.TripleHandler;
import org.junit.Assert;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

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

	public void expectTriple(Node s, Node p, Node o) {
		expectations.add("triple(" + Triple.create(s, p, o) + ", default)");
	}
	
	public void expectTriple(Node s, Node p, Node o, String contextLocalName) {
		expectations.add("triple(" + Triple.create(s, p, o) + ", " + contextLocalName + ")");
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

	public void receiveTriple(Node s, Node p, Node o, ExtractionContext context) {
		assertNextExpectation("triple(" + Triple.create(s, p, o) + ", "
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
