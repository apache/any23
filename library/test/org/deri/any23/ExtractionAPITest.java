package org.deri.any23;

import java.net.URI;

import junit.framework.TestCase;

import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.extractor.example.ExampleExtractor;
import org.deri.any23.writer.CountingTripleHandler;

public class ExtractionAPITest extends TestCase {
	private static final String exampleDoc = "http://example.com/";
	
	public void testDirectInstantiation() throws Exception {
		URI in = new URI(exampleDoc);
		CountingTripleHandler out = new CountingTripleHandler();
		ExtractionResultImpl writer = new ExtractionResultImpl(exampleDoc, out);
		new ExampleExtractor().run(in, writer);
		writer.close();
		assertEquals(1, out.getCount());
	}

	public void testEmptyStreamDoesNotGenerateTriples() throws Exception {
		URI in = new URI(exampleDoc);
		CountingTripleHandler out = new CountingTripleHandler();
		ExtractionResultImpl writer = new ExtractionResultImpl(exampleDoc, out);
		new ExampleExtractor().run(in, writer);
		writer.close();
		assertEquals(0, out.getCount());
	}
}
