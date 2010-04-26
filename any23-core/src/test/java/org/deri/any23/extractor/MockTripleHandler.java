/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor;

import org.deri.any23.util.RDFHelper;
import org.deri.any23.writer.TripleHandler;
import org.junit.Assert;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Just a mockup implementing a {@link org.deri.any23.writer.TripleHandler}.
 * Used only for test purposes. 
 *
 */
public class MockTripleHandler implements TripleHandler {

    private final List<String> expectations = new LinkedList<String>();

    public void expectStartDocument(URI documentURI) {
        expectations.add("startDocument(" + documentURI + ")");
    }

    public void expectEndDocument(URI documentURI) {
        expectations.add("endDocument(" + documentURI + ")");
    }

    public void expectSetContentLength(long contentLength) {
        expectations.add("setContentLength(" + contentLength + ")");
    }

    public void expectClose() {
        expectations.add("close()");
    }

    public void expectOpenContext(String extractorName, URI documentURI, String localID) {
        expectations.add("openContext(" + new ExtractionContext(extractorName, documentURI, localID) + ")");
    }

    public void expectCloseContext(String extractorName, URI documentURI, String localID) {
        expectations.add("closeContext(" + new ExtractionContext(extractorName, documentURI, localID) + ")");
    }

    public void expectTriple(Resource s, URI p, Value o, URI g, String extractorName, URI documentURI, String localID) {
        expectations.add("triple(" + RDFHelper.quad(s, p, o, g) + ", " +
                new ExtractionContext(extractorName, documentURI, localID) + ")");
    }

    public void expectNamespace(String prefix, String uri, String extractorName, URI documentURI, String localID) {
        expectations.add("namespace(" + prefix + ", " + uri + ", " +
                new ExtractionContext(extractorName, documentURI, localID) + ")");
    }

    public void verify() {
        if (!expectations.isEmpty()) {
            Assert.fail("Expected " + expectations.size() +
                    " more invocation(s), first: " + expectations.get(0));
        }
    }

    public void startDocument(URI documentURI) {
        assertNextExpectation("startDocument(" + documentURI + ")");
    }

    public void endDocument(URI documentURI) {
        assertNextExpectation("endDocument(" + documentURI + ")");
    }

    public void openContext(ExtractionContext context) {
        assertNextExpectation("openContext(" + context + ")");
    }

    public void closeContext(ExtractionContext context) {
        assertNextExpectation("closeContext(" + context + ")");
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context) {
        assertNextExpectation("triple(" + RDFHelper.quad(s, p, o, g) + ", " + context + ")");
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
