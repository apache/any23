package org.deri.any23.cli;

import org.junit.Test;

/**
 * Test case for {@link VocabPrinter} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class VocabPrinterTest extends ToolTestBase {

    public VocabPrinterTest() {
        super(VocabPrinter.class);
    }

    @Test
    public void testRun() throws Exception {
        runToolCheckExit0();
    }

}
