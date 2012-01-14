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
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link TripleHandler} that merely counts the number
 * of triples it has received.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CountingTripleHandler implements TripleHandler {

    private static final Logger logger = LoggerFactory.getLogger(CountingTripleHandler.class);

    private final boolean logTriples;

    private int count = 0;

    public CountingTripleHandler(boolean logTriples) {
        this.logTriples = logTriples;
    }

    public CountingTripleHandler() {
        this(false);
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {
        // ignore
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        // ignore
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        // ignore
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        count++;
        if(logTriples) logger.debug( String.format("%s %s %s %s %s\n", s, p, o, g, context) );
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
    throws TripleHandlerException {
        // ignore
    }

    public void close() throws TripleHandlerException {
        // ignore
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
        //ignore
    }

    public void setContentLength(long contentLength) {
        //ignore
    }
}
