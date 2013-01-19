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
import org.apache.any23.vocab.FOAF;
import org.apache.any23.vocab.XFN;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;

/**
 * Extractor for the <a href="http://microformats.org/wiki/xfn">XFN</a>
 * microformat.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XFNExtractor implements TagSoupDOMExtractor {

    private static final FOAF vFOAF = FOAF.getInstance();
    private static final XFN vXFN  = XFN.getInstance();

    private final static Any23ValueFactoryWrapper factoryWrapper =
            new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());

    private HTMLDocument     document;
    private ExtractionResult out;

    @Override
    public ExtractorDescription getDescription() {
        return XFNExtractorFactory.getDescriptionInstance();
    }

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        factoryWrapper.setIssueReport(out);
        try {
            document = new HTMLDocument(in);
            this.out = out;

            BNode subject = factoryWrapper.createBNode();
            boolean foundAnyXFN = false;
            final URI documentURI = extractionContext.getDocumentURI();
            for (Node link : document.findAll("//A[@rel][@href]")) {
                foundAnyXFN |= extractLink(link, subject, documentURI);
            }
            if (!foundAnyXFN) return;
            out.writeTriple(subject, RDF.TYPE, vFOAF.Person);
            out.writeTriple(subject, vXFN.mePage, documentURI);
        } finally {
            factoryWrapper.setIssueReport(null);
        }
    }

    private boolean extractLink(Node firstLink, BNode subject, URI documentURI)
    throws ExtractionException {
        String href = firstLink.getAttributes().getNamedItem("href").getNodeValue();
        String rel = firstLink.getAttributes().getNamedItem("rel").getNodeValue();

        String[] rels = rel.split("\\s+");
        URI link = document.resolveURI(href);
        if (containsRelMe(rels)) {
            if (containsXFNRelExceptMe(rels)) {
                return false;    // "me" cannot be combined with any other XFN values
            }
            out.writeTriple(subject, vXFN.mePage, link);
            out.writeTriple(documentURI, vXFN.getExtendedProperty("me"), link);
        } else {
            BNode person2 = factoryWrapper.createBNode();
            boolean foundAnyXFNRel = false;
            for (String aRel : rels) {
                foundAnyXFNRel |= extractRel(aRel, subject, documentURI, person2, link);
            }
            if (!foundAnyXFNRel) {
                return false;
            }
            out.writeTriple(person2, RDF.TYPE, vFOAF.Person);
            out.writeTriple(person2, vXFN.mePage, link);
        }
        return true;
    }

    private boolean containsRelMe(String[] rels) {
        for (String rel : rels) {
            if ("me".equals(rel.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsXFNRelExceptMe(String[] rels) {
        for (String rel : rels) {
            if (!"me".equals(rel.toLowerCase()) && vXFN.isXFNLocalName(rel)) {
                return true;
            }
        }
        return false;
    }

    private boolean extractRel(String rel, BNode person1, URI uri1, BNode person2, URI uri2) {
        URI peopleProp = vXFN.getPropertyByLocalName(rel);
        URI hyperlinkProp = vXFN.getExtendedProperty(rel);
        if (peopleProp == null) {
            return false;
        }
        out.writeTriple(person1, peopleProp, person2);
        out.writeTriple(uri1, hyperlinkProp, uri2);
        return true;
    }

}