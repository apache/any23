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
package org.apache.any23.extractor.yaml;

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.html.AbstractExtractorTestCase;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.YAML;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.semarglproject.vocab.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test {@link YAMLExtractor}.
 *
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YAMLExtractorTest extends AbstractExtractorTestCase {

    public static final Logger log = LoggerFactory.getLogger(YAMLExtractorTest.class);

    private static final YAML vocab = YAML.getInstance();

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new YAMLExtractorFactory();
    }

    @Test
    public void simpleFileLoading()
            throws Exception {
        assertExtract("/org/apache/any23/extractor/yaml/simple-load.yml");
        log.debug(dumpModelToTurtle());
        assertModelNotEmpty();

    }

    @Test
    public void integersTest()
            throws Exception {
        assertExtract("/org/apache/any23/extractor/yaml/different-integers.yml");
        log.debug(dumpModelToTurtle());
        assertModelNotEmpty();
        assertContains(null, RDFS.LABEL, RDFUtils.literal("hexadecimal"));
        assertContains(null, RDFS.LABEL, RDFUtils.literal("octal"));
    }

    @Test
    public void floatsTest()
            throws Exception {
        assertExtract("/org/apache/any23/extractor/yaml/different-float.yml");
        log.debug(dumpModelToTurtle());
        assertModelNotEmpty();
    }

    @Test
    public void multiTest()
            throws Exception {
        assertExtract("/org/apache/any23/extractor/yaml/multi-test.yml");
        log.debug(dumpModelToTurtle());
        assertModelNotEmpty();
        RepositoryResult<Statement> docs = getStatements(null, RDF.TYPE, vocab.document);
        Assert.assertTrue(Iterations.asList(docs).size() > 1);
    }

    @Test
    public void nullTest()
            throws Exception {
        assertExtract("/org/apache/any23/extractor/yaml/test-null.yml");
        log.debug(dumpModelToTurtle());
        assertModelNotEmpty();
        RepositoryResult<Statement> docs = getStatements(null, null, RDF.NIL);
        Assert.assertTrue(Iterations.asList(docs).size() == 2);
    }
}
