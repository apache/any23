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

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Defines a document based triple handler.
 */
public interface TripleHandler {

    void startDocument(URI documentURI) throws TripleHandlerException;

    /**
     * Informs the handler that a new context has been established.
     * Contexts are not guaranteed to receive any triples, so they
     * might be closed without any triple.
     */
    void openContext(ExtractionContext context) throws TripleHandlerException;

    /**
     * Invoked with a currently open context,
     * notifies the detection of a triple.
     *
     * @param s triple subject, cannot be <code>null</code>.
     * @param p triple predicate, cannot be <code>null</code>.
     * @param o triple object, cannot be <code>null</code>.
     * @param g triple graph, can be <code>null</code>.
     * @param context extraction context.
     * @throws TripleHandlerException
     */
    void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context) throws TripleHandlerException;

    /**
     * Invoked with a currently open context, notifies the detection of a
     * namespace.
     *
     * @param prefix namespace prefix.
     * @param uri namespace <i>URI</i>.
     * @param context namespace context.
     * @throws TripleHandlerException
     */
    void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException;

    /**
     * Informs the handler that no more triples will come from a
     * previously opened context. All contexts are guaranteed to
     * be closed before the final close(). The document context
     * for each document is guaranteed to be closed after all
     * local contexts of that document.
     *
     * @param context the context to be closed.
     * @throws TripleHandlerException
     */
    void closeContext(ExtractionContext context) throws TripleHandlerException;

    /**
     * Informs the handler that the end of the document
     * has been reached.
     *
     * @param documentURI document URI.
     * @throws TripleHandlerException
     */
    void endDocument(URI documentURI) throws TripleHandlerException;

    /**
     * Sets the length of the content to be processed.
     *
     * @param contentLength
     * @throws TripleHandlerException
     */
    void setContentLength(long contentLength);

    /**
     * Will be called last and exactly once.
     * @throws TripleHandlerException
     */
    void close() throws TripleHandlerException;

}