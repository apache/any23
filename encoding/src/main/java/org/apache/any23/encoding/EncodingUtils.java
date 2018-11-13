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

import org.apache.tika.detect.TextStatistics;
import org.apache.tika.utils.CharsetUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
            for (int i = 0; i < n; i++) {
                chars.append((char)(buffer[i] & 0xFF));
            }
        }
        return chars.toString();
    }


    //get correct variant, or null if charset is incompatible with stats
    static Charset correctVariant(TextStatistics stats, Charset charset) {
        if (charset == null) {
            return null;
        }
        switch (charset.name()) {
            //ISO-8859-1 variants
            case "ISO-8859-1":
                //Take a hint from icu4j's CharsetRecog_8859_1 and Tika's UniversalEncodingListener:
                // return windows-1252 before ISO-8859-1 if:
                // (1) C1 ctrl chars are used (as in icu4j), or
                // (2) '\r' is used (as in Tika)
                if ((stats.count('\r') != 0 || hasC1Control(stats)) && hasNoneOf(stats, windows1252Illegals)) {
                    try {
                        return forName("windows-1252");
                    } catch (Exception e) {
                        //ignore
                    }
                }
                return iso_8859_1_or_15(stats);
            case "windows-1252":
                return hasNoneOf(stats, windows1252Illegals) ? charset : iso_8859_1_or_15(stats);

            //ISO-8859-2 variants
            case "ISO-8859-2":
                //Take a hint from icu4j's CharsetRecog_8859_2 class:
                // return windows-1250 before ISO-8859-2 if has valid C1 chars
                if (hasC1Control(stats) && hasNoneOf(stats, windows1250Illegals)) {
                    try {
                        return forName("windows-1250");
                    } catch (Exception e) {
                        //ignore
                    }
                }
                return charset;
            case "windows-1250":
                return hasNoneOf(stats, windows1250Illegals) ? charset : charset("ISO-8859-2");

            //ISO-8859-7 variants
            case "ISO-8859-7":
                //Take a hint from icu4j's CharsetRecog_8859_7 class:
                // return windows-1253 before ISO-8859-7 if has valid C1 chars
                if (hasC1Control(stats) && hasNoneOf(stats, windows1253Illegals)) {
                    try {
                        return forName("windows-1253");
                    } catch (Exception e) {
                        //ignore
                    }
                }
                return hasNoneOf(stats, iso_8859_7Illegals) ? charset : null;
            case "windows-1253":
                return hasNoneOf(stats, windows1253Illegals) ? charset :
                        hasNoneOf(stats, iso_8859_7Illegals) ? charset("ISO-8859-7") : null;

            //ISO-8859-8 variants
            case "ISO-8859-8":
            case "ISO-8859-8-I":
                //Take a hint from icu4j's CharsetRecog_8859_8 class:
                // return windows-1255 before ISO-8859-8 if has valid C1 chars
                if (hasC1Control(stats) && hasNoneOf(stats, windows1255Illegals)) {
                    try {
                        return forName("windows-1255");
                    } catch (Exception e) {
                        //ignore
                    }
                }
                return hasNoneOf(stats, iso_8859_8Illegals) ? charset : null;
            case "windows-1255":
                return hasNoneOf(stats, windows1255Illegals) ? charset :
                        hasNoneOf(stats, iso_8859_8Illegals) ? charset("ISO-8859-8") : null;

            //ISO-8859-9 variants
            case "ISO-8859-9":
                //Take a hint from icu4j's CharsetRecog_8859_9 class:
                // return windows-1254 before ISO-8859-9 if has valid C1 chars
                if (hasC1Control(stats) && hasNoneOf(stats, windows1254Illegals)) {
                    try {
                        return forName("windows-1254");
                    } catch (Exception e) {
                        //ignore
                    }
                }
                return charset;
            case "windows-1254":
                return hasNoneOf(stats, windows1254Illegals) ? charset : charset("ISO-8859-9");

            //Others: just make sure no illegal characters are present
            case "windows-1251":
                return hasNoneOf(stats, windows1251Illegals) ? charset : null;
            case "ISO-8859-6":
                return hasNoneOf(stats, iso_8859_6Illegals) ? charset : null;
            default:
                return charset;
        }
    }

    private static Charset iso_8859_1_or_15(TextStatistics stats) {
        //Take a hint from Tika's UniversalEncodingListener:
        // return ISO-8859-15 before ISO-8859-1 if currency/euro symbol is used
        if (stats.count(0xa4) != 0) {
            try {
                return forName("ISO-8859-15");
            } catch (Exception e) {
                //ignore
            }
        }
        return StandardCharsets.ISO_8859_1;
    }

    private static final int[] windows1252Illegals = {0x81, 0x8D, 0x8F, 0x90, 0x9D};
    private static final int[] windows1250Illegals = {0x81, 0x83, 0x88, 0x90, 0x98};
    private static final int[] iso_8859_7Illegals = {0xAE, 0xD2, 0xFF};
    private static final int[] windows1253Illegals = {0x81, 0x88, 0x8A, 0x8C, 0x8D,
            0x8E, 0x8F, 0x90, 0x98, 0x9A, 0x9C, 0x9D, 0x9E, 0x9F, 0xAA, 0xD2, 0xFF};

    private static final int[] windows1255Illegals = {0x81, 0x8A, 0x8C, 0x8D, 0x8E, 0x8F, 0x90, 0x9A,
            0x9C, 0x9D, 0x9E, 0x9F, 0xCA, 0xD9, 0xDA, 0xDB, 0xDC, 0xDD, 0xDE, 0xDF, 0xFB, 0xFC, 0xFF};

    private static final int[] iso_8859_8Illegals = {0xA1, 0xBF, 0xC0, 0xC1, 0xC2, 0xC3, 0xC4, 0xC5,
            0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xCB, 0xCC, 0xCD, 0xCE, 0xCF, 0xD0, 0xD1, 0xD2, 0xD3, 0xD4,
            0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xDB, 0xDC, 0xDD, 0xDE, 0xFB, 0xFC, 0xFF};

    private static final int[] windows1254Illegals = {0x81, 0x8D, 0x8E, 0x8F, 0x90, 0x9D, 0x9E};

    private static final int[] windows1251Illegals = {0x98};

    private static final int[] iso_8859_6Illegals = {0xA1, 0xA2, 0xA3, 0xA5, 0xA6, 0xA7,
            0xA8, 0xA9, 0xAA, 0xAB, 0xAE, 0xAF, 0xB0, 0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6,
            0xB7, 0xB8, 0xB9, 0xBA, 0xBC, 0xBD, 0xBE, 0xC0, 0xDB, 0xDC, 0xDD, 0xDE, 0xDF,
            0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA, 0xFB, 0xFC, 0xFD, 0xFE, 0xFF};


    private static boolean hasNoneOf(TextStatistics stats, int[] illegals) {
        for (int i : illegals) {
            if (stats.count(i) != 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasC1Control(TextStatistics ts) {
        for (int i = 0x80; i < 0xA0; i++) {
            if (ts.count(i) != 0) {
                return true;
            }
        }
        return false;
    }

    /*
     * Returns a custom implementation of Tika's TextStatistics class for an input stream
     */
    static TextStatistics stats(InputStream stream) throws IOException {
        long numInvalid = 0;
        long numValid = 0;

        int state = 0;

        byte[] buffer = new byte[8192];

        class CustomTextStatistics extends TextStatistics {
            private boolean looksLikeUTF8;
            @Override
            public boolean looksLikeUTF8() {
                //override to be 100% precise
                return looksLikeUTF8;
            }
        }

        CustomTextStatistics stats = new CustomTextStatistics();

        int n;
        while ((n = stream.read(buffer)) != -1) {
            stats.addData(buffer, 0, n);

            for (int i = 0; i < n; i++) {
                state = nextStateUtf8(state, (byte) i);
                if (state == -1) { //bad state
                    numInvalid++;
                    state = 0; //reset state to valid
                } else if (state >= 0) { //state is a valid codepoint
                    //take a hint from jchardet: count SO, SI, ESC as invalid
                    //(but Tika calls ESC (0x1B) a "safe control", so let's OK that one for now).
                    if (state == 0x0E || state == 0x0F /* || state == 0x1B*/) {
                        numInvalid++;
                    } else if (state > 0x7F) { //was at least a two-byte sequence
                        numValid++;

                        //shortcut: avoid reading entire stream
                        //if we can detect early on that it's UTF-8
                        if (numValid > (numInvalid + 1) * 10) {
                            stats.looksLikeUTF8 = true;
                            return stats;
                        }
                    }
                }
            }
        }

        //condition for success based roughly on ICU4j's CharsetRecog_UTF8 class:
        // Valid multi-byte UTF-8 sequences are unlikely to occur by chance
        stats.looksLikeUTF8 = numValid > numInvalid * 10;
        return stats;
    }


    /**
     * Returns the next UTF-8 state given the next byte of input and the current state.
     * If the input byte is the last byte in a valid UTF-8 byte sequence,
     * the returned state will be the corresponding unicode character (in the range of 0 through 0x10FFFF).
     * Otherwise, a negative integer is returned. A state of -1 is returned whenever an
     * invalid UTF-8 byte sequence is detected.
     */
    static int nextStateUtf8(int currentState, byte nextByte) {

        // This function was adapted from jchardet, with 2 bugfixes:
        // (1) jchardet counted codepoints in Supplementary Multilingual Plane as invalid
        // (2) jchardet counted codepoints past 0x10FFFF as valid
        // Cf. https://issues.apache.org/jira/browse/TIKA-2038

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
                return (nextByte & 0xC0) == 0x80 ? currentState << 6 | nextByte & 0x9000003F : -1;
            case 0x80000000: //3rd-to-last continuation byte, check overlong
                // jchardet's (incorrect) version was: 0xA0-0xBF
                // Need to allow 0x90-0x9F (Supplementary Multilingual Plane) as well!
                return (nextByte & 0xE0) == 0xA0 || (nextByte & 0xF0) == 0x90
                        ? currentState << 6 | nextByte & 0x9000003F : -1;
            case 0xD0000000: //3rd-to-last continuation byte, check undefined
                //anything greater than or equal to 0x90 is illegal
                return (nextByte & 0xF0) == 0x80 ? currentState << 6 | nextByte & 0x9000003F : -1;
            case 0x90000000: //2nd-to-last continuation byte
                return (nextByte & 0xC0) == 0x80 ? currentState << 6 | nextByte & 0xC000003F : -1;
            case 0xA0000000: //2nd-to-last continuation byte, check overlong
                return (nextByte & 0xE0) == 0xA0 ? currentState << 6 | nextByte & 0xC000003F : -1;
            case 0xB0000000: //2nd-to-last continuation byte, check surrogate
                return (nextByte & 0xE0) == 0x80 ? currentState << 6 | nextByte & 0xC000003F : -1;
            case 0xC0000000: //last continuation byte
                return (nextByte & 0xC0) == 0x80 ? currentState << 6 | nextByte & 0x3F : -1;
            case 0xF0000000: //error
                return -1;
            default:
                throw new IllegalStateException("illegal state " + Integer.toHexString(currentState));
        }
    }


    static Charset forName(String charset) throws Exception {
        try {
            return CharsetUtils.forName(charset);
        } catch (Exception e) {
            //ICU4j sometimes returns 'ISO-8859-8-I', which is unsupported!
            // Cf. https://en.wikipedia.org/wiki/ISO/IEC_8859-8
            // "Nominally ISO-8859-8 (code page 28598) is for 'visual order',
            // and ISO-8859-8-I (code page 38598) is for logical order.
            // But usually in practice, and required for HTML and XML
            // documents, ISO-8859-8 also stands for logical order text."
            charset = charset.replaceAll("(?i)-I\\b", "");
            try {
                return CharsetUtils.forName(charset);
            } catch (Exception e1) {
                //ignore
            }
            throw e;
        }
    }

    private static Charset charset(String charset) {
        try {
            return forName(charset);
        } catch (Exception e) {
            return null;
        }
    }

    private static final Evaluator charsetMetas = QueryParser
            .parse("meta[http-equiv=content-type], meta[charset]");

    static Charset htmlCharset(TextStatistics stats, Element root) {
        for (Element meta : Selector.select(charsetMetas, root)) {
            Charset foundCharset = correctVariant(stats, charset(meta.attr("charset")));
            if (foundCharset != null) {
                return foundCharset;
            }
            foundCharset = correctVariant(stats, contentTypeCharset(meta.attr("content")));
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
                return forName(m.group(1));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static final Pattern xmlEncoding = Pattern.compile(
            "(?is)\\A\\s*<\\?\\s*xml\\s+[^<>]*encoding\\s*=\\s*(?:['\"]\\s*)?([-_:.a-z0-9]+)");

    static Charset xmlCharset(TextStatistics stats, CharSequence str) {
        Matcher matcher = xmlEncoding.matcher(str);
        if (matcher.find()) {
            return correctVariant(stats, charset(matcher.group(1)));
        } else {
            return null;
        }
    }


    //uncomment this handy function to print out invalid bytes for a charset
//    public static void main(String[] args) throws Exception {
//        String[] cs = {
//                "ISO-8859-15",
//                "windows-1252", "ISO-8859-1",
//                "windows-1250", "ISO-8859-2",
//                "windows-1253", "ISO-8859-7",
//                "windows-1255", "ISO-8859-8",
//                "windows-1254", "ISO-8859-9",
//                "windows-1251",
//                "windows-1256",
//                "ISO-8859-5",
//                "ISO-8859-6"
//        };
//
//        for (String name : cs) {
//            System.out.println(IntStream.range(0, 256).filter(i -> {
//                try {
//                    Charset c = EncodingUtils.forName(name);
//                    String s = new String(new byte[]{(byte) i}, c);
//                    byte[] bytes = s.getBytes(c);
//                    return bytes.length != 1 || bytes[0] != (byte)i;
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }).mapToObj(i -> "0x" + Integer.toHexString(i).toUpperCase())
//                    .collect(Collectors.joining(", ", "undefined " + name + " = {", "};")));
//        }
//    }

}
