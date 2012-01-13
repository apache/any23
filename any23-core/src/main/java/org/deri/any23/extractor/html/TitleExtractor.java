/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Arrays;

/**
 * Extracts the value of the &lt;title&gt; element of an
 * HTML or XHTML page.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TitleExtractor implements TagSoupDOMExtractor {

    public static final String NAME = "html-head-title";

    private static final DCTERMS vDCTERMS = DCTERMS.getInstance();

    public final static ExtractorFactory<TitleExtractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    PopularPrefixes.createSubset("dcterms"),
                    Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                    "example-title.html",
                    TitleExtractor.class
            );

    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        final Any23ValueFactoryWrapper valueFactory = new Any23ValueFactoryWrapper(
            ValueFactoryImpl.getInstance(), out, extractionContext.getDefaultLanguage()
        );
        
        try {
            String title = DomUtils.find(in, "/HTML/HEAD/TITLE/text()").trim();
            if (title != null && (title.length() != 0)) {
                out.writeTriple(extractionContext.getDocumentURI(), vDCTERMS.title, valueFactory.createLiteral(title));
            }
        } finally {
            valueFactory.setErrorReporter(null);
        }
    }

    public ExtractorDescription getDescription() {
        return factory;
    }
    
}
