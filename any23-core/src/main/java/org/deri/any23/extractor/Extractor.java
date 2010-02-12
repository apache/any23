/**
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.deri.any23.extractor;

import org.openrdf.model.URI;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

/**
 * It defines the signature of a generic Extractor.
 *
 * @param <Input> the type of the input data to be processed.
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
        
        /**
         * If <code>true</code>, the extractor will stop at first parsing error,
         * if<code>false</code> the extractor will attempt to ignore all parsing errors.
         *
         * @param f tolerance flag.
         */
        void setStopAtFirstError(boolean f);

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
