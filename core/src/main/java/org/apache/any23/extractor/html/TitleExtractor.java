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

package org.apache.any23.extractor.html;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.apache.any23.vocab.DCTerms;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Extracts the value of the &lt;title&gt; element of an
 * HTML or XHTML page.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TitleExtractor implements TagSoupDOMExtractor {

    private static final DCTerms vDCTERMS = DCTerms.getInstance();

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        final Any23ValueFactoryWrapper valueFactory = new Any23ValueFactoryWrapper(
            SimpleValueFactory.getInstance(), out, extractionContext.getDefaultLanguage()
        );
        
        try {
            String title = DomUtils.find(in, "/HTML/HEAD/TITLE/text()").trim();
            if (title != null && (title.length() != 0)) {
                out.writeTriple(extractionContext.getDocumentIRI(), vDCTERMS.title, valueFactory.createLiteral(title));
            }
        } finally {
            valueFactory.setIssueReport(null);
        }
    }

    @Override
    public ExtractorDescription getDescription() {
        return TitleExtractorFactory.getDescriptionInstance();
    }
    
}
