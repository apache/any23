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
package org.apache.any23.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.any23.extractor.ExtractionContext;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link JSONWriter} and {@link JSONLDWriter} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Julio Caguano
 */
public class JSONWriterTest {

    @Test
    public void testJSONWriting() throws TripleHandlerException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeContent(new JSONWriter(baos));

        final String expected
                = "{ "
                + "\"quads\" : "
                + "["
                + "["
                + "{ \"type\" : \"bnode\", \"value\" : \"bn1\"}, "
                + "\"http://pred/1\", "
                + "{ \"type\" : \"uri\", \"value\" : \"http://value/1\"}, "
                + "\"http://graph/1\""
                + "], "
                + "["
                + "{ \"type\" : \"uri\", \"value\" : \"http://sub/2\"}, "
                + "\"http://pred/2\", "
                + "{\"type\" : \"literal\", \"value\" : \"language literal\", \"lang\" : \"en\", \"datatype\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\"}, "
                + "\"http://graph/2\""
                + "], "
                + "["
                + "{ \"type\" : \"uri\", \"value\" : \"http://sub/3\"}, "
                + "\"http://pred/3\", "
                + "{\"type\" : \"literal\", \"value\" : \"123\", \"lang\" : null, \"datatype\" : \"http://datatype\"}, "
                + "null"
                + "]"
                + "]"
                + "}";
        Assert.assertEquals(expected, baos.toString());
    }

    @Test
    public void testJSONLDWriting() throws TripleHandlerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeContent(new JSONLDWriter(baos));
        final String expected
                = "["
                + "{\"@graph\":[{\"@id\":\"http://sub/3\",\"http://pred/3\":[{\"@type\":\"http://datatype\",\"@value\":\"123\"}]}],\"@id\":\"http://any23.org/tmp/\"},"
                + "{\"@graph\":[{\"@id\":\"_:bn1\",\"http://pred/1\":[{\"@id\":\"http://value/1\"}]}],\"@id\":\"http://graph/1\"},"
                + "{\"@graph\":[{\"@id\":\"http://sub/2\",\"http://pred/2\":[{\"@language\":\"en\",\"@value\":\"language literal\"}]}],\"@id\":\"http://graph/2\"}"
                + "]";
        Assert.assertEquals(expected, baos.toString());
    }

    private void writeContent(FormatWriter writer) throws TripleHandlerException {
        final IRI documentIRI = SimpleValueFactory.getInstance().createIRI("http://fake/uri");
        writer.startDocument(documentIRI);
        writer.receiveTriple(
                SimpleValueFactory.getInstance().createBNode("bn1"),
                SimpleValueFactory.getInstance().createIRI("http://pred/1"),
                SimpleValueFactory.getInstance().createIRI("http://value/1"),
                SimpleValueFactory.getInstance().createIRI("http://graph/1"),
                null
        );
        writer.receiveTriple(
                SimpleValueFactory.getInstance().createIRI("http://sub/2"),
                SimpleValueFactory.getInstance().createIRI("http://pred/2"),
                SimpleValueFactory.getInstance().createLiteral("language literal", "en"),
                SimpleValueFactory.getInstance().createIRI("http://graph/2"),
                null
        );
        if (writer instanceof JSONWriter) {
            writer.receiveTriple(
                    SimpleValueFactory.getInstance().createIRI("http://sub/3"),
                    SimpleValueFactory.getInstance().createIRI("http://pred/3"),
                    SimpleValueFactory.getInstance().createLiteral("123", SimpleValueFactory.getInstance().createIRI("http://datatype")),
                    null,
                    null
            );
        } else if (writer instanceof JSONLDWriter) {
            ExtractionContext extractionContext = new ExtractionContext("rdf-nq", SimpleValueFactory.getInstance().createIRI("http://any23.org/tmp/"));
            writer.receiveTriple(
                    SimpleValueFactory.getInstance().createIRI("http://sub/3"),
                    SimpleValueFactory.getInstance().createIRI("http://pred/3"),
                    SimpleValueFactory.getInstance().createLiteral("123", SimpleValueFactory.getInstance().createIRI("http://datatype")),
                    null,
                    extractionContext
            );
        }
        writer.endDocument(documentIRI);
        writer.close();
    }
}
