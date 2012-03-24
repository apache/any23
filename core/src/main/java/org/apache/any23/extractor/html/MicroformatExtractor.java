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
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.TagSoupExtractionResult;
import org.apache.any23.extractor.html.annotations.Includes;
import org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;

/**
 * The abstract base class for any
 * <a href="microformats.org/">Microformat specification</a> extractor.
 */
public abstract class MicroformatExtractor implements TagSoupDOMExtractor {

    public static final String BEGIN_SCRIPT = "<script>";
    public static final String END_SCRIPT   = "</script>";

    private HTMLDocument htmlDocument;

    private ExtractionContext context;

    private URI documentURI;

    private ExtractionResult out;

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

    public HTMLDocument getHTMLDocument() {
        return htmlDocument;
    }

    public ExtractionContext getExtractionContext() {
        return context;
    }

    public URI getDocumentURI() {
        return documentURI;
    }

    public final void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        this.htmlDocument = new HTMLDocument(in);
        this.context      = extractionContext;
        this.documentURI  = extractionContext.getDocumentURI();
        this.out          = out;
        valueFactory.setIssueReport(out);
        try {
            extract();
        } finally {
            valueFactory.setIssueReport(null);
        }
    }

    /**
     * Returns the {@link org.apache.any23.extractor.ExtractionResult} associated
     * to the extraction session.
     *
     * @return a valid extraction result.
     */
    protected ExtractionResult getCurrentExtractionResult() {
        return out;
    }

    protected ExtractionResult openSubResult(ExtractionContext context) {
        return out.openSubResult(context);
    }

    /**
     * Helper method that adds a literal property to a subject only if the value of the property
     * is a valid string.
     *
     * @param n the <i>HTML</i> node from which the property value has been extracted.
     * @param subject the property subject.
     * @param p the property URI.
     * @param value the property value.
     * @return returns <code>true</code> if the value has been accepted and added, <code>false</code> otherwise.
     */
    protected boolean conditionallyAddStringProperty(
            Node n,
            Resource subject, URI p, String value
    ) {
        if (value == null) return false;
        value = value.trim();
        return
                value.length() > 0 
                        &&
                conditionallyAddLiteralProperty(
                        n,
                        subject, p, valueFactory.createLiteral(value)
                );
    }

    /**
     * Helper method that adds a literal property to a node.
     *
     * @param n the <i>HTML</i> node from which the property value has been extracted.
     * @param subject subject the property subject.
     * @param property the property URI.
     * @param literal value the property value.
     * @return returns <code>true</code> if the literal has been accepted and added, <code>false</code> otherwise.
     */
    protected boolean conditionallyAddLiteralProperty(
            Node n,
            Resource subject,
            URI property,
            Literal literal
    ) {
        final String literalStr = literal.stringValue();
        if( containsScriptBlock(literalStr) ) {
            out.notifyIssue(
                    IssueReport.IssueLevel.Warning,
                    String.format("Detected script in literal: [%s]", literalStr)
                    , -1
                    , -1
            );
            return false;
        }
        out.writeTriple(subject, property, literal);
        TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addPropertyPath(this.getClass(), subject, property, null, DomUtils.getXPathListForNode(n) );
        return true;
    }

    /**
     * Helper method that adds a URI property to a node.
     * @param subject the property subject.
     * @param property the property URI.
     * @param uri the property object.
     * @return <code>true</code> if the the resource has been added, <code>false</code> otherwise. 
     */
    protected boolean conditionallyAddResourceProperty(Resource subject, URI property, URI uri) {
        if (uri == null) return false;
        out.writeTriple(subject, property, uri);
        return true;
    }

    /**
     * Helper method that adds a BNode property to a node.
     *
     * @param n the <i>HTML</i> node used for extracting such property.
     * @param subject the property subject.
     * @param property the property URI.
     * @param bnode the property value.
     */
    protected void addBNodeProperty(Node n, Resource subject, URI property, BNode bnode) {
        out.writeTriple(subject, property, bnode);
        TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addPropertyPath(this.getClass(), subject, property, bnode, DomUtils.getXPathListForNode(n) );
    }

    /**
     * Helper method that adds a BNode property to a node.
     *
     * @param subject the property subject.
     * @param property the property URI.
     * @param bnode the property value.
     */
    protected void addBNodeProperty( Resource subject, URI property, BNode bnode) {
        out.writeTriple(subject, property, bnode);
    }

    /**
     * Helper method that adds a URI property to a node.
     *
     * @param subject
     * @param property
     * @param object
     */
    protected void addURIProperty(Resource subject, URI property, URI object) {
        out.writeTriple(subject, property, object);    
    }

    protected URI fixLink(String link) {
        return valueFactory.fixLink(link, null);
    }

    protected URI fixLink(String link, String defaultSchema) {
        return valueFactory.fixLink(link, defaultSchema);
    }

    private boolean containsScriptBlock(String in) {
        final String inLowerCase = in.toLowerCase();
        final int beginBlock = inLowerCase.indexOf(BEGIN_SCRIPT);
        if(beginBlock == -1) {
            return false;
        }
        return inLowerCase.indexOf(END_SCRIPT, beginBlock + BEGIN_SCRIPT.length()) != -1;
    }

        /**
     * This method checks if there is a native nesting relationship between two
     * {@link MicroformatExtractor}.
     *
     * @see org.apache.any23.extractor.html.annotations.Includes
     * @param including the including {@link MicroformatExtractor}
     * @param included the included {@link MicroformatExtractor}
     * @return <code>true</code> if there is a declared nesting relationship
     */
    public static boolean includes(
            Class<? extends MicroformatExtractor>including,
            Class<? extends MicroformatExtractor> included) {
        Includes includes = including.getAnnotation(Includes.class);
        if (includes != null) {
            Class<? extends MicroformatExtractor>[] extractors = includes.extractors();
            if (extractors != null && extractors.length > 0) {
                for (Class<? extends MicroformatExtractor> extractor : extractors) {
                    if (extractor.equals(included)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}