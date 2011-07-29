package org.deri.any23.cli;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link ExtractorDocumentation} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractorDocumentationTest extends ToolTestBase {

    private static final String TARGET_EXTRACTOR = "html-microdata";

    public ExtractorDocumentationTest() {
        super(ExtractorDocumentation.class);
    }

    @Test
    public void tesList() throws Exception {
        runToolCheckExit0("-list");
    }

    @Test
    public void testAll() throws Exception {
        runToolCheckExit0("-all");
    }

    @Ignore("no available example")
    @Test
    public void testExampleInput() throws Exception {
        runToolCheckExit0("-i", TARGET_EXTRACTOR);
    }

    @Ignore("no available example")
    @Test
    public void testExampleOutput() throws Exception {
        runToolCheckExit0("-o", TARGET_EXTRACTOR);
    }

}
