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

package org.deri.any23.cli;

import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.util.FileUtils;
import org.deri.any23.util.StringUtils;
import org.deri.any23.util.URLUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.openrdf.model.Statement;

import java.io.File;

/**
 * Test case for {@link Rover}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RoverTest extends ToolTestBase {

    private static final String[] TARGET_FILES = {
        "src/test/resources/microdata/microdata-nested.html",
        "src/test/resources/org/deri/any23/extractor/csv/test-semicolon.csv"
    };

    private static final String[] TARGET_URLS = {
            "http://twitter.com/micmos",
            "http://twitter.com/dpalmisano"
    };

    public RoverTest() {
        super(Rover.class);
    }

    @Test
    public void testRunMultiFiles() throws Exception {
        runWithMultiSourcesAndVerify(TARGET_FILES, 0);
    }

    @Test
    public void testRunWithDefaultNS() throws Exception {
        final String DEFAULT_GRAPH = "http://test/default/ns";
        final File outFile = File.createTempFile("rover-test", "out");
        final int exitCode = runTool(
                String.format(
                        "-v -o %s -f nquads -p -n %s -d %s",
                        outFile.getAbsolutePath(),
                        "src/test/resources/cli/rover-test1.nq",
                        DEFAULT_GRAPH
                )
        );

        Assert.assertEquals("Unexpected exit code.", 0, exitCode);
        Assert.assertTrue(outFile.exists());
        final String fileContent = FileUtils.readFileContent(outFile);
        final String[] lines = fileContent.split("\\n");
        int graphCounter = 0;
        for(String line : lines) {
            if(line.contains(DEFAULT_GRAPH)) {
                graphCounter++;
            }
        }
        Assert.assertEquals(0, graphCounter);
    }

    /* BEGIN: online tests. */

    @Test
    public void testRunMultiURLs() throws Exception {
        // Assuming first accessibility to remote resources.
        assumeOnlineAllowed();
        for(String targetURL : TARGET_URLS) {
            Assume.assumeTrue( URLUtils.isOnline(targetURL) );
        }

        runWithMultiSourcesAndVerify(TARGET_URLS, 0);
    }

    private void runWithMultiSourcesAndVerify(String[] targets, int expectedExit) throws Exception {
        final File outFile = File.createTempFile("rover-test", "out");
        final File logFile = File.createTempFile("rover-test", "log");
        outFile.delete();
        outFile.delete();

        final int exitCode = runTool(
                String.format(
                        "-v -o %s -f nquads -l %s -p -n %s",
                        outFile.getAbsolutePath(),
                        logFile.getAbsolutePath(),
                        StringUtils.join(" ", targets)
                )
        );
        Assert.assertEquals("Unexpected exit code.", expectedExit, exitCode);

        Assert.assertTrue(outFile.exists());
        Assert.assertTrue(logFile.exists());

        final String logFileContent = FileUtils.readFileContent(logFile);
        Assert.assertEquals(
                "Unexpected number of log lines.",
                targets.length + 1,  // Header line.
                StringUtils.countNL(logFileContent)
        );

        final String outNQuads = FileUtils.readFileContent(outFile);
        final Statement[] statements = RDFUtils.parseRDF(RDFUtils.Parser.NQuads, outNQuads);
        Assert.assertTrue("Unexpected number of statements.", statements.length > 10);
    }

}
