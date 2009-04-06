package org.deri.any23.extractor.example;

import java.io.IOException;

import junit.framework.TestCase;

import org.deri.any23.TestHelper;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.MockExtractionResult;
import org.openrdf.model.URI;


public class ExampleExtractorTest extends TestCase {
	private final static String exampleDoc = "http://example.com/";
	private final static URI exampleURI = TestHelper.uri(exampleDoc);
	
	public void testDoesNotUseContexts() throws Exception {
		ExampleExtractor extractor = new ExampleExtractor();
		MockExtractionResult writer = new MockExtractionResult(exampleURI);
		writer.expectContexts(true, 0);
		writer.expectTriple(exampleDoc, "a", "foaf:Document");
		writer.expectTripleCount(1);
		try {
			extractor.run(exampleURI, writer);
		} catch (ExtractionException ex) {
			fail();
		} catch (IOException ex) {
			fail();
		}
		writer.verify();
	}
}
