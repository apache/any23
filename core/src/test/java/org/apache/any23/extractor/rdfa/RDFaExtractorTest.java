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

package org.apache.any23.extractor.rdfa;

import org.apache.any23.extractor.ExtractorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;

import java.io.IOException;
import java.util.List;

/**
 * Reference Test Class for {@link RDFaExtractor}.
 */
public class RDFaExtractorTest extends AbstractRDFaExtractorTestCase {

    /**
     * Taken from the <a href="http://www.heppnetz.de/rdfa4google/testcases.html">GoodRelations test cases</a>.
     * It checks if the extraction is the same when the namespaces are defined in <i>RDFa1.0</i> or
     * <i>RDFa1.1</i> respectively.
     *
     * @throws org.eclipse.rdf4j.repository.RepositoryException if an error is encountered whilst loading content from a storage connection
     * @throws java.io.IOException if there is an error interpreting the input data
     * @throws org.eclipse.rdf4j.rio.RDFHandlerException if there is an error within the {@link org.eclipse.rdf4j.rio.RDFHandler}
     * @throws org.eclipse.rdf4j.rio.RDFParseException if there is an exception parsing an RDF Stream
     */
    @Test
    public void testRDFa11PrefixBackwardCompatibility()
    throws RepositoryException, RDFHandlerException, IOException, RDFParseException {
        final int EXPECTED_STATEMENTS = 31;

        assertExtract("/html/rdfa/goodrelations-rdfa10.html");
        logger.debug("Model 1 " + dumpHumanReadableTriples());
        Assert.assertEquals(EXPECTED_STATEMENTS, dumpAsListOfStatements().size());
        List<Statement> rdfa10Stmts = dumpAsListOfStatements();

        //assertContainsModel("/html/rdfa/goodrelations-rdfa10-expected.nq");

        assertExtract("/html/rdfa/goodrelations-rdfa11.html");
        logger.debug("Model 2 " + dumpHumanReadableTriples());
        Assert.assertTrue(dumpAsListOfStatements().size() >= EXPECTED_STATEMENTS);

        for(Statement stmt : rdfa10Stmts) {
            assertContains(stmt);
        }
    }

	@Test
	public void testRDFa11CURIEs() throws Exception {
	}
	
    /**
     * Tests that the default parser settings enable tolerance in data type parsing.
     */
    @Test
    public void testTolerantParsing() {
        assertExtract("/html/rdfa/oreilly-invalid-datatype.html");
    }

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new RDFaExtractorFactory();
    }

}
