package org.deri.any23.extractor.example;

import java.io.IOException;
import java.net.URI;

import junit.framework.TestCase;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.MockExtractionResult;


public class ExampleExtractorTest extends TestCase {
	private final static String exampleDoc = "http://example.com/";
	
	public void testDoesNotUseContexts() throws Exception {
		ExampleExtractor extractor = new ExampleExtractor();
		MockExtractionResult writer = new MockExtractionResult(exampleDoc);
		writer.expectContexts(true, 0);
		writer.expectTriple(exampleDoc, "a", "foaf:Document");
		writer.expectTripleCount(1);
		try {
			extractor.run(new URI(exampleDoc), writer);
		} catch (ExtractionException ex) {
			fail();
		} catch (IOException ex) {
			fail();
		}
		writer.verify();
	}
}
