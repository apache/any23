package org.deri.any23.cli;

import org.junit.Test;

/**
 * Test case for {@link PluginVerifier} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class PluginVerifierTest extends ToolTestBase {

    public PluginVerifierTest() {
        super(PluginVerifier.class);
    }

    @Test
    public void testRun() throws Exception {
        runToolCheckExit0(".");
    }

}
