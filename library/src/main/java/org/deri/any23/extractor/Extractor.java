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

    public interface BlindExtractor extends Extractor<URI> {
    }

    public interface ContentExtractor extends Extractor<InputStream> {
    }

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
