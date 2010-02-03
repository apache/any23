package org.deri.any23;

import junit.framework.Assert;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.extractor.example.ExampleExtractor;
import org.deri.any23.writer.CountingTripleHandler;
import org.junit.Test;
import org.openrdf.model.URI;

/**
 * Tests the <i>extraction</i> scenario.
 */
//TODO MED - Move under extraction package.
public class ExtractionAPITest {

    private static final String exampleDoc = "http://example.com/";
    private static final URI uri           = Helper.uri(exampleDoc);

    @Test
    public void testDirectInstantiation() throws Exception {
        CountingTripleHandler out   = new CountingTripleHandler();
        ExampleExtractor extractor  = new ExampleExtractor();
        ExtractionResultImpl writer = new ExtractionResultImpl(uri, extractor, out);
        extractor.run(uri, uri, writer);
        writer.close();
        Assert.assertEquals(1, out.getCount());
    }
    
}
