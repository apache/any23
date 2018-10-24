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

package org.apache.any23.extractor.microdata;

import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.html.DomUtils;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.DCTerms;
import org.apache.any23.vocab.XHTML;
import org.eclipse.rdf4j.common.net.ParsedIRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation of <a href="http://www.w3.org/TR/microdata/">Microdata</a> extractor,
 * based on {@link org.apache.any23.extractor.Extractor.TagSoupDOMExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class MicrodataExtractor implements Extractor.TagSoupDOMExtractor {

    private static final IRI MICRODATA_ITEM
            = RDFUtils.iri("http://www.w3.org/1999/xhtml/microdata#item");

    private String documentLanguage;

    @Override
    public ExtractorDescription getDescription() {
        return MicrodataExtractorFactory.getDescriptionInstance();
    }

    /**
     * This extraction performs the
     * <a href="http://www.w3.org/TR/microdata/#rdf">Microdata to RDF conversion algorithm</a>.
     * A slight modification of the specification algorithm has been introduced
     * to avoid performing actions 5.2.1, 5.2.2, 5.2.3, 5.2.4 if step 5.2.6 doesn't detect any
     * Microdata.
     */
    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {

        final MicrodataParserReport parserReport = MicrodataParser.getMicrodata(in);
        if(parserReport.getErrors().length > 0) {
            notifyError(parserReport.getErrors(), out);
        }
        final ItemScope[] itemScopes = parserReport.getDetectedItemScopes();
        if (itemScopes.length == 0) {
            return;
        }

        final IRI documentIRI = extractionContext.getDocumentIRI();

        boolean isStrict = extractionParameters.getFlag("any23.microdata.strict");
        final IRI defaultNamespace;
        if (!isStrict) {
            defaultNamespace = RDFUtils.iri(extractionParameters.getProperty("any23.microdata.ns.default"));
            if (!defaultNamespace.getLocalName().isEmpty()) {
                throw new IllegalArgumentException("invalid namespace IRI: " + defaultNamespace);
            }
        } else {
            defaultNamespace = createNamespaceFromPrefix(documentIRI);
        }

        documentLanguage = getDocumentLanguage(in);

        /**
         * 5.2.6
         */
        final Map<ItemScope, Resource> mappings = new HashMap<>();
        for (ItemScope itemScope : itemScopes) {
            Resource subject = processType(itemScope, documentIRI, out, mappings, defaultNamespace);
            out.writeTriple(
                    documentIRI,
                    MICRODATA_ITEM,
                    subject
            );
        }

        /**
         * 5.2.1
         */
        processTitle(in, documentIRI, out);
        /**
         * 5.2.2
         */
        processHREFElements(in, documentIRI, out);
        /**
         * 5.2.3
         */
        processMetaElements(in, documentIRI, out);

        /**
         * 5.2.4
         */
        processCiteElements(in, documentIRI, out);
    }

    /**
     * Returns the {@link Document} language if declared, <code>null</code> otherwise.
     *
     * @param in a instance of {@link Document}.
     * @return the language declared, could be <code>null</code>.
     */
    private String getDocumentLanguage(Document in) {
        String lang = DomUtils.find(in, "string(/HTML/@lang)");
        if ("".equals(lang)) {
            return null;
        }
        return lang;
    }

    /**
     * Returns the {@link Node} language if declared, or the {@link Document} one
     * if not defined.
     *
     * @param node a {@link Node} instance.
     * @return the {@link Node} language or the {@link Document} one. Could be <code>null</code>
     */
    private String getLanguage(Node node) {
        Node nodeLang = node.getAttributes().getNamedItem("lang");
        if (nodeLang == null) {
            // if the element does not specify a lang, use the document one
            return documentLanguage;
        }
        return nodeLang.getTextContent();
    }

    /**
     * Implements step 5.2.1 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in          {@link Document} to be processed.
     * @param documentIRI Document current {@link IRI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processTitle(Document in, IRI documentIRI, ExtractionResult out) {
        NodeList titles = in.getElementsByTagName("title");
        // just one title is allowed.
        if (titles.getLength() == 1) {
            Node title = titles.item(0);
            String titleValue = title.getTextContent();
            Literal object;
            String lang = getLanguage(title);
            if (lang == null) {
                // unable to decide the language, leave it unknown
                object = RDFUtils.literal(titleValue);
            } else {
                object = RDFUtils.literal(titleValue, lang);
            }
            out.writeTriple(
                    documentIRI,
                    DCTerms.getInstance().title,
                    object
            );
        }
    }

    /**
     * Implements step 5.2.2 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in          {@link Document} to be processed.
     * @param documentIRI Document current {@link IRI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processHREFElements(Document in, IRI documentIRI, ExtractionResult out) {
        NodeList anchors = in.getElementsByTagName("a");
        for (int i = 0; i < anchors.getLength(); i++) {
            processHREFElement(anchors.item(i), documentIRI, out);
        }
        NodeList areas = in.getElementsByTagName("area");
        for (int i = 0; i < areas.getLength(); i++) {
            processHREFElement(areas.item(i), documentIRI, out);
        }
        NodeList links = in.getElementsByTagName("link");
        for (int i = 0; i < links.getLength(); i++) {
            processHREFElement(links.item(i), documentIRI, out);
        }
    }

    /**
     * Implements sub-step for 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param item        {@link Node} to be processed.
     * @param documentIRI Document current {@link IRI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processHREFElement(Node item, IRI documentIRI, ExtractionResult out) {
        Node rel = item.getAttributes().getNamedItem("rel");
        if (rel == null) {
            return;
        }
        Node href = item.getAttributes().getNamedItem("href");
        if (href == null) {
            return;
        }
        IRI iri;
        try {
            iri = toAbsoluteIRI(documentIRI, href.getTextContent());
        } catch (URISyntaxException e) {
            // cannot happen
            return;
        }

        String[] relTokens = rel.getTextContent().split(" ");
        Set<String> tokensWithNoDuplicates = new HashSet<>();
        for (String relToken : relTokens) {
            if (relToken.contains(":")) {
                // if contain semi-colon, skip
                continue;
            }
            if ("alternate".equals(relToken) || "stylesheet".equals(relToken)) {
                tokensWithNoDuplicates.add("ALTERNATE-STYLESHEET");
                continue;
            }
            tokensWithNoDuplicates.add(relToken.toLowerCase());
        }
        for (String token : tokensWithNoDuplicates) {
            IRI predicate = toAbsoluteIRI(token).orElseGet(() -> RDFUtils.iri(XHTML.NS + token.trim()));
            out.writeTriple(
                    documentIRI,
                    predicate,
                    iri
            );
        }
    }

    /**
     * Implements step 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in          {@link Document} to be processed.
     * @param documentIRI Document current {@link IRI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processMetaElements(Document in, IRI documentIRI, ExtractionResult out) {
        NodeList metas = in.getElementsByTagName("meta");
        for (int i = 0; i < metas.getLength(); i++) {
            Node meta = metas.item(i);
            String name    = DomUtils.readAttribute(meta, "name", null);
            String content = DomUtils.readAttribute(meta, "content", null);
            if (name != null && content != null) {
                Optional<IRI> nameIRI = toAbsoluteIRI(name);
                if (nameIRI.isPresent()) {
                    processMetaElement(
                            nameIRI.get(),
                            content,
                            getLanguage(meta),
                            documentIRI,
                            out
                    );
                } else {
                    processMetaElement(
                            name,
                            content,
                            getLanguage(meta),
                            documentIRI,
                            out
                    );
                }
            }
        }
    }

    /**
     * Implements sub step for 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param uri
     * @param content
     * @param language
     * @param documentIRI
     * @param out
     */
    private void processMetaElement(
            IRI uri,
            String content,
            String language,
            IRI documentIRI,
            ExtractionResult out
    ) {
        if (content.contains(":")) {
            // if it contains U+003A COLON, exit
            return;
        }
        Literal subject;
        if (language == null) {
            // ok, we don't know the language
            subject = RDFUtils.literal(content);
        } else {
            subject = RDFUtils.literal(content, language);
        }
        out.writeTriple(
                documentIRI,
                uri,
                subject
        );
    }

    /**
     * Implements sub step for 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param name
     * @param content
     * @param language
     * @param documentIRI
     * @param out
     */
    private void processMetaElement(
            String name,
            String content,
            String language,
            IRI documentIRI,
            ExtractionResult out) {
        Literal subject;
        if (language == null) {
            // ok, we don't know the language
            subject = RDFUtils.literal(content);
        } else {
            subject = RDFUtils.literal(content, language);
        }
        out.writeTriple(
                documentIRI,
                RDFUtils.iri(XHTML.NS + name.toLowerCase().trim()),
                subject
        );
    }

    /**
     * Implements sub step for 5.2.4 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in
     * @param documentIRI
     * @param out
     */
    private void processCiteElements(Document in, IRI documentIRI, ExtractionResult out) {
        NodeList blockQuotes = in.getElementsByTagName("blockquote");
        for (int i = 0; i < blockQuotes.getLength(); i++) {
            processCiteElement(blockQuotes.item(i), documentIRI, out);
        }
        NodeList quotes = in.getElementsByTagName("q");
        for (int i = 0; i < quotes.getLength(); i++) {
            processCiteElement(quotes.item(i), documentIRI, out);
        }
    }

    private void processCiteElement(Node item, IRI documentIRI, ExtractionResult out) {
        if (item.getAttributes().getNamedItem("cite") != null) {
            out.writeTriple(
                    documentIRI,
                    DCTerms.getInstance().source,
                    RDFUtils.iri(item.getAttributes().getNamedItem("cite").getTextContent())
            );
        }
    }

    /**
     * Recursive method implementing 5.2.6.1 "generate the triple for the item" of
     * <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param itemScope
     * @param documentIRI
     * @param out
     * @param mappings
     * @return
     * @throws ExtractionException
     */
    private Resource processType(
            ItemScope itemScope,
            IRI documentIRI, ExtractionResult out,
            Map<ItemScope, Resource> mappings, IRI defaultNamespace
    ) throws ExtractionException {
        Resource subject = mappings.computeIfAbsent(itemScope, scope ->
                createSubjectForItemId(documentIRI, scope.getItemId()));

        List<IRI> itemScopeTypes = itemScope.getTypes();
        if (!itemScopeTypes.isEmpty()) {
            defaultNamespace = getNamespaceIRI(itemScopeTypes.get(0));
            for (IRI type : itemScopeTypes) {
                out.writeTriple(subject, RDF.TYPE, type);
            }
        }
        for (Map.Entry<String, List<ItemProp>> itemProps : itemScope.getProperties().entrySet()) {
            String propName = itemProps.getKey();
            IRI predicate = getPredicate(defaultNamespace, propName);
            if (predicate == null) {
                continue;
            }
            for (ItemProp itemProp : itemProps.getValue()) {
                try {
                    processProperty(
                            subject,
                            predicate,
                            itemProp,
                            documentIRI,
                            mappings,
                            out,
                            defaultNamespace
                    );
                } catch (URISyntaxException e) {
                    throw new ExtractionException(
                            "Error while processing on subject '" + subject +
                                    "' the itemProp: '" + itemProp + "' "
                    );
                }
            }
        }
        return subject;
    }

    private static Resource createSubjectForItemId(IRI documentIRI, String itemId) {
        if (itemId == null) {
            return RDFUtils.bnode();
        }
        try {
            return toAbsoluteIRI(documentIRI, itemId);
        } catch (URISyntaxException e) {
            return RDFUtils.bnode();
        }
    }

    private void processProperty(
            Resource subject,
            IRI predicate,
            ItemProp itemProp,
            IRI documentIRI,
            Map<ItemScope, Resource> mappings,
            ExtractionResult out,
            IRI defaultNamespace
    ) throws URISyntaxException, ExtractionException {

        Value value;
        Object propValue = itemProp.getValue().getContent();
        ItemPropValue.Type propType = itemProp.getValue().getType();
        if (itemProp.getValue().literal != null) {
            value = itemProp.getValue().literal;
        } else if (propType.equals(ItemPropValue.Type.Nested)) {
            value = processType((ItemScope) propValue, documentIRI, out, mappings, defaultNamespace);
        } else if (propType.equals(ItemPropValue.Type.Plain)) {
            value = RDFUtils.literal((String) propValue, documentLanguage);
        } else if (propType.equals(ItemPropValue.Type.Link)) {
            value = toAbsoluteIRI(documentIRI, (String)propValue);
            //TODO: support registries so hardcoding not needed
            if (predicate.stringValue().equals("http://schema.org/additionalType")) {
                out.writeTriple(subject, RDF.TYPE, value);
            }
        } else if (propType.equals(ItemPropValue.Type.Date)) {
            value = RDFUtils.literal(ItemPropValue.formatDateTime((Date) propValue), XMLSchema.DATE);
        } else {
            throw new RuntimeException("Invalid Type '" +
                    propType + "' for ItemPropValue with name: '" + predicate + "'");
        }
        out.writeTriple(subject, predicate, value);
    }

    private static final String hcardPrefix    = "http://microformats.org/profile/hcard";
    private static final IRI hcardNamespaceIRI = RDFUtils.iri("http://microformats.org/profile/hcard#");

    static {
        assert createNamespaceFromPrefix(RDFUtils.iri(hcardPrefix)).equals(hcardNamespaceIRI);
    }

    private static IRI createNamespaceFromPrefix(IRI prefix) {
        if (prefix.getLocalName().isEmpty()) {
            return prefix;
        }
        String ns = prefix.getNamespace();
        IRI ret = RDFUtils.iri(ns.endsWith("#") ? ns : (prefix.stringValue() + "#"));
        assert ret.getLocalName().isEmpty() && ret.getNamespace().endsWith("#");
        return ret;
    }

    private static IRI getNamespaceIRI(IRI itemType) {
        //TODO: support registries so hardcoding not needed
        return itemType.stringValue().startsWith(hcardPrefix) ? hcardNamespaceIRI : itemType;
    }

    private static IRI getPredicate(IRI namespaceIRI, String localName) {
        return toAbsoluteIRI(localName).orElseGet(() -> namespaceIRI == null ? null :
                RDFUtils.iri(namespaceIRI.getNamespace(), localName.trim()));
    }

    private static Optional<IRI> toAbsoluteIRI(String urlString) {
        if (urlString != null) {
            try {
                ParsedIRI iri = ParsedIRI.create(urlString.trim());
                if (iri.isAbsolute()) {
                    return Optional.of(RDFUtils.iri(iri.toString()));
                }
            } catch (RuntimeException e) {
                //not an absolute iri
            }
        }
        return Optional.empty();
    }

    private static IRI toAbsoluteIRI(IRI documentIRI, String part) throws URISyntaxException {
        ParsedIRI iri;
        try {
            iri = ParsedIRI.create(part.trim());
        } catch (RuntimeException e) {
            throw new URISyntaxException(String.valueOf(part), e.getClass().getName() + ": " + e.getMessage());
        }

        if (iri.isAbsolute()) {
            return RDFUtils.iri(iri.toString());
        }

        return RDFUtils.iri(new ParsedIRI(documentIRI.toString()).resolve(iri).toString());
    }

    private void notifyError(MicrodataParserException[] errors, ExtractionResult out) {
        for(MicrodataParserException mpe : errors) {
            out.notifyIssue(
                    IssueReport.IssueLevel.ERROR,
                    mpe.toJSON(),
                    mpe.getErrorLocationBeginRow(),
                    mpe.getErrorLocationBeginCol()
            );
        }
    }

}