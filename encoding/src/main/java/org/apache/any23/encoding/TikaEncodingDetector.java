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

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.html.HtmlEncodingDetector;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.apache.tika.utils.CharsetUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return guessEncoding(input, null);
    }

    @Override
    public String guessEncoding(InputStream is, String contentType) throws IOException {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }

        Charset xmlCharset = detectXmlEncoding(is, 1024);

        HtmlEncodingDetector htmlEncodingDetector = new HtmlEncodingDetector();
        htmlEncodingDetector.setMarkLimit(16384);
        Charset htmlCharset = htmlEncodingDetector.detect(is, new Metadata());

        CharsetDetector charsetDetector = new CharsetDetector(65536);

        String incomingCharset = null;
        if (contentType != null) {
            MediaType mt = MediaType.parse(contentType);
            if (mt != null) {
                incomingCharset = mt.getParameters().get("charset");
            }
        }

        if (incomingCharset != null) {
            incomingCharset = CharsetUtils.clean(incomingCharset);
            if (incomingCharset != null) {
                charsetDetector.setDeclaredEncoding(incomingCharset);
            }
        }

        //enableInputFilter() needs to precede setText() to have any effect
        charsetDetector.enableInputFilter(true);
        charsetDetector.setText(is);

        Charset bestCharset = null;
        int bestConfidence = 0;
        for (CharsetMatch match : charsetDetector.detectAll()) {
            try {
                Charset charset = CharsetUtils.forName(match.getName());
                int confidence = match.getConfidence();
                if (StandardCharsets.UTF_8.equals(charset)) {
                    confidence *= 4;
                }
                if (charset.equals(htmlCharset) || charset.equals(xmlCharset)) {
                    confidence *= 16;
                }
                if (charset.name().equals(incomingCharset)) {
                    confidence *= 16;
                }
                if (confidence > bestConfidence) {
                    bestCharset = charset;
                    bestConfidence = confidence;
                }
            } catch (Exception e) {
                    //ignore
            }
        }

        if (bestConfidence >= 100)
            return bestCharset.name();
        if (htmlCharset != null)
            return htmlCharset.name();
        if (xmlCharset != null)
            return xmlCharset.name();
        if (bestCharset != null)
            return bestCharset.name();
        return null;
    }

    private static final Pattern xmlEncoding = Pattern.compile(
            "(?is)\\A\\s*<\\?\\s*xml\\s+[^<>]*encoding\\s*=\\s*(?:['\"]\\s*)?([-_:.a-z0-9]+)");

    static Charset detectXmlEncoding(InputStream input, int markLimit) throws IOException {
        if (input == null) {
            return null;
        }
        input.mark(markLimit);
        byte[] buffer = new byte[markLimit];
        int n = 0;
        int m = input.read(buffer);
        while (m != -1 && n < buffer.length) {
            n += m;
            m = input.read(buffer, n, buffer.length - n);
        }
        input.reset();

        // Interpret the head as ASCII and try to spot a meta tag with
        // a possible character encoding hint

        String head = StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(buffer, 0, n)).toString();

        Matcher matcher = xmlEncoding.matcher(head);

        if (matcher.find()) {
            try {
                return CharsetUtils.forName(matcher.group(1));
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

}
