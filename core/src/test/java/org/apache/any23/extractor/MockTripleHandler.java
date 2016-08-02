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

package org.apache.any23.extractor;

import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.TripleHandler;
import org.junit.Assert;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Just a mockup implementing a {@link org.apache.any23.writer.TripleHandler}.
 * Used only for test purposes. 
 *
 */
//TODO: replace with Mockito
public class MockTripleHandler implements TripleHandler {

    private final List<String> expectations = new LinkedList<String>();

    public void expectStartDocument(IRI documentIRI) {
        expectations.add("startDocument(" + documentIRI + ")");
    }

    public void expectEndDocument(IRI documentIRI) {
        expectations.add("endDocument(" + documentIRI + ")");
    }

    public void expectSetContentLength(long contentLength) {
        expectations.add("setContentLength(" + contentLength + ")");
    }

    public void expectClose() {
        expectations.add("close()");
    }

    public void expectOpenContext(String extractorName, IRI documentIRI, String localID) {
        expectations.add("openContext(" + new ExtractionContext(extractorName, documentIRI, localID) + ")");
    }

    public void expectCloseContext(String extractorName, IRI documentIRI, String localID) {
        expectations.add("closeContext(" + new ExtractionContext(extractorName, documentIRI, localID) + ")");
    }

    public void expectTriple(Resource s, IRI p, Value o, IRI g, String extractorName, IRI documentIRI, String localID) {
        expectations.add("triple(" + RDFUtils.quad(s, p, o, g) + ", " +
                new ExtractionContext(extractorName, documentIRI, localID) + ")");
    }

    public void expectNamespace(String prefix, String uri, String extractorName, IRI documentIRI, String localID) {
        expectations.add("namespace(" + prefix + ", " + uri + ", " +
                new ExtractionContext(extractorName, documentIRI, localID) + ")");
    }

    public void verify() {
        if (!expectations.isEmpty()) {
            Assert.fail("Expected " + expectations.size() +
                    " more invocation(s), first: " + expectations.get(0));
        }
    }

    public void startDocument(IRI documentIRI) {
        assertNextExpectation("startDocument(" + documentIRI + ")");
    }

    public void endDocument(IRI documentIRI) {
        assertNextExpectation("endDocument(" + documentIRI + ")");
    }

    public void openContext(ExtractionContext context) {
        assertNextExpectation("openContext(" + context + ")");
    }

    public void closeContext(ExtractionContext context) {
        assertNextExpectation("closeContext(" + context + ")");
    }

    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context) {
        assertNextExpectation("triple(" + RDFUtils.quad(s, p, o, g) + ", " + context + ")");
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        assertNextExpectation("namespace(" + prefix + ", " + uri + ", " + context + ")");
    }

    public void close() {
        assertNextExpectation("close()");
    }

    public void setContentLength(long contentLength) {
        assertNextExpectation("setContentLength(" + contentLength + ")");
    }

    private void assertNextExpectation(String invocation) {
        if (expectations.isEmpty()) {
            Assert.fail("Next expectation was <null>, invocation was " + invocation);
        }
        String expectation = expectations.remove(0);
        Assert.assertEquals("Invocation doesn't match expectation", expectation, invocation);
    }

}
