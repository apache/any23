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

package org.apache.any23.rdf;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.rio.RDFFormat;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Reference test class for {@link RDFUtils}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@gmail.com)
 */
public class RDFUtilsTest {

    @Test
    public void testFixAbsoluteIRI() throws UnsupportedEncodingException, URISyntaxException {
        Assert.assertEquals(
                "Error: passed IRIs are not the same.",
                "http://example.com/resource/the%20godfather",
                RDFUtils.fixAbsoluteIRI("http://example.com/resource/the godfather")
        );

        Assert.assertEquals(
                "Error: passed IRIs are not the same.",
                "http://dbpedia.org/",
                RDFUtils.fixAbsoluteIRI("http://dbpedia.org")
        );
    }

    @Test
    public void testGetXSDDate() throws DatatypeConfigurationException, ParseException {
        Assert.assertEquals("1997-09-01T13:00:00.000Z",
                RDFUtils.getXSDDate(
                        "19970901T1300Z",
                        "yyyyMMdd'T'HHmm'Z'"
                )
        );
    }

    /**
     * Tests the extension support.
     */
    @Test
    public void testGetRDFFormatByExtension() {
        Assert.assertEquals(RDFFormat.NTRIPLES, RDFUtils.getFormatByExtension("nt").get());
        Assert.assertEquals(RDFFormat.TURTLE  , RDFUtils.getFormatByExtension("ttl").get());
        Assert.assertEquals(RDFFormat.NQUADS, RDFUtils.getFormatByExtension("nq").get());
        Assert.assertEquals(RDFFormat.NQUADS, RDFUtils.getFormatByExtension(".nq").get());
    }

    /**
     * Tests the <code>NQuads</code> format support.
     */
    @Test
    public void testGetNQuadsFormat() {
        RDFUtils.getFormats().contains(RDFFormat.NQUADS);
    }

    /**
     * Tests the <code>NQuads</code> parsing support.
     */
    @Test
    public void testGetNQuadsParser() {
        Assert.assertNotNull( RDFUtils.getParser(RDFFormat.NQUADS) );
    }

    /**
     * Tests the <code>NQuads</code> writing support.
     */
    @Test
    public void testGetNQuadsWriter() {
        Assert.assertNotNull(
                RDFUtils.getWriter(RDFFormat.NQUADS, new OutputStreamWriter(new ByteArrayOutputStream() ) )
        );
    }

}
