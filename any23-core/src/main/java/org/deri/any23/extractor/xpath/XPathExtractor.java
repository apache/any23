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

package org.deri.any23.extractor.xpath;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.openrdf.model.URI;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of an {@link org.deri.any23.extractor.Extractor.TagSoupDOMExtractor} able to
 * apply {@link XPathExtractionRule}s and generate <i>quads</i>.
 *
 * @see XPathExtractionRule
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class XPathExtractor implements Extractor.TagSoupDOMExtractor {

    public final static String NAME = "html-xpath";

    public final static ExtractorFactory<XPathExtractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    null,
                    Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                    null,
                    XPathExtractor.class
            );

    private final List<XPathExtractionRule> xPathExtractionRules = new ArrayList<XPathExtractionRule>();

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

    public void run(Document in, URI documentURI, ExtractionResult out)
    throws IOException, ExtractionException {
        for(XPathExtractionRule rule : xPathExtractionRules) {
            if(rule.acceptURI(documentURI)) {
                rule.process(in, out);
            }
        }
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

}
