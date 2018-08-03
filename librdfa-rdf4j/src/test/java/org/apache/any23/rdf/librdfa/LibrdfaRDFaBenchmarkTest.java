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
package org.apache.any23.rdf.librdfa;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.any23.rdf.rdfa.LibrdfaRDFaParser;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.semarglproject.rdf4j.rdf.rdfa.RDF4JRDFaParser;

/**
 *
 * @author Julio Caguano
 */
@BenchmarkOptions(callgc = false, benchmarkRounds = 20, warmupRounds = 0)
public class LibrdfaRDFaBenchmarkTest extends AbstractBenchmark {

    private final int ITERATIONS = 2000;
    private String DOCUMENT = "";

    @Before
    public void init() {
        DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n xmlns:dc=\"http://purl.org/dc/elements/1.1/\" lang=\"en\">\n"
                + "<head><title>Speed Test</title></head><body><p>";
        for (int i = 0; i < ITERATIONS; i++) {
            DOCUMENT += "<span about=\"#foo\" rel=\"dc:title\" resource=\"#you\" />";
        }
        DOCUMENT += "</p></body></html>";
    }

    @Test
    public void testSemargl() throws Exception {
        runTest(new RDF4JRDFaParser());
    }

    @Test
    public void testLibrdfa() throws IOException {
        runTest(new LibrdfaRDFaParser());
    }

    private void runTest(RDFParser parser) throws IOException {
        InputStream in = new ByteArrayInputStream(DOCUMENT.getBytes(StandardCharsets.UTF_8));
        StatementCollector sc = new StatementCollector();
        parser.setRDFHandler(sc);
        parser.parse(in, "http://example.org/");
        assertEquals(ITERATIONS, sc.getStatements().size());
    }
}
