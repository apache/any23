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

package org.apache.any23.extractor.rdfa;

import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.rdf.RDFParserFactory;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

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

    public final static String xsltFilename =
            DefaultConfiguration.singleton().getPropertyOrFail("any23.rdfa.extractor.xslt");

    private static XSLTStylesheet xslt = null;

    /**
     * Returns a {@link XSLTStylesheet} able to distill RDFa from
     * HTML pages.
     *
     * @return returns a not <code>null</code> XSLT instance.
     */
    public static synchronized XSLTStylesheet getXSLT() {
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

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {

        StringWriter buffer = new StringWriter();
        try {
            getXSLT().applyTo(in, buffer);
        } catch (XSLTStylesheetException xslte) {
            throw new ExtractionException("An error occurred during the XSLT application.", xslte);
        }

        try {
            RDFParser parser
                    = RDFParserFactory.getInstance().getRDFXMLParser(
                        verifyDataType, stopAtFirstError, extractionContext, out
                    );
            parser.parse(
                    new StringReader(buffer.getBuffer().toString()),
                    extractionContext.getDocumentURI().stringValue()
            );
        } catch (RDFHandlerException ex) {
            throw new IllegalStateException(
                    "Should not happen, RDFHandlerAdapter does not throw RDFHandlerException", ex
            );
        } catch (RDFParseException ex) {
            throw new ExtractionException(
                    "Invalid RDF/XML produced by RDFa transform.", ex, out
            );
        }
    }

    private String getDocType(Document in) {
        return in.getDoctype().getPublicId();
    }

    /**
     * @return the {@link org.apache.any23.extractor.ExtractorDescription} of this extractor
     */
    @Override
    public ExtractorDescription getDescription() {
        return RDFaExtractorFactory.getDescriptionInstance();
    }

}