package org.deri.any23.cli;

import org.junit.Test;

/**
 * Test case for {@link Version} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class VersionTest extends ToolTestBase {

    public VersionTest() {
        super(Version.class);
    }

    @Test
    public void testRun() throws Exception {
        runToolCheckExit0();
    }

}
