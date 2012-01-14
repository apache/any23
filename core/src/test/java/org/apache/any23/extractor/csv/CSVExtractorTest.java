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

package org.apache.any23.extractor.csv;

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.html.AbstractExtractorTestCase;
import org.apache.any23.vocab.CSV;
import org.junit.Test;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference test case for {@link CSVExtractor}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class CSVExtractorTest extends AbstractExtractorTestCase {

    private static final Logger logger = LoggerFactory.getLogger(CSVExtractorTest.class);

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return CSVExtractor.factory;
    }

    @Test
    public void testExtractionCommaSeparated() throws RepositoryException {
        CSV csv = CSV.getInstance();
        assertExtracts("org/apache/any23/extractor/csv/test-comma.csv");
        logger.debug(dumpModelToRDFXML());

        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 28);
        assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
        assertContains(null, csv.numberOfColumns, new LiteralImpl("4", XMLSchema.INTEGER));
        assertContains(null, csv.numberOfRows, new LiteralImpl("3", XMLSchema.INTEGER));
    }

    @Test
    public void testExtractionSemicolonSeparated() throws RepositoryException {
        CSV csv = CSV.getInstance();
        assertExtracts("org/apache/any23/extractor/csv/test-semicolon.csv");
        logger.debug(dumpModelToRDFXML());

        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 28);
        assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
        assertContains(null, csv.numberOfColumns, new LiteralImpl("4", XMLSchema.INTEGER));
        assertContains(null, csv.numberOfRows, new LiteralImpl("3", XMLSchema.INTEGER));
    }

    @Test
    public void testExtractionTabSeparated() throws RepositoryException {
        CSV csv = CSV.getInstance();
        assertExtracts("org/apache/any23/extractor/csv/test-tab.csv");
        logger.debug(dumpModelToRDFXML());

        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 28);
        assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
        assertContains(null, csv.numberOfColumns, new LiteralImpl("4", XMLSchema.INTEGER));
        assertContains(null, csv.numberOfRows, new LiteralImpl("3", XMLSchema.INTEGER));
    }

    @Test
    public void testTypeManagement() throws RepositoryException {
        CSV csv = CSV.getInstance();
        assertExtracts("org/apache/any23/extractor/csv/test-type.csv");
        logger.debug(dumpModelToRDFXML());

        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 21);
        assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
        assertContains(null, csv.numberOfColumns, new LiteralImpl("2", XMLSchema.INTEGER));
        assertContains(null, csv.numberOfRows, new LiteralImpl("3", XMLSchema.INTEGER));
        assertContains(null, null, new LiteralImpl("5.2", XMLSchema.FLOAT));
        assertContains(null, null, new LiteralImpl("7.9", XMLSchema.FLOAT));
        assertContains(null, null, new LiteralImpl("10" , XMLSchema.INTEGER));
    }

}
