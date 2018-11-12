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

import org.apache.tika.utils.CharsetUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hans Brende
 */
class EncodingUtils {

    /**
     * Very efficient method to convert an input stream directly to an ISO-8859-1 encoded string
     */
    static String iso_8859_1(InputStream is) throws IOException {
        StringBuilder chars = new StringBuilder(Math.max(is.available(), 8192));
        byte[] buffer = new byte[8192];
        int n;
        while ((n = is.read(buffer)) != -1) {
            chars.ensureCapacity(chars.length() + n);
            for (int i = 0; i < n; i++) {
                chars.append((char)(buffer[i] & 0xFF));
            }
        }
        return chars.toString();
    }


    /**
     * Returns null for an ASCII stream, true for a UTF-8 stream,
     * and false for a stream encoded in something other than UTF-8.
     */
    static Boolean isUTF8(InputStream stream) throws IOException {
        long numInvalid = 0;
        long numValid = 0;

        int state = 0;

        int i;
        while ((i = stream.read()) != -1) {
            state = nextStateUtf8(state, (byte)i);
            if (state == -1) { //bad state
                numInvalid++;
            } else if (state >= 0) { //state is a valid codepoint
                //take a hint from jchardet: count SO, SI, ESC as invalid
                if (state == 0x0E || state == 0x0F || state == 0x1B) {
                    numInvalid++;
                } else if (state > 0x7F) { //was at least a two-byte sequence
                    numValid++;

                    //shortcut: avoid reading entire stream
                    //if we can detect early on that it's UTF-8
                    if (numValid > (numInvalid + 1) * 10) {
                        return true;
                    }
                }
            }
        }

        if (numValid == 0 && numInvalid == 0) { //Plain ASCII
            return null;
        }
        //condition for success based roughly on ICU4j's CharsetRecog_UTF8 class
        //valid multi-byte UTF-8 sequences are unlikely to occur by chance
        return numValid > numInvalid * 10;
    }


    /**
     * Returns the next UTF-8 state given the next byte of input and the current state.
     * If the input byte is the last byte in a valid UTF-8 byte sequence,
     * the returned state will be the corresponding unicode character (in the range of 0 through 0x10FFFF).
     * Otherwise, a negative integer is returned. A state of -1 is returned whenever an
     * invalid UTF-8 byte sequence is detected.
     */
    static int nextStateUtf8(int currentState, byte nextByte) {
        switch (currentState & 0xF0000000) {
            case 0:
                if ((nextByte & 0x80) == 0) { //0 trailing bytes (ASCII)
                    return nextByte;
                } else if ((nextByte & 0xE0) == 0xC0) { //1 trailing byte
                    if (nextByte == (byte) 0xC0 || nextByte == (byte) 0xC1) { //0xCO & 0xC1 are overlong
                        return -1;
                    } else {
                        return nextByte & 0xC000001F;
                    }
                } else if ((nextByte & 0xF0) == 0xE0) { //2 trailing bytes
                    if (nextByte == (byte) 0xE0) { //possibly overlong
                        return nextByte & 0xA000000F;
                    } else if (nextByte == (byte) 0xED) { //possibly surrogate
                        return nextByte & 0xB000000F;
                    } else {
                        return nextByte & 0x9000000F;
                    }
                } else if ((nextByte & 0xFC) == 0xF0) { //3 trailing bytes
                    if (nextByte == (byte) 0xF0) { //possibly overlong
                        return nextByte & 0x80000007;
                    } else {
                        return nextByte & 0xE0000007;
                    }
                } else if (nextByte == (byte) 0xF4) { //3 trailing bytes, possibly undefined
                    //modification from jchardet: don't allow > 0x10FFFF.
                    return nextByte & 0xD0000007;
                } else {
                    return -1;
                }
            case 0xE0000000: //3rd-to-last continuation byte
                if ((nextByte & 0xC0) == 0x80) {
                    return (currentState << 6) | (nextByte & 0x9000003F);
                } else {
                    return -1;
                }
            case 0x80000000: //3rd-to-last continuation byte, check overlong
                // jchardet's (incorrect) version was: 0xA0-0xBF
                // Need to allow 0x90-0x9F (Supplementary Multilingual Plane) as well!
                if ((nextByte & 0xE0) == 0xA0 || (nextByte & 0xF0) == 0x90) {
                    return (currentState << 6) | (nextByte & 0x9000003F);
                } else {
                    return -1;
                }
            case 0xD0000000: //3rd-to-last continuation byte, check undefined
                //anything greater than or equal to 0x90 is illegal
                if ((nextByte & 0xF0) == 0x80) {
                    return (currentState << 6) | (nextByte & 0x9000003F);
                } else {
                    return -1;
                }
            case 0x90000000: //2nd-to-last continuation byte
                if ((nextByte & 0xC0) == 0x80) {
                    return (currentState << 6) | (nextByte & 0xC000003F);
                } else {
                    return -1;
                }
            case 0xA0000000: //2nd-to-last continuation byte, check overlong
                if ((nextByte & 0xE0) == 0xA0) {
                    return (currentState << 6) | (nextByte & 0xC000003F);
                } else {
                    return -1;
                }
            case 0xB0000000: //2nd-to-last continuation byte, check surrogate
                if ((nextByte & 0xE0) == 0x80) {
                    return (currentState << 6) | (nextByte & 0xC000003F);
                } else {
                    return -1;
                }
            case 0xC0000000: //last continuation byte
                if ((nextByte & 0xC0) == 0x80) {
                    return (currentState << 6) | (nextByte & 0x3F);
                } else {
                    return -1;
                }
            case 0xF0000000: //error
                return -1;
            default:
                throw new IllegalStateException("illegal state " + Integer.toHexString(currentState));
        }
    }


    private static Charset charset(String charset) {
        try {
            return CharsetUtils.forName(charset);
        } catch (Exception e) {
            return null;
        }
    }

    private static final Evaluator charsetMetas = QueryParser
            .parse("meta[http-equiv=content-type], meta[charset]");

    static Charset htmlCharset(Element root) {
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


    private static final Pattern contentTypeCharsetPattern =
            Pattern.compile("(?i)\\bcharset\\s*=[\\s\"']*([^\\s,;\"']+)");

    static Charset contentTypeCharset(CharSequence contentType) {
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

    static Charset xmlCharset(CharSequence str) {
        Matcher matcher = xmlEncoding.matcher(str);
        if (matcher.find()) {
            return charset(matcher.group(1));
        } else {
            return null;
        }
    }

}
