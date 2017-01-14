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

package org.apache.any23.filter;


import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.MockTripleHandler;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.TripleHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;

/**
 * Test case for {@link ExtractionContextBlocker}.
 */
public class ExtractionContextBlockerTest {

    private final static IRI docIRI = RDFUtils.iri("http://example.com/doc");
    private final static IRI s = (IRI) RDFUtils.toValue("ex:s");
    private final static IRI p = (IRI) RDFUtils.toValue("ex:p");
    private final static IRI o = (IRI) RDFUtils.toValue("ex:o");
    private ExtractionContextBlocker blocker;
    private MockTripleHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new MockTripleHandler();
        blocker = new ExtractionContextBlocker(handler);
    }

    @Test
    public void testSendsNamespaceAfterUnblock() throws TripleHandlerException {
        handler.expectOpenContext("test", docIRI, null);
        handler.expectNamespace("ex", "http://example.com/", "test", docIRI, null);
        handler.expectTriple(s, p, o, null, "test", docIRI, null);
        handler.expectCloseContext("test", docIRI, null);
        handler.expectEndDocument(docIRI);

        ExtractionContext context = new ExtractionContext("test", docIRI);
        blocker.openContext(context);
        blocker.blockContext(context);
        blocker.receiveNamespace("ex", "http://example.com/", context);
        blocker.receiveTriple(s, p, o, null, context);
        blocker.closeContext(context);
        blocker.unblockContext(context);
        blocker.endDocument(docIRI);
        handler.verify();
    }

}
