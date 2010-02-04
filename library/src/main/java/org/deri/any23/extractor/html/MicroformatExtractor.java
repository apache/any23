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

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * The abstract base class for any
 * <a href="microformats.org/">Microformat specification</a> extractor.
 */
public abstract class MicroformatExtractor implements TagSoupDOMExtractor {

    protected HTMLDocument document;
    protected URI documentURI;
    protected ExtractionResult out;
    protected final Any23ValueFactoryWrapper valueFactory =
            new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());

    /**
     * Returns the description of this extractor.
     *
     * @return a human readable description.
     */
    public abstract ExtractorDescription getDescription();

    /**
     * Performs the extraction of the data and writes them to the model.
     * The nodes generated in the model can have any name or implicit label
     * but if possible they </i>SHOULD</i> have names (either URIs or AnonId) that
     * are uniquely derivable from their position in the DOM tree, so that
     * multiple extractors can merge information.
     */
    protected abstract boolean extract() throws ExtractionException;

    public void run(Document in, URI documentURI, ExtractionResult out)
    throws IOException, ExtractionException {
        this.document = new HTMLDocument(in);
        this.documentURI = documentURI;
        this.out = out;
        extract();
    }

    /**
     * Helper method that adds a literal property to a node.
     */
    protected boolean conditionallyAddStringProperty(Resource subject, URI p, String value) {
        if (value == null) return false;
        value = value.trim();
        if ("".equals(value)) return false;
        out.writeTriple(subject, p, valueFactory.createLiteral(value));
        return true;
    }

    protected URI fixLink(String link) {
        return fixLink(link, null);
    }

    /**
     * Helper method to conditionally add a schema to a URI unless it's there, or null if link is empty.
     * TODO #3 - Move this to the same class as fixURI().
     */
    protected URI fixLink(String link, String defaultSchema) {
        if (link == null) return null;
        link = fixWhiteSpace(link);
        if ("".equals(link)) return null;
        if (defaultSchema != null && !link.startsWith(defaultSchema + ":")) {
            link = defaultSchema + ":" + link;
        }
        return valueFactory.fixURI(link);
    }

    protected String fixWhiteSpace(String name) {
        return name.replaceAll("\\s+", " ").trim();
    }

    /**
     * Helper method that adds a URI property to a node.
     */
    protected boolean conditionallyAddResourceProperty(
            Resource subject,
            URI property,
            URI uri
    ) {
        if (uri == null) return false;
        out.writeTriple(subject, property, uri);
        return true;
    }

}