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
 * Test case for {@link JSONLDWriter} and deprecated JSONWriter classes.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Julio Caguano
 */
public class JSONWriterTest {

    @Test
    @Deprecated
    public void testJSONWriting() throws TripleHandlerException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeContentComplicated(new JSONWriter(baos));

        final String expected 
            = "{\n"
            + "  \"quads\" : [ [ {\n"
            + "    \"type\" : \"bnode\",\n"
            + "    \"value\" : \"bn1\"\n"
            + "  }, \"http://pred/1\", {\n"
            + "    \"type\" : \"uri\",\n"
            + "    \"value\" : \"http://value/1\"\n"
            + "  }, \"http://graph/1\" ], [ {\n"
            + "    \"type\" : \"uri\",\n"
            + "    \"value\" : \"http://sub/2\"\n"
            + "  }, \"http://pred/2\", {\n"
            + "    \"type\" : \"literal\",\n"
            + "    \"value\" : \"language literal\",\n"
            + "    \"lang\" : \"en\",\n"
            + "    \"datatype\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\"\n"
            + "  }, \"http://graph/2\" ], [ {\n"
            + "    \"type\" : \"uri\",\n"
            + "    \"value\" : \"http://sub/3\"\n"
            + "  }, \"http://pred/3\", {\n"
            + "    \"type\" : \"literal\",\n"
            + "    \"value\" : \"123\",\n"
            + "    \"lang\" : null,\n"
            + "    \"datatype\" : \"http://datatype\"\n"
            + "  }, null ] ]\n"
            + "}";
        Assert.assertEquals(expected, baos.toString());

        baos.reset();
        writeContentSimple(new JSONWriter(baos));
        Assert.assertEquals(expected, baos.toString());
    }

    @Test
    public void testJSONLDWriting() throws TripleHandlerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeContentComplicated(new JSONLDWriter(baos));
        final String expected =
                "[ {\n" +
                "  \"@graph\" : [ {\n" +
                "    \"@id\" : \"http://sub/3\",\n" +
                "    \"http://pred/3\" : [ {\n" +
                "      \"@type\" : \"http://datatype\",\n" +
                "      \"@value\" : \"123\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"@id\" : \"http://any23.org/tmp/\"\n" +
                "}, {\n" +
                "  \"@graph\" : [ {\n" +
                "    \"@id\" : \"_:bn1\",\n" +
                "    \"http://pred/1\" : [ {\n" +
                "      \"@id\" : \"http://value/1\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"@id\" : \"http://graph/1\"\n" +
                "}, {\n" +
                "  \"@graph\" : [ {\n" +
                "    \"@id\" : \"http://sub/2\",\n" +
                "    \"http://pred/2\" : [ {\n" +
                "      \"@language\" : \"en\",\n" +
                "      \"@value\" : \"language literal\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"@id\" : \"http://graph/2\"\n" +
                "} ]";
        Assert.assertEquals(expected, baos.toString());

        baos.reset();
        writeContentSimple(new JSONLDWriter(baos));
        Assert.assertEquals(expected, baos.toString());
    }

    private void writeContentSimple(TripleWriter writer) throws TripleHandlerException {
        writer.writeTriple(SimpleValueFactory.getInstance().createBNode("bn1"),
                SimpleValueFactory.getInstance().createIRI("http://pred/1"),
                SimpleValueFactory.getInstance().createIRI("http://value/1"),
                SimpleValueFactory.getInstance().createIRI("http://graph/1"));

        writer.writeTriple(SimpleValueFactory.getInstance().createIRI("http://sub/2"),
                SimpleValueFactory.getInstance().createIRI("http://pred/2"),
                SimpleValueFactory.getInstance().createLiteral("language literal", "en"),
                SimpleValueFactory.getInstance().createIRI("http://graph/2"));

        writer.writeTriple(
                SimpleValueFactory.getInstance().createIRI("http://sub/3"),
                SimpleValueFactory.getInstance().createIRI("http://pred/3"),
                SimpleValueFactory.getInstance().createLiteral("123",
                        SimpleValueFactory.getInstance().createIRI("http://datatype")),
                writer instanceof JSONLDWriter ? SimpleValueFactory.getInstance().createIRI("http://any23.org/tmp/") : null);

        writer.close();

    }

    private void writeContentComplicated(TripleHandler writer) throws TripleHandlerException {
        //creating a fake document uri in order to write triples is terrible.
        //see improved solution in "writeContentSimple"!
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
        if (!(writer instanceof JSONLDWriter)) {
            writer.receiveTriple(
                    SimpleValueFactory.getInstance().createIRI("http://sub/3"),
                    SimpleValueFactory.getInstance().createIRI("http://pred/3"),
                    SimpleValueFactory.getInstance().createLiteral("123", SimpleValueFactory.getInstance().createIRI("http://datatype")),
                    null,
                    null
            );
        } else {
            //creating a fake extraction context in order to write triples is terrible.
            //see improved solution in "writeContentSimple"!
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
