package org.deri.any23;

import junit.framework.TestCase;

import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.extractor.example.ExampleExtractor;
import org.deri.any23.writer.CountingTripleHandler;
import org.openrdf.model.URI;

public class ExtractionAPITest extends TestCase {
	private static final String exampleDoc = "http://example.com/";
	private static final URI uri = TestHelper.uri(exampleDoc);
	
	public void testDirectInstantiation() throws Exception {
		CountingTripleHandler out = new CountingTripleHandler();
		ExampleExtractor extractor = new ExampleExtractor();
		ExtractionResultImpl writer = new ExtractionResultImpl(uri, extractor, out);
		extractor.run(uri, uri, writer);
		writer.close();
		assertEquals(1, out.getCount());
	}

	public void testEmptyStreamDoesNotGenerateTriples() throws Exception {
		CountingTripleHandler out = new CountingTripleHandler();
		ExampleExtractor extractor = new ExampleExtractor();
		ExtractionResultImpl writer = new ExtractionResultImpl(uri, extractor, out);
		extractor.run(uri, uri, writer);
		writer.close();
		assertEquals(0, out.getCount());
	}
}
