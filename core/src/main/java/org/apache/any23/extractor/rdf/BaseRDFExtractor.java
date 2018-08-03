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

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.html.JsoupUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
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
import java.util.Iterator;
import java.util.regex.Pattern;

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

    private static final Pattern invalidXMLCharacters = Pattern.compile(
            "[^\u0009\r\n\u0020-\uD7FF\uE000-\uFFFD\ud800\udc00-\udbff\udfff]");

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            InputStream in,
            ExtractionResult extractionResult
    ) throws IOException, ExtractionException {
        try {
            final RDFParser parser = getParser(extractionContext, extractionResult);

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
                    final HashSet<String> tmpAttributeKeys = new HashSet<>();

                    @Override
                    public FilterResult head(Node node, int depth) {
                        if (node instanceof Element) {
                            HashSet<String> attributeKeys = tmpAttributeKeys;
                            for (Iterator<Attribute> it = node.attributes().iterator(); it.hasNext(); ) {
                                // fix for ANY23-350: valid xml attribute names are ^[a-zA-Z_:][-a-zA-Z0-9_:.]
                                Attribute attr = it.next();
                                String oldKey = attr.getKey();
                                String newKey = oldKey.replaceAll("[^-a-zA-Z0-9_:.]", "");

                                // fix for ANY23-347: strip non-reserved xml namespaces
                                // See https://www.w3.org/TR/xml-names/#sec-namespaces
                                // "All other prefixes beginning with the three-letter sequence x, m, l,
                                // in any case combination, are reserved. This means that:
                                //   * users SHOULD NOT use them except as defined by later specifications
                                //   * processors MUST NOT treat them as fatal errors."
                                int prefixlen = oldKey.lastIndexOf(':') + 1;
                                String prefix = newKey.substring(0, prefixlen).toLowerCase();
                                newKey = (prefix.startsWith("xml") ? prefix : "") + newKey.substring(prefixlen);

                                if (newKey.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*")
                                        //the namespace name for "xmlns" MUST NOT be declared
                                        //the namespace name for "xml" need not be declared
                                        && !newKey.startsWith("xmlns:xml")
                                        // fix for ANY23-380: disallow duplicate attribute keys
                                        && attributeKeys.add(newKey)) {
                                    //avoid indexOf() operation if possible
                                    if (!newKey.equals(oldKey)) {
                                        attr.setKey(newKey);
                                    }
                                } else {
                                    it.remove();
                                }
                            }
                            attributeKeys.clear();

                            String tagName = ((Element)node).tagName().replaceAll("[^-a-zA-Z0-9_:.]", "");
                            tagName = tagName.substring(tagName.lastIndexOf(':') + 1);
                            ((Element)node).tagName(tagName.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*") ? tagName : "div");

                            return FilterResult.CONTINUE;
                        }
                        return node instanceof DataNode || node instanceof Comment || node instanceof DocumentType
                                ? FilterResult.REMOVE : FilterResult.CONTINUE;
                    }
                    @Override
                    public FilterResult tail(Node node, int depth) {
                        return FilterResult.CONTINUE;
                    }
                }, doc);

                // fix for ANY23-379: remove invalid xml characters from document
                String finalOutput = invalidXMLCharacters.matcher(doc.toString()).replaceAll("");

                in = new ByteArrayInputStream(finalOutput.getBytes(charset));
            } else if (format.hasFileExtension("jsonld") || format.hasMIMEType("application/ld+json")) {
                in = new JsonCleaningInputStream(in);
            }

            parser.parse(in, iri);
        } catch (RDFHandlerException ex) {
            throw new IllegalStateException("Unexpected exception.", ex);
        } catch (RDFParseException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof JsonParseException) {
                JsonParseException err = (JsonParseException)cause;
                JsonLocation loc = err.getLocation();
                if (loc == null) {
                    extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, err.getOriginalMessage(), -1L, -1L);
                } else {
                    extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, err.getOriginalMessage(), loc.getLineNr(), loc.getColumnNr());
                }
            } else {
                throw new ExtractionException("Error while parsing RDF document.", ex, extractionResult);
            }
        }
    }


    private static class JsonCleaningInputStream extends InputStream {

        private boolean inEscape;
        private int quoteChar;
        private boolean inCDATA;
        private boolean needsComma;

        private final PushbackInputStream wrapped;

        JsonCleaningInputStream(InputStream in) {
            wrapped = new PushbackInputStream(in, 16);
        }

        private static boolean isNextOrUnread(PushbackInputStream stream, int... next) throws IOException {
            int i = -1;
            for (int test : next) {
                int c = stream.read();
                if (c != test) {
                    if (c != -1) {
                        stream.unread(c);
                    }
                    while (i >= 0) {
                        stream.unread(next[i--]);
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

            for (;;) {
                int c = stream.read();

                //other types of comments are handled by enabling fasterxml's
                //ALLOW_COMMENTS and ALLOW_YAML_COMMENTS features
                if (inCDATA) {
                    if (c == ']' && isNextOrUnread(stream, ']', '>')) {
                        inCDATA = false;
                        continue;
                    }
                } else {
                    if (c == '<' && isNextOrUnread(stream, '!', '[', 'C', 'D', 'A', 'T', 'A', '[')) {
                        inCDATA = true;
                        continue;
                    }
                }

                int q = quoteChar;
                if (q != 0) {
                    //we're in a quote
                    if (inEscape) {
                        //end escape
                        inEscape = false;
                    } else if (c == '\\') {
                        //begin escape
                        inEscape = true;
                    } else if (c == q) {
                        //end quote
                        quoteChar = 0;
                    }
                    return c;
                }

                //we're not in a quote
                switch (c) {
                    case ',':
                    case ';':
                        //don't write out comma yet!
                        needsComma = true;
                        break;
                    case '}':
                    case ']':
                        //discard comma at end of object or array
                        needsComma = false;
                        return c;
                    default:
                        if (c != -1 && !Character.isWhitespace(c)) {
                            if (needsComma) {
                                stream.unread(c);
                                stream.unread(' ');
                                needsComma = false;
                                return ',';
                            } else if (c == '"' || c == '\'') {
                                quoteChar = c;
                            }
                        }
                        return c;
                }
            }
        }
    }

}
