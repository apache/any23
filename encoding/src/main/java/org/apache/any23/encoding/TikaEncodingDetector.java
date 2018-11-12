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

package org.apache.any23.encoding;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.apache.tika.utils.CharsetUtils;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.PseudoTextElement;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Parser;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * An implementation of {@link EncodingDetector} based on
 * <a href="http://tika.apache.org/">Apache Tika</a>.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Hans Brende (hansbrende@apache.org)
 * @version $Id$
 */
public class TikaEncodingDetector implements EncodingDetector {

    @Override
    public String guessEncoding(InputStream input) throws IOException {
        return guessEncoding(input, (String)null);
    }

    private static final String TAG_CHARS = "< />";
    private static final byte[] TAG_BYTES = TAG_CHARS.getBytes(UTF_8);
    private static final Node[] EMPTY_NODES = new Node[0];

    private static Charset guessEncoding(InputStream is, Charset declared) throws IOException {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        is.mark(Integer.MAX_VALUE);
        Boolean utf8;
        try {
            utf8 = EncodingUtils.isUTF8(is);
            if (utf8 != null && utf8) {
                // > 92% of the web is UTF-8. Do not risk false positives from obscure charsets.
                // See https://issues.apache.org/jira/browse/TIKA-2771
                // and https://issues.apache.org/jira/browse/TIKA-539
                return UTF_8;
            }
        } finally {
            is.reset();
        }

        if (declared != null) {
            return declared;
        }

        boolean filterInput;
        byte[] text;
        {
            // ISO-8859-1 is Java's only "standard charset" which maps 1-to-1 onto the first 256 unicode characters;
            // use ISO-8859-1 for round-tripping of bytes after stripping html/xml tags from input
            String iso_8859_1;
            is.mark(Integer.MAX_VALUE);
            try {
                iso_8859_1 = EncodingUtils.iso_8859_1(is);
            } finally {
                is.reset();
            }

            Charset xmlCharset = EncodingUtils.xmlCharset(iso_8859_1);
            if (xmlCharset != null) {
                return xmlCharset;
            }

            ParseErrorList htmlErrors = ParseErrorList.tracking(Integer.MAX_VALUE);
            Document doc = parseFragment(iso_8859_1, htmlErrors);

            Charset htmlCharset = EncodingUtils.htmlCharset(doc);

            if (htmlCharset != null) {
                return htmlCharset;
            }

            if (utf8 == null) {
                // All characters are plain ASCII, so it doesn't matter what we choose.
                return UTF_8;
            }

            long openTags = countTags(doc);
            long badTags = htmlErrors.stream().map(ParseError::getErrorMessage)
                    .filter(err -> err != null && err.matches(".*'[</>]'.*")).count();

            //condition for filtering input adapted from icu4j's CharsetDetector#MungeInput()
            if (openTags < 5 || openTags / 5 < badTags) {
                filterInput = false;
            } else {
                String wholeText = wholeText(doc);
                if (wholeText.length() < 100 && iso_8859_1.length() > 600) {
                    filterInput = false;
                } else {
                    filterInput = true;
                    iso_8859_1 = wholeText;
                }
            }
            text = iso_8859_1.getBytes(ISO_8859_1);
        }

        CharsetDetector icu4j = new CharsetDetector(text.length);
        icu4j.setText(text);

        for (CharsetMatch match : icu4j.detectAll()) {
            try {
                int confidence = match.getConfidence();
                if (confidence <= 0) {
                    continue;
                }

                Charset charset = CharsetUtils.forName(match.getName());

                // If we successfully filtered input based on 0x3C and 0x3E, then this must be an ascii-compatible charset
                // See https://issues.apache.org/jira/browse/TIKA-2771
                if (filterInput && !TAG_CHARS.equals(new String(TAG_BYTES, charset))) {
                    continue;
                }

                return charset;
            } catch (Exception e) {
                //ignore; if this charset isn't supported by this platform, it's probably not correct anyway.
            }
        }

        // No bytes are invalid in ISO-8859-1, so this one is always possible if there are no options left.
        // Also, has second-highest popularity on the web behind UTF-8.
        return ISO_8859_1;
    }

    @Override
    public String guessEncoding(InputStream is, String contentType) throws IOException {
        Charset charset = EncodingUtils.contentTypeCharset(contentType);
        Charset best = guessEncoding(is, charset);
        return best == null ? null : best.name();
    }



    //////////////////////////
    // JSOUP HELPER METHODS //
    //////////////////////////

    private static Document parseFragment(String html, ParseErrorList errors) {
        Document doc = new Document("");
        Node[] childNodes = Parser.parseFragment(html, null, "", errors).toArray(EMPTY_NODES);
        for (Node node : childNodes) {
            if (node.parentNode() != null) {
                node.remove();
            }
            doc.appendChild(node);
        }
        return doc;
    }

    private static long countTags(Node node) {
        long[] ret = {0};
        NodeTraversor.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if (node instanceof Document || node instanceof PseudoTextElement) {
                    //subclasses of Element that don't have start/end tags
                    return;
                }
                if (node instanceof Element || node instanceof DocumentType || node instanceof Comment) {
                    ret[0] += node.childNodeSize() == 0 ? 1 : 2;
                }
            }
            @Override
            public void tail(Node node, int depth) {
            }
        }, node);
        return ret[0];
    }

    private static String wholeText(Node node) {
        StringBuilder sb = new StringBuilder();
        NodeTraversor.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    sb.append(((TextNode) node).getWholeText());
                } else if (node instanceof DataNode) {
                    String data = ((DataNode) node).getWholeData();
                    do {
                        //make sure json-ld data is included in text stats
                        //otherwise, ignore css & javascript
                        if ("script".equalsIgnoreCase(node.nodeName())) {
                            if (node.attr("type").toLowerCase().contains("json")) {
                                sb.append(data);
                            }
                            break;
                        } else if ("style".equalsIgnoreCase(node.nodeName())) {
                            break;
                        }
                        node = node.parentNode();
                    } while (node != null);
                } else if (node instanceof Comment) {
                    String data = ((Comment) node).getData();
                    //avoid comments that are actually processing instructions or xml declarations
                    if (!data.contains("<!") && !data.contains("<?")) {
                        sb.append(data);
                    }
                } else if (node instanceof Element) {
                    //make sure all microdata itemprop "content" values are taken into consideration
                    sb.append(node.attr("content"));
                }
            }
            @Override
            public void tail(Node node, int depth) {
            }
        }, node);
        return sb.toString();
    }

}
