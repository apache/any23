package org.deri.any23.filter;


import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.MockTripleHandler;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;

public class ExtractionContextBlockerTest {
    private final static URI docURI = Helper.uri("http://example.com/doc");
    private final static URI s = (URI) Helper.toRDF("ex:s");
    private final static URI p = (URI) Helper.toRDF("ex:p");
    private final static URI o = (URI) Helper.toRDF("ex:o");
    private ExtractionContextBlocker blocker;
    private MockTripleHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new MockTripleHandler();
        blocker = new ExtractionContextBlocker(handler);
    }

    @Test
    public void testSendsNamespaceAfterUnblock() {
        handler.expectOpenContext("test", docURI, null);
        handler.expectNamespace("ex", "http://example.com/", "test", docURI, null);
        handler.expectTriple(s, p, o, "test", docURI, null);
        handler.expectCloseContext("test", docURI, null);
        handler.expectEndDocument(docURI);

        ExtractionContext context = new ExtractionContext("test", docURI);
        blocker.openContext(context);
        blocker.blockContext(context);
        blocker.receiveNamespace("ex", "http://example.com/", context);
        blocker.receiveTriple(s, p, o, context);
        blocker.closeContext(context);
        blocker.unblockContext(context);
        blocker.endDocument(docURI);
        handler.verify();
    }
}
