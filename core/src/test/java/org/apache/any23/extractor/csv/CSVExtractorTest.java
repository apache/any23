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
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference test case for {@link CSVExtractor}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class CSVExtractorTest extends AbstractExtractorTestCase {

  private static final Logger logger = LoggerFactory
          .getLogger(CSVExtractorTest.class);

  @Override
  protected ExtractorFactory<?> getExtractorFactory() {
    return new CSVExtractorFactory();
  }

  @Test
  public void testExtractionCommaSeparated() throws Exception {
    CSV csv = CSV.getInstance();
    assertExtract("/org/apache/any23/extractor/csv/test-comma.csv");
    logger.debug(dumpModelToRDFXML());

    assertModelNotEmpty();
    assertStatementsSize(null, null, null, 28);
    assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
    assertContains(null, csv.numberOfColumns, SimpleValueFactory.getInstance().createLiteral("4",
            XMLSchema.INTEGER));
    assertContains(null, csv.numberOfRows, SimpleValueFactory.getInstance().createLiteral("3",
            XMLSchema.INTEGER));
  }

  @Test
  public void testExtractionSemicolonSeparated() throws Exception {
    CSV csv = CSV.getInstance();
    assertExtract("/org/apache/any23/extractor/csv/test-semicolon.csv");
    logger.debug(dumpModelToRDFXML());

    assertModelNotEmpty();
    assertStatementsSize(null, null, null, 28);
    assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
    assertContains(null, csv.numberOfColumns, SimpleValueFactory.getInstance().createLiteral("4",
            XMLSchema.INTEGER));
    assertContains(null, csv.numberOfRows, SimpleValueFactory.getInstance().createLiteral("3",
            XMLSchema.INTEGER));
  }

  @Test
  public void testExtractionTabSeparated() throws Exception {
    CSV csv = CSV.getInstance();
    assertExtract("/org/apache/any23/extractor/csv/test-tab.csv");
    logger.debug(dumpModelToRDFXML());

    assertModelNotEmpty();
    assertStatementsSize(null, null, null, 28);
    assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
    assertContains(null, csv.numberOfColumns, SimpleValueFactory.getInstance().createLiteral("4",
            XMLSchema.INTEGER));
    assertContains(null, csv.numberOfRows, SimpleValueFactory.getInstance().createLiteral("3",
            XMLSchema.INTEGER));
  }

  @Test
  public void testTypeManagement() throws Exception {
    CSV csv = CSV.getInstance();
    assertExtract("/org/apache/any23/extractor/csv/test-type.csv");
    logger.debug(dumpModelToRDFXML());

    assertModelNotEmpty();
    assertStatementsSize(null, null, null, 21);
    assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
    assertContains(null, csv.numberOfColumns, SimpleValueFactory.getInstance().createLiteral("2",
            XMLSchema.INTEGER));
    assertContains(null, csv.numberOfRows, SimpleValueFactory.getInstance().createLiteral("3",
            XMLSchema.INTEGER));
    assertContains(null, null, SimpleValueFactory.getInstance().createLiteral("5.2", XMLSchema.FLOAT));
    assertContains(null, null, SimpleValueFactory.getInstance().createLiteral("7.9", XMLSchema.FLOAT));
    assertContains(null, null, SimpleValueFactory.getInstance().createLiteral("10", XMLSchema.INTEGER));
  }

  @Test
  public void testExtractionEmptyValue() throws Exception {
    CSV csv = CSV.getInstance();
    assertExtract("/org/apache/any23/extractor/csv/test-missing.csv");
    logger.debug(dumpModelToRDFXML());

    assertModelNotEmpty();
    assertStatementsSize(null, null, null, 25);
    assertStatementsSize(null, RDF.TYPE, csv.rowType, 3);
    assertContains(null, csv.numberOfColumns, SimpleValueFactory.getInstance().createLiteral("4",
            XMLSchema.INTEGER));
    assertContains(null, csv.numberOfRows, SimpleValueFactory.getInstance().createLiteral("3",
            XMLSchema.INTEGER));
    assertContains(null, null, SimpleValueFactory.getInstance().createLiteral("Michele", XMLSchema.STRING));
    assertContains(null, null,
            SimpleValueFactory.getInstance().createLiteral("Giovanni", XMLSchema.STRING));
  }

}
