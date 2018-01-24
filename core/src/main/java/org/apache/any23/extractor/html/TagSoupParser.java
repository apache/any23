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

import org.apache.any23.validator.DefaultValidator;
import org.apache.any23.validator.Validator;
import org.apache.any23.validator.ValidatorException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * <p>Parses an {@link java.io.InputStream}
 * into an <i>HTML DOM</i> tree using a <i>TagSoup</i> parser.
 * </p>
 * <p><strong>Note:</strong> The resulting <i>DOM</i> tree will not be namespace
 * aware, and all element names will be upper case, while attributes
 * will be lower case. This is because the
 * <a href="http://nekohtml.sourceforge.net/">NekoHTML</a> based <i>TagSoup</i> parser
 * by default uses the <a href="http://xerces.apache.org/xerces2-j/dom.html">Xerces HTML DOM</a>
 * implementation, which doesn't support namespaces and forces uppercase element names. This works
 * with the <i>RDFa XSLT Converter</i> and with <i>XPath</i>, so we left it this way.</p>
 *
 * @author Richard Cyganiak (richard at cyganiak dot de)
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */

public class TagSoupParser {

    public static final String ELEMENT_LOCATION = "Element-Location";

    private static final String AUGMENTATIONS_FEATURE = "http://cyberneko.org/html/features/augmentations";

    private final static Logger logger = LoggerFactory.getLogger(TagSoupParser.class);

    private final InputStream input;

    private final String documentIRI;

    private final String encoding;

    private final TagSoupParsingConfiguration config;

    private Document result = null;


    public TagSoupParser(InputStream input, String documentIRI) {
        this.input = input;
        this.documentIRI = documentIRI;
        this.encoding = null;

        config = TagSoupParsingConfiguration.getDefault();
    }

    public TagSoupParser(InputStream input, String documentIRI, String encoding) {
        if (encoding != null && !Charset.isSupported(encoding))
            throw new UnsupportedCharsetException(String.format("Charset %s is not supported", encoding));

        this.input = input;
        this.documentIRI = documentIRI;
        this.encoding = encoding;

        config = TagSoupParsingConfiguration.getDefault();
    }


    /**
     * Returns the DOM of the given document IRI. 
     *
     * @return the <i>HTML</i> DOM.
     * @throws IOException if there is an error whilst accessing the DOM
     */
    public Document getDOM() throws IOException {
        if (result == null) {
            long startTime = System.currentTimeMillis();
            try {
                result = config.parse(input, documentIRI, encoding);
            } finally {
                long elapsed = System.currentTimeMillis() - startTime;
                logger.debug("Parsed " + documentIRI + " with " + config.name() + ", " + elapsed + "ms");
            }
        }
        result.setDocumentURI(documentIRI);
        return result;
    }

    /**
     * Returns the validated DOM and applies fixes on it if <i>applyFix</i>
     * is set to <code>true</code>.
     *
     * @param applyFix whether to apply fixes to the DOM
     * @return a report containing the <i>HTML</i> DOM that has been validated and fixed if <i>applyFix</i>
     *         if <code>true</code>. The reports contains also information about the activated rules and the
     *         the detected issues.
     * @throws IOException if there is an error accessing the DOM
     * @throws org.apache.any23.validator.ValidatorException if there is an error validating the DOM
     */
    public DocumentReport getValidatedDOM(boolean applyFix) throws IOException, ValidatorException {
        final URI dIRI;
        try {
            dIRI = new URI(documentIRI);
        } catch (IllegalArgumentException | URISyntaxException urise) {
            throw new ValidatorException("Error while performing validation, invalid document IRI.", urise);
        }
        Validator validator = new DefaultValidator();
        Document document = getDOM();
        return new DocumentReport( validator.validate(dIRI, document, applyFix), document );
    }


    static TagSoupParsingConfiguration legacyConfig() {
        return NekoHTML.instance;
    }

    private static class NekoHTML extends TagSoupParsingConfiguration {

        private static final NekoHTML instance = new NekoHTML();

        @Override
        Document parse(InputStream input, String documentIRI, String encoding) throws IOException {
            try {
                return parse(input, encoding);
            } catch (SAXException ex) {
                // should not happen, it's a tag soup parser
                throw new RuntimeException("Should not happen, it's a tag soup parser", ex);
            } catch (TransformerException ex) {
                // should not happen, it's a tag soup parser
                throw new RuntimeException("Should not happen, it's a tag soup parser", ex);
            } catch (NullPointerException ex) {
                if (ex.getStackTrace()[0].getClassName().equals("java.io.Reader")) {
                    throw new RuntimeException("Bug in NekoHTML, try upgrading to newer release!", ex);
                } else {
                    throw ex;
                }
            }
        }

        private Document parse(InputStream input, String encoding) throws IOException, SAXException, TransformerException {
            final DOMParser parser = new DOMParser() {

                private QName currentQName;
                private Augmentations currentAugmentations;

                @Override
                protected Element createElementNode(QName qName) {
                    final Element created = super.createElementNode(qName);
                    if (qName.equals(currentQName) && currentAugmentations != null) {
                        final ElementLocation elementLocation = createElementLocation(
                                currentAugmentations.getItem(AUGMENTATIONS_FEATURE)
                        );
                        created.setUserData(ELEMENT_LOCATION, elementLocation, null);
                    }
                    return created;
                }

                @Override
                public void startElement(QName qName, XMLAttributes xmlAttributes, Augmentations augmentations)
                        throws XNIException {
                    super.startElement(qName, xmlAttributes, augmentations);
                    currentQName = qName;
                    currentAugmentations = augmentations;
                }

                private ElementLocation createElementLocation(Object obj) {
                    if(obj == null) return null;
                    String pattern = null;
                    try {
                        pattern = obj.toString();
                        if( "synthesized".equals(pattern) ) return null;
                        final String[] parts = pattern.split(":");
                        return new ElementLocation(
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]),
                                Integer.parseInt(parts[3]),
                                Integer.parseInt(parts[4])

                        );
                    } catch (Exception e) {
                        logger.warn(
                                String.format("Unexpected string format for given augmentation: [%s]", pattern),
                                e
                        );
                        return null;
                    }
                }
            };
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-cdata-delims", true);
            parser.setFeature(AUGMENTATIONS_FEATURE, true);
            if (encoding != null)
                parser.setProperty("http://cyberneko.org/html/properties/default-encoding", encoding);

            /*
             * NOTE: the SpanCloserInputStream has been added to wrap the stream passed to the CyberNeko
             *       parser. This will ensure the correct handling of inline HTML SPAN tags.
             *       This fix is documented at issue #78.
             */
            parser.parse(new InputSource( new SpanCloserInputStream(input)));
            return parser.getDocument();
        }


    }



    /**
     * Describes a <i>DOM Element</i> location.
     */
    public static class ElementLocation {

        private int beginLineNumber;
        private int beginColumnNumber;
        private int endLineNumber;
        private int endColumnNumber;

        private ElementLocation(
                int beginLineNumber, int beginColumnNumber, int endLineNumber, int endColumnNumber
        ) {
            this.beginLineNumber = beginLineNumber;
            this.beginColumnNumber = beginColumnNumber;
            this.endLineNumber = endLineNumber;
            this.endColumnNumber = endColumnNumber;
        }

        public int getBeginLineNumber() {
            return beginLineNumber;
        }

        public int getBeginColumnNumber() {
            return beginColumnNumber;
        }

        public int getEndLineNumber() {
            return endLineNumber;
        }

        public int getEndColumnNumber() {
            return endColumnNumber;
        }
    }
    
}