package org.deri.any23.extractor;

import org.openrdf.model.URI;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * It defines the signature of a generic Extractor
 *
 * @param <Input> the type of the input data to be processed
 *
 */
public interface Extractor<Input> {

    /**
     * This interface specializes an {@link org.deri.any23.extractor.Extractor} able to handle
     * {@link java.net.URI} as input format. Use it if you need to fetch a document before the extraction
     */
    public interface BlindExtractor extends Extractor<URI> {
    }

    /**
     * This interface specializes an {@link org.deri.any23.extractor.Extractor} able to handle
     * {@link java.io.InputStream} as input format.
     */
    public interface ContentExtractor extends Extractor<InputStream> {
    }

    /**
     * This interface specializes an {@link org.deri.any23.extractor.Extractor} able to handle
     * {@link org.w3c.dom.Document} as input format.
     */
    public interface TagSoupDOMExtractor extends Extractor<Document> {
    }

    /**
     * Executes the extractor. Will be invoked only once, extractors are
     * not reusable.
     *
     * @param in          The extractor's input
     * @param documentURI The document's URI
     * @param out         Sink for extracted data
     * @throws IOException         On error while reading from the input stream
     * @throws ExtractionException On other error, such as parse errors
     */
    void run(Input in, URI documentURI, ExtractionResult out)
            throws IOException, ExtractionException;

    /**
     * Returns a {@link org.deri.any23.extractor.ExtractorDescription} of this extractor.
     */
    ExtractorDescription getDescription();
}
