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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import org.apache.any23.extractor.ExtractionContext;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

/**
 * Implementation of <i>JSON</i> format writer.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONWriter implements FormatWriter {

    private JsonGenerator ps;
    private boolean documentStarted = false;

    public JSONWriter(OutputStream os) {
        if (os == null) {
            throw new NullPointerException("Output stream cannot be null.");
        }
        JsonFactory factory = new JsonFactory();
        try {
            this.ps = factory.createGenerator(os)
                    .setPrettyPrinter(new DefaultPrettyPrinter());
        } catch (IOException ex) {
        }
    }

    @Override
    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        if (documentStarted) {
            throw new IllegalStateException("Document already started.");
        }
        documentStarted = true;

        try {
            ps.writeStartObject();
            ps.writeFieldName("quads");
            ps.writeStartArray();
        } catch (IOException ex) {
            throw new TripleHandlerException("IO Error while starting document.", ex);
        }
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        // Empty.
    }

    @Override
    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
            throws TripleHandlerException {
        validateDocumentStarted();
        try {
            ps.writeStartArray();

            if (s instanceof IRI) {
                printExplicitIRI(s.stringValue());
            } else {
                printBNode(s.stringValue());
            }

            printIRI(p.stringValue());

            if (o instanceof IRI) {
                printExplicitIRI(o.stringValue());
            } else if (o instanceof BNode) {
                printBNode(o.stringValue());
            } else {
                printLiteral((Literal) o);
            }

            printIRI(g == null ? null : g.stringValue());

            ps.writeEndArray();
        } catch (IOException ex) {
            throw new TripleHandlerException("IO Error while writing triple", ex);
        }
    }

    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
            throws TripleHandlerException {
        // Empty.
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        // Empty.
    }

    @Override
    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        validateDocumentStarted();

        try {
            ps.writeEndArray();
            ps.writeEndObject();
            documentStarted = false;
        } catch (IOException ex) {
            throw new TripleHandlerException("IO Error while closing document.", ex);
        }
    }

    @Override
    public void setContentLength(long contentLength) {
        // Empty.
    }

    @Override
    public void close() throws TripleHandlerException {
        if (documentStarted) {
            endDocument(null);
        }
        try {
            ps.close();
        } catch (IOException ex) {
            throw new TripleHandlerException("IO Error while closing stream.", ex);
        }
    }

    private void validateDocumentStarted() {
        if (!documentStarted) {
            throw new IllegalStateException("Document didn't start.");
        }
    }

    private void printIRI(String uri) throws IOException {
        printValue(uri);
    }

    private void printExplicitIRI(String uri) throws IOException {
        printValue("uri", uri);
    }

    private void printBNode(String bnode) throws IOException {
        printValue("bnode", bnode);
    }

    private void printLiteral(Literal literal) throws IOException {
        ps.writeStartObject();
        ps.writeStringField("type", "literal");
        ps.writeStringField("value", literal.stringValue());

        final Optional<String> language = literal.getLanguage();
        ps.writeStringField("lang", language.isPresent() ? literal.getLanguage().get() : null);

        final IRI datatype = literal.getDatatype();
        ps.writeStringField("datatype", datatype != null ? datatype.stringValue() : null);
        ps.writeEndObject();
    }

    private void printValue(String type, String value) throws IOException {
        ps.writeString(value);
    }

    private void printValue(String value) throws IOException {
        ps.writeString(value);
    }

    @Override
    public boolean isAnnotated() {
        return false; // TODO: add annotation support.
    }

    @Override
    public void setAnnotated(boolean f) {
        // Empty.
    }
}
