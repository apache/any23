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

package org.apache.any23.extractor.xpath;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.eclipse.rdf4j.model.IRI;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of an {@link org.apache.any23.extractor.Extractor.TagSoupDOMExtractor} able to
 * apply {@link XPathExtractionRule}s and generate <i>quads</i>.
 *
 * @see XPathExtractionRule
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class XPathExtractor implements Extractor.TagSoupDOMExtractor {

    private final List<XPathExtractionRule> xPathExtractionRules = new ArrayList<>();

    public XPathExtractor() {
        //default constructor
    }
    
    public XPathExtractor(List<XPathExtractionRule> rules) {
        xPathExtractionRules.addAll(rules);
    }

    public void add(XPathExtractionRule rule) {
        xPathExtractionRules.add(rule);
    }

    public void remove(XPathExtractionRule rule) {
        xPathExtractionRules.remove(rule);
    }

    public boolean contains(XPathExtractionRule rule) {
        return xPathExtractionRules.contains(rule);
    }

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    )
    throws IOException, ExtractionException {
        final IRI documentIRI = extractionContext.getDocumentIRI();
        for(XPathExtractionRule rule : xPathExtractionRules) {
            if(rule.acceptIRI(documentIRI)) {
                rule.process(in, out);
            }
        }
    }

    @Override
    public ExtractorDescription getDescription() {
        return XPathExtractorFactory.getDescriptionInstance();
    }

}
