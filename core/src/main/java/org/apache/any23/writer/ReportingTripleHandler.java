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
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link TripleHandler} that collects
 * various information about the extraction process, such as
 * the extractors used and the total number of triples.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ReportingTripleHandler implements TripleHandler {

    private final TripleHandler wrapped;

    private final Collection<String> extractorNames = new HashSet<String>();
    private AtomicInteger totalTriples   = new AtomicInteger(0);
    private AtomicInteger totalDocuments = new AtomicInteger(0);

    public ReportingTripleHandler(TripleHandler wrapped) {
        if(wrapped == null) {
            throw new NullPointerException("wrapped cannot be null.");
        }
        this.wrapped = wrapped;
    }

    public Collection<String> getExtractorNames() {
        return extractorNames;
    }

    public int getTotalTriples() {
        return totalTriples.get();
    }

    public int getTotalDocuments() {
        return totalDocuments.get();
    }

    /**
     * @return a human readable report.
     */
    public String printReport() {
        return String.format("Total Documents: %d, Total Triples: %d", getTotalDocuments(), getTotalTriples());
    }

    public void startDocument(IRI documentIRI) throws TripleHandlerException {
        totalDocuments.incrementAndGet();
        wrapped.startDocument(documentIRI);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        wrapped.openContext(context);
    }

    public void receiveNamespace(
            String prefix,
            String uri,
            ExtractionContext context
    ) throws TripleHandlerException {
        wrapped.receiveNamespace(prefix, uri, context);
    }

    public void receiveTriple(
            Resource s,
            IRI p,
            Value o,
            IRI g,
            ExtractionContext context
    ) throws TripleHandlerException {
        extractorNames.add(context.getExtractorName());
        totalTriples.incrementAndGet();
        wrapped.receiveTriple(s, p, o, g, context);
    }

    public void setContentLength(long contentLength) {
        wrapped.setContentLength(contentLength);
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        wrapped.closeContext(context);
    }

    public void endDocument(IRI documentIRI) throws TripleHandlerException {
        wrapped.endDocument(documentIRI);
    }

    public void close() throws TripleHandlerException {
        wrapped.close();
    }

}