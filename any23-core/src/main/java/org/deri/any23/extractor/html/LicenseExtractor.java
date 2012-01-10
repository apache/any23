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

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.XHTML;
import org.openrdf.model.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Arrays;

/**
 * Extractor for the <a href="http://microformats.org/wiki/rel-license">rel-license</a>
 * microformat.
 * <p/>
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak
 */
public class LicenseExtractor implements TagSoupDOMExtractor {

    private static final XHTML vXHTML = XHTML.getInstance();

    public final static ExtractorFactory<LicenseExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-license",
                    PopularPrefixes.createSubset("xhtml"),
                    Arrays.asList("text/html;q=0.01", "application/xhtml+xml;q=0.01"),
                    "example-mf-license.html",
                    LicenseExtractor.class
            );

    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        HTMLDocument document = new HTMLDocument(in);
        final URI documentURI = extractionContext.getDocumentURI();
        for (Node node : DomUtils.findAll(in, "//A[@rel='license']/@href")) {
            String link = node.getNodeValue();
            if ("".equals(link)) {
                out.notifyError(
                        ExtractionResult.ErrorLevel.WARN,
                        String.format(
                                "Invalid license link detected within document %s.",
                                documentURI.toString()
                        ),
                        0, 0
                );
                continue;
            }
            out.writeTriple(documentURI, vXHTML.license, document.resolveURI(link));
        }
    }

    public ExtractorDescription getDescription() {
        return factory;
    }
    
}
