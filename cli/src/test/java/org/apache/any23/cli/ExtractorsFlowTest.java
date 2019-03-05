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

import org.apache.any23.cli.flows.PeopleExtractor;
import org.apache.any23.rdf.RDFUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This is example for task ANY23-396
 *
 * @author Jacek Grzebyta (jgrzebyta@apache.org)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class ExtractorsFlowTest extends ToolTestBase {

    private static final String testingDatafile = "/org/apache/any23/extractor/csv/test-comma.csv";
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ExtractorsFlowTest() {
        super(Rover.class);
    }

    /**
     * Emulates action described in ANY23-396.
     * @throws Exception if there is an error asserting the test data.
     */
    @Test
    public void runTestFor396() throws Exception {
        File outputFile = File.createTempFile("mockdata-", ".ttl", tempDirectory);
        File logFile = File.createTempFile("log-exec-", ".txt", tempDirectory);

        runTool(String.format("-l %s -o %s -f people,turtle -e csv -d %s %s",
                logFile.getAbsolutePath(),
                outputFile.getAbsolutePath(),
                PeopleExtractor.RAW_NS,
                copyResourceToTempFile(testingDatafile).getAbsolutePath()));

        // populate expected model
        Model expected = new TreeModel();
        Stream.of("Davide Palmisano", "Michele Mostarda", "Giovanni Tummarello")
                .map(PeopleExtractor::createPerson).forEach(expected::addAll);

        if (log.isDebugEnabled()) {
            log.debug("\n\nlog file content:\n{}", FileUtils.readFileToString(logFile, "utf-8"));
            log.debug("\n\nData file: \n{}", FileUtils.readFileToString(outputFile, "utf-8"));
        }

        Assert.assertTrue(assertCompareModels(expected, outputFile));
    }

    /**
     * Compare expected model and received from input File.
     * @throws Exception if there is an error asserting the test data.
     */
    private boolean assertCompareModels(Model expected, File received) throws Exception {
        Model receivedModel = new TreeModel();
        receivedModel.addAll(Arrays.asList(RDFUtils.parseRDF(
                Rio.getParserFormatForFileName(received.getName()).orElseThrow(AssertionError::new),
                new BufferedInputStream(new FileInputStream(received)),
                received.toURI().toString()
        )));

        return receivedModel.containsAll(expected);
    }
}
