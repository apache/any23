/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.filter;


import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.MockTripleHandler;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.writer.TripleHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;

/**
 * Test case for {@link org.deri.any23.filter.ExtractionContextBlocker}.
 */
public class ExtractionContextBlockerTest {

    private final static URI docURI = RDFHelper.uri("http://example.com/doc");
    private final static URI s = (URI) RDFHelper.toRDF("ex:s");
    private final static URI p = (URI) RDFHelper.toRDF("ex:p");
    private final static URI o = (URI) RDFHelper.toRDF("ex:o");
    private ExtractionContextBlocker blocker;
    private MockTripleHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new MockTripleHandler();
        blocker = new ExtractionContextBlocker(handler);
    }

    @Test
    public void testSendsNamespaceAfterUnblock() throws TripleHandlerException {
        handler.expectOpenContext("test", docURI, null);
        handler.expectNamespace("ex", "http://example.com/", "test", docURI, null);
        handler.expectTriple(s, p, o, null, "test", docURI, null);
        handler.expectCloseContext("test", docURI, null);
        handler.expectEndDocument(docURI);

        ExtractionContext context = new ExtractionContext("test", docURI);
        blocker.openContext(context);
        blocker.blockContext(context);
        blocker.receiveNamespace("ex", "http://example.com/", context);
        blocker.receiveTriple(s, p, o, null, context);
        blocker.closeContext(context);
        blocker.unblockContext(context);
        blocker.endDocument(docURI);
        handler.verify();
    }

}
