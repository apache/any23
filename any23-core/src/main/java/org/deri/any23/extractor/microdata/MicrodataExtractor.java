/*
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
 */

package org.deri.any23.extractor.microdata;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.openrdf.model.URI;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Default implementation of <a href="http://www.w3.org/TR/microdata/">Microdata</a> extractor,
 * based on {@link TagSoupDOMExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataExtractor implements Extractor.TagSoupDOMExtractor {

    public ExtractorDescription getDescription() {
        throw new UnsupportedOperationException();
    }

    public void run(Document in, URI documentURI, ExtractionResult out) throws IOException, ExtractionException {
        throw new UnsupportedOperationException();
    }

    // 5.2.1
    private void processTitle(Document in, URI documentURI, ExtractionResult out) {

    }

    // 5.2.2
    private void processHREFElements(Document in, URI documentURI, ExtractionResult out) {

    }

    // 5.2.3
    private void processMetaElements(Document in, URI documentURI, ExtractionResult out) {

    }

    // 5.2.4
    private void processCiteElements(Document in, URI documentURI, ExtractionResult out) {

    }

    // 5.2.6
    private void processMicrodata(Document in, URI documentURI, ExtractionResult out) {

    }

    // 5.2 "generate the triples for an item"
    private void processType(ItemScope itemScope, URI documentURI, ExtractionResult out) {

        // 3,4,5 Add type triple and trailing char.

        // 6 Add property itemscope property triples.

    }
}
