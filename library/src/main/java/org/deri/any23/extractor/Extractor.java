package org.deri.any23.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.openrdf.model.URI;
import org.w3c.dom.Document;

public interface Extractor<Input> {

	public interface BlindExtractor extends Extractor<URI> {}
	public interface ContentExtractor extends Extractor<InputStream> {}
	public interface TagSoupDOMExtractor extends Extractor<Document> {}
	
	/**
	 * Executes the extractor. Will be invoked only once, extractors are
	 * not reusable.
	 * 
	 * @param in The extractor's input
	 * @param documentURI The document's URI
	 * @param out Sink for extracted data
	 * @throws IOException On error while reading from the input stream
	 * @throws ExtractionException On other error, such as parse errors
	 */
	void run(Input in, URI documentURI, ExtractionResult out) 
	throws IOException, ExtractionException;
	
	/**
	 * Returns a description of this extractor.
	 */
	ExtractorDescription getDescription();
}
