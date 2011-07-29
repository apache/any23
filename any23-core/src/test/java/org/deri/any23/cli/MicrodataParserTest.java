package org.deri.any23.cli;

import org.junit.Test;

/**
 * Test case for {@link MicrodataParser} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParserTest extends ToolTestBase {

    public MicrodataParserTest() {
        super(MicrodataParser.class);
    }

    @Test
    public void testRun() throws Exception {
        runToolCheckExit0("file:src/test/resources/microdata/microdata-nested.html");
    }

}
