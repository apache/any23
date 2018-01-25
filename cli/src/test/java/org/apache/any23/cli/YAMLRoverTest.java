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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.util.Charsets;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for issue ANY23-308
 *
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YAMLRoverTest extends ToolTestBase {

    private static final String file1 = "/org/apache/any23/extractor/yaml/simple-load.yml";

    private static final String baseUri = "urn:test";

    private final Logger log = LoggerFactory.getLogger(getClass());

    public YAMLRoverTest() {
        super(Rover.class);
    }

    @Test
    public void simpleTest()
            throws Exception {
        File outputFile = File.createTempFile("rover-test", ".ttl", tempDirectory);
        File logfile = File.createTempFile("test-log", ".txt", tempDirectory);

        int exitCode = runTool(String.format("-l %s -o %s -f turtle -e yaml,csv -d %s %s",
                logfile.getAbsolutePath(),
                outputFile.getAbsolutePath(),
                baseUri,
                copyResourceToTempFile(file1).getAbsolutePath()));

        Assert.assertTrue(logfile.exists());
        log.debug("Log file location: {}", logfile.getAbsolutePath());
        log.info("Log file content: \n{}\n", Files.toString(logfile, Charsets.UTF_8));

        Assert.assertEquals("Unexpected exit code.", 0, exitCode);
        assertFileContainsString(outputFile, baseUri);
    }

    /**
     * Asserts if file contains wanted string.
     * 
     * If logging level is <tt>trace</tt> than additionally displays file content.
     * 
     * @param f
     * @param s Expected string in the file
     * @return
     */
    public void assertFileContainsString(File f, String s) throws IOException {
        String fileContent = Files.toString(f, Charsets.UTF_8);
        log.trace("File content: \n{}\n", fileContent);
        Assert.assertTrue(fileContent.contains(s));
    }

}
