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

package org.apache.any23.extractor.rdf;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.html.JsoupUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RioSetting;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeFilter;
import org.jsoup.select.NodeTraversor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

/**
 * Base class for a generic <i>RDF</i>
 * {@link org.apache.any23.extractor.Extractor.ContentExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class BaseRDFExtractor implements Extractor.ContentExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(BaseRDFExtractor.class);
    private boolean verifyDataType;
    private boolean stopAtFirstError;

    public BaseRDFExtractor() {
        this(false, false);
    }

    /**
     * Constructor, allows to specify the validation and error handling policies.
     *
     * @param verifyDataType if <code>true</code> the data types will be verified,
     *         if <code>false</code> will be ignored.
     * @param stopAtFirstError if <code>true</code> the parser will stop at first parsing error,
     *        if <code>false</code> will ignore non blocking errors.
     */
    public BaseRDFExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        this.verifyDataType = verifyDataType;
        this.stopAtFirstError = stopAtFirstError;
    }

    protected abstract RDFParser getParser(
            ExtractionContext extractionContext,
            ExtractionResult extractionResult
    );

    public boolean isVerifyDataType() {
        return verifyDataType;
    }

    public void setVerifyDataType(boolean verifyDataType) {
        this.verifyDataType = verifyDataType;
    }

    public boolean isStopAtFirstError() {
        return stopAtFirstError;
    }

    @Override
    public void setStopAtFirstError(boolean b) {
        stopAtFirstError = b;
    }

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            InputStream in,
            ExtractionResult extractionResult
    ) throws IOException, ExtractionException {
        try {
            final RDFParser parser = getParser(extractionContext, extractionResult);
            parser.getParserConfig().setNonFatalErrors(new HashSet<RioSetting<?>>());

            // Disable verification to ensure that DBPedia is accessible, given it uses so many custom datatypes
            parser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
            parser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
            parser.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);
            parser.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS, true);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_RELATIVE_URIS);


            RDFFormat format = parser.getRDFFormat();
            String iri = extractionContext.getDocumentIRI().stringValue();

            if (format.hasFileExtension("xhtml") || format.hasMIMEType("application/xhtml+xml")) {
                Charset charset = format.getCharset();
                if (charset == null) {
                    charset = StandardCharsets.UTF_8;
                }
                Document doc = JsoupUtils.parse(in, iri, null);
                doc.outputSettings()
                        .prettyPrint(false)
                        .syntax(Document.OutputSettings.Syntax.xml)
                        .escapeMode(Entities.EscapeMode.xhtml)
                        .charset(charset);
                // Delete scripts, comments, and doctypes
                // See https://issues.apache.org/jira/browse/ANY23-317
                // and https://issues.apache.org/jira/browse/ANY23-340
                NodeTraversor.filter(new NodeFilter() {
                    @Override
                    public FilterResult head(Node node, int depth) {
                        return node instanceof DataNode || node instanceof Comment || node instanceof DocumentType
                                ? FilterResult.REMOVE : FilterResult.CONTINUE;
                    }
                    @Override
                    public FilterResult tail(Node node, int depth) {
                        return FilterResult.CONTINUE;
                    }
                }, doc);

                in = new ByteArrayInputStream(doc.toString().getBytes(charset));
            } else if (format.hasFileExtension("jsonld") || format.hasMIMEType("application/ld+json")) {
                in = new JsonCommentStripperInputStream(in);
            }

            parser.parse(in, iri);
        } catch (RDFHandlerException ex) {
            throw new IllegalStateException("Unexpected exception.", ex);
        } catch (RDFParseException ex) {
            LOG.error("Error while parsing RDF document.", ex);
        }
    }


    private static class JsonCommentStripperInputStream extends InputStream {

        private boolean inEscape;
        private boolean inQuote;
        private boolean inCDATA;

        private final PushbackInputStream wrapped;

        JsonCommentStripperInputStream(InputStream in) {
            wrapped = new PushbackInputStream(in, 16);
        }

        private boolean isNextOrUnread(int... next) throws IOException {
            int i = -1;
            for (int test : next) {
                int c = wrapped.read();
                if (c != test) {
                    if (c != -1) {
                        wrapped.unread(c);
                    }
                    while (i >= 0) {
                        wrapped.unread(next[i--]);
                    }
                    return false;
                }
                i++;
            }
            return true;
        }

        @Override
        public int read() throws IOException {
            PushbackInputStream stream = wrapped;
            int c = stream.read();

            if (inQuote) {
                if (inEscape) {
                    inEscape = false;
                } else if (c == '"') {
                    inQuote = false;
                } else if (c == '\\') {
                    inEscape = true;
                }
                return c;
            }

            //we're not in a quote
            switch (c) {
                case '/':
                    if (isNextOrUnread('/')) {
                        //single line comment: read to end of line
                        for (;;) {
                            c = stream.read();
                            if (c == -1 || c == '\r' || c == '\n') {
                                return c;
                            }
                        }
                    } else if (isNextOrUnread('*')) {
                        //multiline comment: read till next "*/"
                        for (;;) {
                            c = stream.read();
                            if (c == -1) {
                                return c;
                            } else if (c == '*') {
                                c = stream.read();
                                if (c == -1) {
                                    return c;
                                } else if (c == '/') {
                                    //replace entire comment with single space
                                    return ' ';
                                }
                            }
                        }
                    } else {
                        return c;
                    }
                case '<':
                    if (isNextOrUnread('!','[','C','D','A','T','A','[')) {
                        inCDATA = true;
                        return ' ';
                    } else {
                        return c;
                    }
                case '#':
                    for (;;) {
                        c = stream.read();
                        if (c == -1 || c == '\r' || c == '\n') {
                            return c;
                        }
                    }
                case ']':
                    if (inCDATA) {
                        if (isNextOrUnread(']', '>')) {
                            inCDATA = false;
                            return ' ';
                        } else {
                            return c;
                        }
                    } else {
                        return c;
                    }
                case '"':
                    inQuote = true;
                    return c;
                default:
                    return c;
            }

        }

    }

}
