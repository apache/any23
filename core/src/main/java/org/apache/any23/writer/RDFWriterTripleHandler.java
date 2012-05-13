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

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.rdf.RDFUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

/**
 * A {@link TripleHandler} that writes
 * triples to a Sesame {@link org.openrdf.rio.RDFWriter},
 * eg for serialization using one of Sesame's writers.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class RDFWriterTripleHandler implements FormatWriter, TripleHandler {

    private final RDFWriter writer;

    private boolean closed = false;

    /**
     * The annotation flag.
     */
    private boolean annotated = false;

    RDFWriterTripleHandler(RDFWriter destination) {
        writer = destination;
        try {
            writer.startRDF();
        } catch (RDFHandlerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If <code>true</code> then the produced <b>RDF</b> is annotated with
     * the extractors used to generate the specific statements.
     *
     * @return the annotation flag value.
     */
    @Override
    public boolean isAnnotated() {
        return annotated;
    }

    /**
     * Sets the <i>annotation</i> flag.
     *
     * @param f If <code>true</code> then the produced <b>RDF</b> is annotated with
     *          the extractors used to generate the specific statements.
     */
    @Override
    public void setAnnotated(boolean f) {
        annotated = f;
    }

    @Override
    public void startDocument(URI documentURI) throws TripleHandlerException {
        handleComment("OUTPUT FORMAT: " + writer.getRDFFormat());
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        handleComment("BEGIN: " + context );
    }

    @Override
    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        final URI graph = g == null ? context.getDocumentURI() : g;
        try {
            writer.handleStatement(
                    RDFUtils.quad(s, p, o, graph));
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(
                    String.format("Error while receiving triple: %s %s %s %s", s, p, o, graph),
                    ex
            );
        }
    }

    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
    throws TripleHandlerException {
        try {
            writer.handleNamespace(prefix, uri);
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(String.format("Error while receiving namespace: %s:%s", prefix, uri),
                    ex
            );
        }
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        handleComment( "END: " + context );
    }

    @Override
    public void close() throws TripleHandlerException {
        if (closed) return;
        closed = true;
        try {
            writer.endRDF();
        } catch (RDFHandlerException e) {
            throw new TripleHandlerException("Error while closing the triple handler.", e);
        }
    }

    @Override
    public void endDocument(URI documentURI) throws TripleHandlerException {
        // Empty.
    }

    @Override
    public void setContentLength(long contentLength) {
        // Empty.
    }

    private void handleComment(String comment) throws TripleHandlerException {
        if( !annotated ) return;
        try {
            writer.handleComment(comment);
        } catch (RDFHandlerException rdfhe) {
            throw new TripleHandlerException("Error while handing comment.", rdfhe);
        }
    }

}
