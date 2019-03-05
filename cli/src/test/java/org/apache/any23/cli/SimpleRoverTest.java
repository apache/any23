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

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for issue ANY23-310
 *
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
@RunWith(Parameterized.class)
public class SimpleRoverTest extends ToolTestBase {
    
    private static final String baseUri = "urn:test";
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private String filePath;
    
    @Parameterized.Parameters
    public static Collection<String[]> litsFiles() throws Exception {
        return Arrays.asList(new String[][] {
            {"/org/apache/any23/extractor/yaml/simple-load.yml"},
            {"/org/apache/any23/extractor/csv/test-comma.csv"}
        });
    }
    

    public SimpleRoverTest(String filePath) {
        super(Rover.class);
        this.filePath = filePath;
    }

    /**
     * Ref <a href="https://issues.apache.org/jira/browse/ANY23-310">ANY23-310</a> unit test.
     * @throws Exception if there is an error asserting the test data.
     */
    @Test
    public void ref310Test()
            throws Exception {
        File outputFile = File.createTempFile("rover-test", ".ttl", tempDirectory);
        File logfile = File.createTempFile("test-log", ".txt", tempDirectory);

        int exitCode = runTool(String.format("-l %s -o %s -f turtle -e yaml,csv -d %s %s",
                logfile.getAbsolutePath(),
                outputFile.getAbsolutePath(),
                baseUri,
                copyResourceToTempFile(filePath).getAbsolutePath()));
        
        Assert.assertTrue(logfile.exists());
        Assert.assertTrue(outputFile.exists());
        // check if output file is longer than 10 chracters
        String outputFileContent = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
        Assert.assertTrue(outputFileContent.length() > 10);
        
        String[] logFileContent = FileUtils.readLines(logfile, StandardCharsets.UTF_8).toArray(new String[0]);
        Assert.assertEquals(2, logFileContent.length);
        //Assert.assertTrue(logFileContent[1].split("\\W*")[1] == );
        int contentSize = Integer.valueOf(logFileContent[1].split("\\t")[1]);
        String extractors = logFileContent[1].split("\\t")[4].replaceAll("[\\[\\]\\s:\\d]", "");

        if (log.isDebugEnabled()) {
            log.debug("Content: '{}'", contentSize);
            log.debug("Extractors: '{}'", extractors);
            log.debug("Log file location: {}", logfile.getAbsolutePath());
        }
        if (log.isTraceEnabled()) {
            log.trace("Log file content: \n{}\n", FileUtils.readFileToString(logfile, StandardCharsets.UTF_8));
        }

        Assert.assertTrue("Content size should be greated than 0", contentSize > 0);
        Assert.assertFalse(extractors.isEmpty());
        Assert.assertEquals("Unexpected exit code.", 0, exitCode);
    }
    
    /**
     * Ref <a href="https://issues.apache.org/jira/browse/ANY23-310">ANY23-310</a> unit test.
     * 
     * Example without the logging file.
     * 
     * By default that test is not active. It might be useful for debugging.
     * @throws Exception if there is an error asserting the test data.
     */
    @Test
    public void ref310ExtendedTest()
            throws Exception {
        File outputFile = File.createTempFile("rover-test", ".ttl", tempDirectory);

        int exitCode = runTool(String.format("-o %s -f turtle -e yaml,csv -d %s %s",
                outputFile.getAbsolutePath(),
                baseUri,
                copyResourceToTempFile(filePath).getAbsolutePath()));
        
        Assert.assertTrue(outputFile.exists());
        // check if output file is longer than 10 chracters
        String outputFileContent = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
        Assert.assertTrue(outputFileContent.length() > 10);
        
        
        Assert.assertEquals("Unexpected exit code.", 0, exitCode);
    }

}
