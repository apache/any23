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
import org.apache.any23.vocab.XHTML;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.List;

/**
 * This {@link org.apache.any23.extractor.Extractor.TagSoupDOMExtractor} implementation
 * retrieves the <code>LINK</code>s declared within the <code>HTML/HEAD</code> page header.
 */
public class HeadLinkExtractor implements TagSoupDOMExtractor {

    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        HTMLDocument html = new HTMLDocument(in);
        ValueFactory vf = SimpleValueFactory.getInstance();

        final List<Node> headLinkNodes = DomUtils.findAll(
                in,
                "/HTML/HEAD/LINK[(" +
                        "@type='application/rdf+xml' or " +
                        "@type='text/rdf' or " +
                        "@type='application/x-turtle' or " +
                        "@type='application/turtle' or " +
                        "@type='text/turtle' or " +
                        "@type='text/rdf+n3'" +
                        ") and @href and @rel]"
        );
        for (Node node : headLinkNodes) {
            final IRI href = html.resolveIRI(DomUtils.find(node, "@href"));
            final String rel = DomUtils.find(node, "@rel");
            out.writeTriple(
                    extractionContext.getDocumentIRI(),
                    vf.createIRI(XHTML.NS + rel),
                    href
            );
            final String title = DomUtils.find(node, "@title");
            if (title != null && !"".equals(title)) {
                out.writeTriple(
                        href,
                        getDescription().getPrefixes().expand("dcterms:title"),
                        vf.createLiteral(title)
                );
            }
            final String type = DomUtils.find(node, "@type");
            if (type != null && !"".equals(type)) {
                out.writeTriple(
                        href,
                        getDescription().getPrefixes().expand("dcterms:format"),
                        vf.createLiteral(type)
                );
            }
        }
    }

    @Override
    public ExtractorDescription getDescription() {
        return HeadLinkExtractorFactory.getDescriptionInstance();
    }

}
