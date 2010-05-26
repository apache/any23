/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.encoding;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Test case for {@link org.deri.any23.encoding.TikaEncodingDetector}.
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

    private void assertEncoding(final String expected, final String resource) throws IOException {
        InputStream fis = this.getClass().getResourceAsStream(resource);
        try {
            String encoding = detector.guessEncoding(fis);
            Assert.assertEquals( "Unexpected encoding", expected, encoding );
        } finally {
            fis.close();
        }
    }

}
