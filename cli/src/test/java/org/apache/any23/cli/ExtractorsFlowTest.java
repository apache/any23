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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModelFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.function.Function;

/**
 * This is example for task ANY23-396
 *
 * @author Jacek Grzebyta (jgrzebyta@apache.org)
 */
public class ExtractorsFlowTest extends ToolTestBase {

    public ExtractorsFlowTest() {
        super(Rover.class);
    }

    private static final String testingDatafile = "/org/apache/any23/extractor/csv/test-comma.csv";
    private static final ValueFactory vf = SimpleValueFactory.getInstance();
    private Logger log = LoggerFactory.getLogger(getClass());

    /*
     Domain ontology & data model
     */
    public static final String NAMESPACE = "http://supercustom.net/ontology/";
    public static final IRI PERSON = vf.createIRI(NAMESPACE, "Person");
    public static final IRI FULL_NAME = vf.createIRI(NAMESPACE, "fullName");
    public static final IRI HASH = vf.createIRI(NAMESPACE, "hash");

    public static final String DATA_NAMESPACE = "http://rdf.supercustom.net/data/";

    // domain ontology person IRI factory
    public static Function<String, IRI> personIRIFactory = (String s) -> {
            return vf.createIRI(DATA_NAMESPACE, DigestUtils.sha1Hex(s));
    };



    /**
     * Emulates action described in description of issue ANY23-396.
     * @throws Exception
     */
    @Test
    public void runTestFor396() throws Exception {
        File outputFile = File.createTempFile("mockdata-", ".ttl", tempDirectory);
        File logFile = File.createTempFile("log-exec-", ".txt", tempDirectory);

        runTool(String.format("-l %s --workflow -o %s -f turtle -e csv,people -d %s %s",
                logFile.getAbsolutePath(),
                outputFile.getAbsolutePath(),
                "urn:dataser:raw/",
                copyResourceToTempFile(testingDatafile).getAbsolutePath()));

        // create some statement of expected model
        Model expected = new TreeModelFactory().createEmptyModel();
        String[] fullNames = new String[] {"Davide Palmisano", "Michele Mostarda", "Giovanni Tummarello"};

        // populate expected model
        Arrays.asList(fullNames).stream().forEach( fullN -> {
            IRI person = personIRIFactory.apply(fullN);
            expected.add(person, RDF.TYPE, PERSON);
            expected.add(person, FULL_NAME, vf.createLiteral(fullN));
            expected.add(person, HASH, vf.createLiteral(DigestUtils.sha1Hex(fullN), XMLSchema.HEXBINARY));
        });

        log.info("\n\nlog file content:\n{}", FileUtils.readFileToString(logFile, "utf-8"));
        log.info("\n\nData file: \n{}", FileUtils.readFileToString(outputFile, "utf-8"));

        Assert.assertTrue(assertCompareModels(expected, outputFile));
    }



    /**
     * Compare expected model and received from input File.
     * @param expected
     * @param received
     * @return
     */
    public boolean assertCompareModels(Model expected, File received) throws Exception {
        Model receivedModel = new TreeModelFactory().createEmptyModel();
        receivedModel.addAll(Arrays.asList(RDFUtils.parseRDF(
                Rio.getParserFormatForFileName(received.getName()).get(),
                new BufferedInputStream(new FileInputStream(received)),
                received.toURI().toString()
        )));

        return receivedModel.containsAll(expected);
    }

}
