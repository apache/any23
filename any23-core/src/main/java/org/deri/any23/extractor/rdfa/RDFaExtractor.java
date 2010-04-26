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

package org.deri.any23.extractor.rdfa;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.rdf.RDFParserFactory;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Extractor for RDFa in HTML, based on Fabien Gadon's XSLT transform, found
 * <a href="http://ns.inria.fr/grddl/rdfa/">here</a>. It works by first
 * parsing the HTML using a tagsoup parser, then applies the XSLT to the
 * DOM tree, then parses the resulting RDF/XML.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class RDFaExtractor implements TagSoupDOMExtractor {

    public final static String NAME = "html-rdfa";

    public final static String xsltFilename = "rdfa.xslt";

    public final static ExtractorFactory<RDFaExtractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    null,
                    Arrays.asList("text/html;q=0.3", "application/xhtml+xml;q=0.3"),
                    null,
                    RDFaExtractor.class
            );

    private static XSLTStylesheet xslt = null;

    private boolean verifyDataType;

    private boolean stopAtFirstError;

    /**
     * Constructor, allows to specify the validation and error handling policies.
     *
     * @param verifyDataType if <code>true</code> the data types will be verified,
     *         if <code>false</code> will be ignored.
     * @param stopAtFirstError if <code>true</code> the parser will stop at first parsing error,
     *        if <code>false</code> will ignore non blocking errors.
     */
    public RDFaExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        this.verifyDataType   = verifyDataType;
        this.stopAtFirstError = stopAtFirstError;
    }

    /**
     * Default constructor, with no verification of data types and not stop at first error.
     */    
    public RDFaExtractor() {
        this(false, false);
    }

    public boolean isVerifyDataType() {
        return verifyDataType;
    }

    public void setVerifyDataType(boolean verifyDataType) {
        this.verifyDataType = verifyDataType;
    }

    public boolean isStopAtFirstError() {
        return stopAtFirstError;
    }

    public void setStopAtFirstError(boolean stopAtFirstError) {
        this.stopAtFirstError = stopAtFirstError;
    }

    /**
     * Triggers the execution of this extractor.
     *
     * @param in          The extractor's input
     * @param documentURI The document's URI
     * @param out         Sink for extracted data
     * @throws IOException
     * @throws ExtractionException
     */
    public void run(Document in, URI documentURI, ExtractionResult out)
            throws IOException, ExtractionException {

        StringWriter buffer = new StringWriter();
        try {
            getXSLT().applyTo(in, buffer);
        } catch (XSLTStylesheetException xslte) {
            throw new ExtractionException("An error occurred during the XSLT application.", xslte);
        }

        try {
            RDFParser parser
                    = RDFParserFactory.getInstance().getRDFXMLParser(verifyDataType, stopAtFirstError, out);
            parser.parse(
                    new StringReader(buffer.getBuffer().toString()),
                    documentURI.stringValue()
            );
        } catch (RDFHandlerException ex) {
            throw new RuntimeException("Should not happen, RDFHandlerAdapter does not throw RDFHandlerException", ex);
        } catch (RDFParseException ex) {
            throw new ExtractionException(
                    "Invalid RDF/XML produced by RDFa transform.", ex, out.getExtractionContext()
            );
        }
    }

    /**
     * @return the {@link org.deri.any23.extractor.ExtractorDescription} of this extractor
     */
    public ExtractorDescription getDescription() {
        return factory;
    }

    /**
     * Returns a {@link org.deri.any23.extractor.rdfa.XSLTStylesheet} able to distill RDFa from
     * HTML pages.
     */
    private synchronized XSLTStylesheet getXSLT() {
        // Lazily initialized static instance, so we don't parse
        // the XSLT unless really necessary, and only once
        if (xslt == null) {
            InputStream in = RDFaExtractor.class.getResourceAsStream(xsltFilename);
            if (in == null) {
                throw new RuntimeException("Couldn't load '" + xsltFilename +
                        "', maybe the file is not bundled in the jar?");
            }
            xslt = new XSLTStylesheet(in);
        }
        return xslt;
    }

}