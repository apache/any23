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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link TikaEncodingDetector}.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @version $Id$
 */
public class TikaEncodingDetectorTest {

    private TikaEncodingDetector detector;

    @Before
    public void setUp() {
        detector = new TikaEncodingDetector();
    }

    @After
    public void tearDown() {
        detector = null;
    }

    @Test
    public void testISO8859HTML() throws IOException {
         assertEncoding( "ISO-8859-1", "/microformats/xfn/encoding-iso-8859-1.html" );
    }

    @Test
    public void testISO8859XHTML() throws IOException {
         assertEncoding( "ISO-8859-1", "/microformats/xfn/encoding-iso-8859-1.xhtml" );
    }

    @Test
    public void testUTF8AfterTitle() throws IOException {
         assertEncoding( "UTF-8", "/microformats/xfn/encoding-utf-8-after-title.html" );
    }

    @Test
    public void testUTF8HTML() throws IOException {
         assertEncoding( "UTF-8", "/microformats/xfn/encoding-utf-8.html" );
    }

    @Test
    public void testUTF8XHTML() throws IOException {
         assertEncoding( "UTF-8", "/microformats/xfn/encoding-utf-8.xhtml" );
    }

    @Test
    public void testEncodingHTML() throws IOException {
         assertEncoding( "UTF-8", "/html/encoding-test.html" );
    }

    @Test
    public void testXMLEncodingPattern() throws IOException {
        String[] strings = {
                "<?xml encoding=\"UTF-8\"?>",
                " \n<?xMl encoding   = 'utf-8'?>",
                "\n <?Xml enCoding=Utf8?>"
        };
        for (String s : strings) {
            Charset detected = EncodingUtils.xmlCharset(s);
            assertEquals(detected, UTF_8);
        }
    }

    @Test
    public void testUTF8StateMachineForValid() throws IOException {
        int state = 0;
        for (int i = 0; i <= Character.MAX_CODE_POINT; i++) {
            byte[] bytes = new String(Character.toChars(i)).getBytes(UTF_8);

            for (byte b : bytes) {
                state = EncodingUtils.nextStateUtf8(state, b);
            }

            assertEquals(new String(bytes, UTF_8).codePointAt(0), state);
        }
    }

    @Test
    public void testUTF8StateMachineForInvalid() {
        for (int i = 0; i <= 0xFF; i++) { //check -1 state preservation
            assertEquals(-1, EncodingUtils.nextStateUtf8(-1, (byte)i));
        }

        for (int i = 0x80; i < 0xC2; i++) { //check invalid first byte
            assertEquals(-1, EncodingUtils.nextStateUtf8(0, (byte)i));
        }

        for (int i = 0xF5; i <= 0xFF; i++) { //illegal code points > 0x10FFFF
            assertEquals(-1, EncodingUtils.nextStateUtf8(0, (byte)i));
        }

        for (int i = 0xC2; i <= 0xF4; i++) { //check invalid continuation bytes
            //check invalid second byte
            int state0 = EncodingUtils.nextStateUtf8(0, (byte)i);
            assertTrue(state0 < -1);
            int from = i == 0xE0 ? 0xA0 : i == 0xF0 ? 0x90 : 0x80;
            for (int j = 0; j < from; j++) {
                assertEquals(-1, EncodingUtils.nextStateUtf8(state0, (byte)j));
            }
            int to = i == 0xED ? 0xA0 : i == 0xF4 ? 0x90 : 0xC0;
            for (int j = to; j <= 0xFF; j++) {
                assertEquals(-1, EncodingUtils.nextStateUtf8(state0, (byte)j));
            }
            if (i >= 0xE0) { //check invalid third byte
                int state1 = EncodingUtils.nextStateUtf8(state0, (byte) from);
                assertTrue(state1 < -1);
                for (int j = from + 1; j < to; j++) {
                    assertTrue(EncodingUtils.nextStateUtf8(state0, (byte)j) < -1);
                }
                for (int j = 0; j < 0x80; j++) {
                    assertEquals(-1, EncodingUtils.nextStateUtf8(state1, (byte) j));
                }
                for (int j = 0xC0; j <= 0xFF; j++) {
                    assertEquals(-1, EncodingUtils.nextStateUtf8(state1, (byte) j));
                }

                if (i >= 0xF0) { //check invalid 4th byte
                    int state2 = EncodingUtils.nextStateUtf8(state1, (byte) 0x80);
                    assertTrue(state2 < -1);
                    for (int j = 0x81; j < 0xC0; j++) {
                        assertTrue(EncodingUtils.nextStateUtf8(state1, (byte)j) < -1);
                    }
                    for (int j = 0; j < 0x80; j++) {
                        assertEquals(-1, EncodingUtils.nextStateUtf8(state2, (byte) j));
                    }
                    for (int j = 0xC0; j <= 0xFF; j++) {
                        assertEquals(-1, EncodingUtils.nextStateUtf8(state2, (byte) j));
                    }
                }
            }
        }
    }

    private static ByteArrayInputStream bytes(String string, Charset encoding) {
        return new ByteArrayInputStream(string.getBytes(encoding));
    }

    @Test
    public void testUtf8Simple() throws IOException {
        assertEquals("UTF-8", detector.guessEncoding(bytes("Hellö Wörld!", UTF_8)));
    }

    @Test
    public void testIso88591Simple() throws IOException {
        assertEquals("ISO-8859-1", detector.guessEncoding(bytes("Hellö Wörld!", ISO_8859_1)));
    }

    @Test
    public void testTikaIssue771() throws IOException {
        assertEquals("UTF-8", detector.guessEncoding(bytes("Hello, World!", UTF_8)));
    }

    @Test
    public void testTikaIssue868() throws IOException {
        assertEquals("UTF-8", detector.guessEncoding(bytes("Indanyl", UTF_8)));
    }

    @Test
    public void testTikaIssue2771() throws IOException {
        assertEquals("UTF-8", detector.guessEncoding(bytes("Name: Amanda\nJazz Band", UTF_8)));
    }

    private void assertEncoding(final String expected, final String resource) throws IOException {
        try (InputStream fis = getClass().getResourceAsStream(resource)) {
            String encoding = detector.guessEncoding(fis);
            assertEquals("Unexpected encoding", expected, encoding);
        }
    }

}
