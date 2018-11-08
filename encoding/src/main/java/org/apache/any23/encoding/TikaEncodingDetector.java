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
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Parser;
import org.jsoup.select.Evaluator;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static Charset charset(String charset) {
        try {
            return CharsetUtils.forName(charset);
        } catch (Exception e) {
            return null;
        }
    }

    static Charset xmlCharset(CharSequence str) {
        Matcher matcher = xmlEncoding.matcher(str);
        if (matcher.find()) {
            return charset(matcher.group(1));
        } else {
            return null;
        }
    }

    private static final Evaluator charsetMetas = QueryParser
            .parse("meta[http-equiv=content-type], meta[charset]");

    private static Charset htmlCharset(Element root) {
        for (Element meta : Selector.select(charsetMetas, root)) {
            Charset foundCharset = charset(meta.attr("charset"));
            if (foundCharset != null) {
                return foundCharset;
            }
            foundCharset = contentTypeCharset(meta.attr("content"));
            if (foundCharset != null) {
                return foundCharset;
            }
        }
        return null;
    }

    // Very efficient method to convert an input stream directly to an ISO-8859-1 encoded string
    private static String iso_8859_1(InputStream is) throws IOException {
        final boolean mark = is.markSupported();
        if (mark) {
            is.mark(Integer.MAX_VALUE);
        }
        StringBuilder chars = new StringBuilder(Math.max(is.available(), 8192));
        byte[] buffer = new byte[8192];
        int n;
        try {
            while ((n = is.read(buffer)) != -1) {
                chars.ensureCapacity(chars.length() + n);
                for (int i = 0; i < n; i++) {
                    chars.append((char)(buffer[i] & 0xFF));
                }
            }
        } finally {
            if (mark) {
                is.reset();
            }
        }
        return chars.toString();
    }

    private static final String TAG_CHARS = "< />";
    private static final byte[] TAG_BYTES = TAG_CHARS.getBytes(UTF_8);
    private static final Node[] EMPTY_NODES = new Node[0];

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

    private static long openTags(Node node) {
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

    private static final double z = 1.96;
    private static double wilsonScoreLowerBound(double p, double n) {
        if (n < 1) {
            return 0;
        }
        final double z2_n = z*z/n;
        return ((p + z2_n * 0.5) - z * Math.sqrt((p*(1-p) + z2_n*0.25)/n)) / (1 + z2_n);
    }
    private static int wilsonScoreLowerBoundPercent(int p, double n) {
        return (int)(wilsonScoreLowerBound(p / 100.0, n) * 100.0);
    }

    //Adapted from icu4j's CharsetRecog_UTF8 class to accept ISO-8859-1 string
    private static Boolean isUtf8(String iso_8859_1) {
        boolean     hasBOM = false;
        int         numValid = 0;
        int         numInvalid = 0;
        int         i;
        int         trailBytes;
        final int length = iso_8859_1.length();
        if (length >= 3 &&
                (iso_8859_1.charAt(0) & 0xFF) == 0xef && (iso_8859_1.charAt(1) & 0xFF) == 0xbb && (iso_8859_1.charAt(2) & 0xFF) == 0xbf) {
            hasBOM = true;
        }

        // Scan for multi-byte sequences
        for (i = 0; i < length; i++) {
            int b = iso_8859_1.charAt(i);
            if ((b & 0x80) == 0) {
                continue;   // ASCII
            }
            // Hi bit on char found.  Figure out how long the sequence should be
            if ((b & 0x0e0) == 0x0c0) {
                trailBytes = 1;
            } else if ((b & 0x0f0) == 0x0e0) {
                trailBytes = 2;
            } else if ((b & 0x0f8) == 0xf0) {
                trailBytes = 3;
            } else {
                numInvalid++;
                continue;
            }

            // Verify that we've got the right number of trail bytes in the sequence
            for (;;) {
                i++;
                if (i>=length) {
                    break;
                }
                b = iso_8859_1.charAt(i);
                if ((b & 0xc0) != 0x080) {
                    numInvalid++;
                    break;
                }
                if (--trailBytes == 0) {
                    numValid++;
                    break;
                }
            }
        }

        if (hasBOM && numValid >= numInvalid * 10) {
            //includes case where numValid == numInvalid == 0
            return Boolean.TRUE;
        } else if (numValid == 0 && numInvalid == 0) {
            // Plain ASCII
            return null;
        } else if (numValid > numInvalid * 10) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private static Charset guessEncoding(InputStream is, Charset declared) throws IOException {
        Charset xmlCharset;
        Charset htmlCharset;
        boolean filterInput;
        byte[] text;
        CharsetDetector icu4j;
        {
            // ISO-8859-1 is Java's only "standard charset" which maps 1-to-1 onto the first 256 unicode characters;
            // use ISO-8859-1 for round-tripping of bytes after stripping html/xml tags from input
            String iso_8859_1 = iso_8859_1(is);
            Boolean utf8 = isUtf8(iso_8859_1);

            if (Boolean.TRUE.equals(utf8)) {
                // > 92% of the web is UTF-8. Do not risk false positives from obscure charsets.
                // See https://issues.apache.org/jira/browse/TIKA-2771
                return UTF_8;
            }

            if (utf8 == null && declared != null) {
                // All characters are plain ASCII.
                // Here, it doesn't really matter what charset we detect, as we'll
                // get same data out. Use declared charset if available.
                return declared;
            }

            xmlCharset = xmlCharset(iso_8859_1);

            if (utf8 == null && xmlCharset != null) {
                // All characters are plain ASCII, so it doesn't matter what we choose.
                // Use xml encoding if available.
                return xmlCharset;
            }

            ParseErrorList errors = ParseErrorList.tracking(Integer.MAX_VALUE);

            Document doc = parseFragment(iso_8859_1, errors);

            htmlCharset = htmlCharset(doc);

            if (utf8 == null) {
                // All characters are plain ASCII, so it doesn't matter what we choose.
                // Use html meta charset if available. Otherwise, UTF-8.
                return htmlCharset != null ? htmlCharset : UTF_8;
            }

            // Here, it's probably not UTF-8.
            // Wait for more text statistics before returning any of the declared charsets.
            // See https://issues.apache.org/jira/browse/TIKA-539

            long openTags = openTags(doc);
            long badTags = errors.stream().filter(err -> err.getErrorMessage().matches(".*'[</>]'.*")).count();
            String wholeText = wholeText(doc);

            //condition for filtering input based roughly on icu4j's CharsetDetector#MungeInput()
            if (openTags < 5 || openTags / 5 < badTags ||
                    (wholeText.length() < 100 && iso_8859_1.length() > 600)) {
                filterInput = false;
            } else {
                filterInput = true;
                iso_8859_1 = wholeText;
            }
            text = iso_8859_1.getBytes(ISO_8859_1);

            icu4j = new CharsetDetector(text.length);
            icu4j.setText(text);
        }

        Charset bestCharset = null;
        int bestConfidence = 0;
        for (CharsetMatch match : icu4j.detectAll()) {
            try {
                Charset charset = CharsetUtils.forName(match.getName());
                int confidence = match.getConfidence();
                if (confidence <= 0) {
                    continue;
                }
                // If we successfully filtered input based on 0x3C and 0x3E, then this must be an ascii-compatible charset
                // See https://issues.apache.org/jira/browse/TIKA-2771
                if (filterInput && !TAG_CHARS.equals(new String(TAG_BYTES, charset))) {
                    continue;
                }

                Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text), charset));
                int ch;
                long byteCountAlpha = 0;
                while ((ch = reader.read()) != -1) {
                    if (Character.isHighSurrogate((char)ch)) {
                        int lo = reader.read();
                        if (lo == -1) {
                            break;
                        }
                        int codePoint = Character.toCodePoint((char)ch, (char)lo);
                        if (Character.isAlphabetic(codePoint) || Character.isIdeographic(codePoint)) {
                            byteCountAlpha += new String(new char[]{(char)ch, (char)lo}).getBytes(charset).length;
                        }
                    } else if (Character.isAlphabetic(ch) || Character.isIdeographic(ch)) {
                        byteCountAlpha += new String(new char[]{(char)ch}).getBytes(charset).length;
                    }
                }

                // For charsets detected based on frequency statistics, reduce confidence relative to
                // declared charsets' stated confidence, using the lower bound of the wilson score confidence interval,
                // taking the initial confidence to be p and the total number of alphabetic bytes to be n.
                // See https://issues.apache.org/jira/browse/TIKA-2771
                confidence = wilsonScoreLowerBoundPercent(confidence, byteCountAlpha);

                if (charset.equals(declared) || charset.equals(xmlCharset) || charset.equals(htmlCharset)) {
                    confidence = (100 + confidence) / 2; //take arithmetic mean, as in icu4j
                }

                if (confidence > bestConfidence) {
                    bestCharset = charset;
                    bestConfidence = confidence;
                }
            } catch (Exception e) {
                //ignore; if this charset isn't supported by this platform, it's probably not correct anyway.
            }
        }

        return bestCharset != null ? bestCharset : declared;
    }

    @Override
    public String guessEncoding(InputStream is, String contentType) throws IOException {
        Charset charset = contentTypeCharset(contentType);
        Charset best = guessEncoding(is, charset);
        return best == null ? null : best.name();
    }

    private static final Pattern contentTypeCharsetPattern =
            Pattern.compile("(?i)\\bcharset\\s*=[\\s\"']*([^\\s,;\"']+)");

    private static Charset contentTypeCharset(CharSequence contentType) {
        if (contentType == null)
            return null;
        Matcher m = contentTypeCharsetPattern.matcher(contentType);
        if (m.find()) {
            try {
                return CharsetUtils.forName(m.group(1));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static final Pattern xmlEncoding = Pattern.compile(
            "(?is)\\A\\s*<\\?\\s*xml\\s+[^<>]*encoding\\s*=\\s*(?:['\"]\\s*)?([-_:.a-z0-9]+)");
}
