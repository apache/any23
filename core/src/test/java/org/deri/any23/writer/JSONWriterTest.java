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

package org.deri.any23.writer;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;

import java.io.ByteArrayOutputStream;

/**
 * Test case for {@link org.deri.any23.writer.JSONWriter} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONWriterTest {

    private JSONWriter jsonWriter;

    @Test
    public void testWriting() throws TripleHandlerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jsonWriter = new JSONWriter(baos);
        final URI documentURI = new URIImpl("http://fake/uri");
        jsonWriter.startDocument(documentURI);
        jsonWriter.receiveTriple(
                new BNodeImpl("bn1"),
                new URIImpl("http://pred/1"),
                new URIImpl("http://value/1"),
                new URIImpl("http://graph/1"),
                null
        );
        jsonWriter.receiveTriple(
                new URIImpl("http://sub/2"),
                new URIImpl("http://pred/2"),
                new LiteralImpl("language literal", "en"),
                new URIImpl("http://graph/2"),
                null
        );
        jsonWriter.receiveTriple(
                new URIImpl("http://sub/3"),
                new URIImpl("http://pred/3"),
                new LiteralImpl("123", new URIImpl("http://datatype")),
                null,
                null
        );
        jsonWriter.endDocument(documentURI);
        jsonWriter.close();

        final String expected =
            "{ " +
            "\"quads\" : " +
            "[" +
            "[" +
            "{ \"type\" : \"bnode\", \"value\" : \"bn1\"}, " +
            "\"http://pred/1\", " +
            "{ \"type\" : \"uri\", \"value\" : \"http://value/1\"}, " +
            "\"http://graph/1\"" +
            "], " +
            "[" +
            "{ \"type\" : \"uri\", \"value\" : \"http://sub/2\"}, " +
            "\"http://pred/2\", " +
            "{\"type\" : \"literal\", \"value\" : \"language literal\", \"lang\" : \"en\", \"datatype\" : null}, " +
            "\"http://graph/2\"" +
            "], " +
            "[" +
            "{ \"type\" : \"uri\", \"value\" : \"http://sub/3\"}, " +
            "\"http://pred/3\", " +
            "{\"type\" : \"literal\", \"value\" : \"123\", \"lang\" : null, \"datatype\" : \"http://datatype\"}, " +
            "null" +
            "]" +
            "]" +
            "}";
        Assert.assertEquals(expected, baos.toString());
    }
}
