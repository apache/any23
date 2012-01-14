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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Triple handler decorator useful for logging purposes.
 */
public class LoggingTripleHandler implements TripleHandler {

    /**
     * Decorated.
     */
    private final TripleHandler underlyingHandler;

    private final Map<String, Integer> contextTripleMap = new HashMap<String, Integer>();
    private long startTime     = 0;
    private long contentLength = 0;
    private final PrintWriter destination;

    public LoggingTripleHandler(TripleHandler tripleHandler, PrintWriter destination) {
        if(tripleHandler == null) {
            throw new NullPointerException("tripleHandler cannot be null.");
        }
        if(destination == null) {
            throw new NullPointerException("destination cannot be null.");
        }
        underlyingHandler = tripleHandler;
        this.destination = destination;

        printHeader(destination);
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {
        underlyingHandler.startDocument(documentURI);
        startTime = System.currentTimeMillis();
    }

    public void close() throws TripleHandlerException {
        underlyingHandler.close();
        destination.flush();
        destination.close();
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        underlyingHandler.closeContext(context);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        underlyingHandler.openContext(context);
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        underlyingHandler.receiveTriple(s, p, o, g, context);
        Integer i = contextTripleMap.get(context.getExtractorName());
        if (i == null) i = 0;
        contextTripleMap.put(context.getExtractorName(), (i + 1));
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
    throws TripleHandlerException {
        underlyingHandler.receiveNamespace(prefix, uri, context);
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
        underlyingHandler.endDocument(documentURI);
        long elapsedTime = System.currentTimeMillis() - startTime;
        boolean success = true;
        StringBuffer sb = new StringBuffer("[");
        for (Entry<String, Integer> ent : contextTripleMap.entrySet()) {
            sb.append(" ").append(ent.getKey()).append(":").append(ent.getValue());
            if (ent.getValue() > 0) {
                success = true;
            }
        }
        sb.append("]");
        destination.println(
                documentURI + "\t" + contentLength + "\t" + elapsedTime + "\t" + success + "\t" + sb.toString()
        );
        contextTripleMap.clear();
    }

    public void setContentLength(long contentLength) {
        underlyingHandler.setContentLength(contentLength);
        this.contentLength = contentLength;
    }

    private void printHeader(PrintWriter writer) {
        writer.println("# Document-URI\tContent-Length\tElapsed-Time\tSuccess\tExtractors");
    }
}