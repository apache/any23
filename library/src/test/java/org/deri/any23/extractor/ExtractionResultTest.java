
package org.deri.any23.extractor;

import junit.framework.TestCase;

import org.deri.any23.TestHelper;
import org.deri.any23.extractor.example.ExampleExtractor;
import org.deri.any23.vocab.FOAF;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;


public class ExtractionResultTest extends TestCase {
	private final static String exampleDoc = "http://example.com/";
	private final static URI exampleResource = TestHelper.uri(exampleDoc);
	private final static String extractorName = "example";
	
	private Extractor<?> extractor;
	private MockTripleHandler handler;
	private ExtractionResultImpl writer;
	
	public void setUp() {
		extractor = new ExampleExtractor();
		handler = new MockTripleHandler();
		writer = new ExtractionResultImpl(exampleResource, handler);
	}

	public void testInvokesClose() {
		handler.expectClose();
		writer.close();
		handler.verify();
	}
	
	public void testOpenAndCloseDefaultContext() {
		handler.expectOpenContext(extractorName, exampleDoc);
		handler.expectCloseContext(extractorName, exampleDoc);
		handler.expectClose();
		assertEquals(
				new ExtractionContext(extractor.getDescription(), exampleResource, null), 
				writer.getDocumentContext(extractor));
		writer.close();
		handler.verify();
	}
	
	public void testOpenDocumentContextOnlyOnce() {
		handler.expectOpenContext(extractorName, exampleDoc);
		handler.expectCloseContext(extractorName, exampleDoc);
		handler.expectClose();
		writer.getDocumentContext(extractor);
		writer.getDocumentContext(extractor);
		writer.getDocumentContext(extractor);
		writer.close();
		handler.verify();
	}
	
	public void testLocalContextsAreClosedAutomatically() {
		handler.expectOpenContext(extractorName, exampleDoc, "item1");
		handler.expectOpenContext(extractorName, exampleDoc, "item2");
		handler.expectCloseContext(extractorName, exampleDoc, "item1");
		handler.expectCloseContext(extractorName, exampleDoc, "item2");
		handler.expectClose();
		writer.createContext(extractor);
		writer.createContext(extractor);
		writer.close();
		handler.verify();
	}
	
	public void testExplicitlyClosedContextsAreNotClosedAgain() {
		handler.expectOpenContext(extractorName, exampleDoc, "item1");
		handler.expectCloseContext(extractorName, exampleDoc, "item1");
		handler.expectOpenContext(extractorName, exampleDoc, "item2");
		handler.expectCloseContext(extractorName, exampleDoc, "item2");
		handler.expectClose();
		writer.closeContext(writer.createContext(extractor));
		writer.createContext(extractor);
		writer.close();
		handler.verify();
	}
	
	public void testCannotCloseDocumentContext() {
		handler.expectOpenContext(extractorName, exampleDoc);
		try {
			writer.closeContext(writer.getDocumentContext(extractor));
			fail("Should fail, document context cannot be closed");
		} catch(IllegalArgumentException ex) {
			// expected
		}
	}
	
	public void testTriplesWrittenToDocumentContextArePassedThrough() {
		handler.expectOpenContext(extractorName, exampleDoc);
		handler.expectTriple(exampleResource, RDF.TYPE, FOAF.Document);
		handler.expectCloseContext(extractorName, exampleDoc);
		handler.expectClose();
		writer.writeTriple(exampleResource, RDF.TYPE, FOAF.Document, 
				writer.getDocumentContext(extractor));
		writer.close();
		handler.verify();
	}
	
	public void testTriplesWrittenToLocalContextArePassedThrough() {
		handler.expectOpenContext(extractorName, exampleDoc, "item1");
		handler.expectTriple(exampleResource, RDF.TYPE, FOAF.Document, "item1");
		handler.expectCloseContext(extractorName, exampleDoc, "item1");
		handler.expectClose();
		writer.writeTriple(exampleResource, RDF.TYPE, FOAF.Document, 
				writer.createContext(extractor));
		writer.close();
		handler.verify();
	}
	
	public void testCannotWriteTriplesToClosedContext() {
		handler.expectOpenContext(extractorName, exampleDoc, "item1");
		handler.expectCloseContext(extractorName, exampleDoc, "item1");
		ExtractionContext context = writer.createContext(extractor);
		writer.closeContext(context);
		try {
			writer.writeTriple(exampleResource, RDF.TYPE, FOAF.Document, 
					context);
			fail("Should fail, context is not open");
		} catch (IllegalStateException ex) {
			// expected
		}
	}
}
