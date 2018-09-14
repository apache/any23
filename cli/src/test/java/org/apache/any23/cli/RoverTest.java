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

package org.apache.any23.cli;

import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.util.FileUtils;
import org.apache.any23.util.StringUtils;
import org.apache.any23.util.URLUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.File;

/**
 * Test case for {@link Rover}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RoverTest extends ToolTestBase {

    private static final String[] TARGET_FILES = {
        "/microdata/microdata-nested.html",
        "/org/apache/any23/extractor/csv/test-semicolon.csv"
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
        
        String[] copiedTargets = new String[TARGET_FILES.length];
        for(int i = 0; i < TARGET_FILES.length; i++)
        {
            File tempFile = copyResourceToTempFile(TARGET_FILES[i]);
            
            copiedTargets[i] = tempFile.getAbsolutePath();
        }
        
        runWithMultiSourcesAndVerify(copiedTargets, 0);
    }

    @Test
    public void testRunWithDefaultNS() throws Exception {
        final String DEFAULT_GRAPH = "http://test/default/ns";
        final File outFile = File.createTempFile("rover-test", "out", tempDirectory);
        final int exitCode = runTool(
                String.format(
                        "-o %s -f nquads -p -n %s -d %s",
                        outFile.getAbsolutePath(),
                        copyResourceToTempFile("/cli/rover-test1.nq").getAbsolutePath(),
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

    @Test
    public void testDelegatingWriterFactory() throws Exception {
        final File outFile = File.createTempFile("rover-test", "out", tempDirectory);
        final String DEFAULT_GRAPH = "http://test/default/ns";
        final String stylesheet = "http://www.w3.org/1999/xhtml/vocab#stylesheet";

        Assert.assertEquals("Unexpected exit code.", 0, runTool(
                String.format(
                        "-o %s -f nquads %s -d %s",
                        outFile.getAbsolutePath(),
                        copyResourceToTempFile("/cli/basic-with-stylesheet.html").getAbsolutePath(),
                        DEFAULT_GRAPH
                )
        ));

        String content = FileUtils.readFileContent(outFile);

        Assert.assertTrue(content.contains(stylesheet));

        final int lineCountWithStylesheet = content.split("\\n").length;

        Assert.assertEquals("Unexpected exit code.", 0, runTool(
                String.format(
                        "-o %s -f notrivial,nquads %s -d %s",
                        outFile.getAbsolutePath(),
                        copyResourceToTempFile("/cli/basic-with-stylesheet.html").getAbsolutePath(),
                        DEFAULT_GRAPH
                )
        ));

        content = FileUtils.readFileContent(outFile);

        Assert.assertTrue(!content.contains(stylesheet));

        final int lineCountWithoutStylesheet = content.split("\\n").length;

        Assert.assertEquals(lineCountWithStylesheet - 1, lineCountWithoutStylesheet);
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
        final File outFile = File.createTempFile("rover-test", "out", tempDirectory);
        final File logFile = File.createTempFile("rover-test", "log", tempDirectory);

        final int exitCode = runTool(
                String.format(
                        "-o %s -f nquads -l %s -p -n %s",
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
        final Statement[] statements = RDFUtils.parseRDF(RDFFormat.NQUADS, outNQuads);
        Assert.assertTrue("Unexpected number of statements.", statements.length > 9);
    }

}
