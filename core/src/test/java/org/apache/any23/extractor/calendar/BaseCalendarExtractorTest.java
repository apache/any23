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

package org.apache.any23.extractor.calendar;

import org.apache.any23.extractor.html.AbstractExtractorTestCase;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Assert;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class BaseCalendarExtractorTest extends AbstractExtractorTestCase {

    abstract String filePrefix();

    protected void extractAndVerifyAgainstNQuads(String actual, String expected)
            throws RepositoryException, RDFHandlerException, IOException, RDFParseException {
        String filePrefix = filePrefix();
        assertExtract(filePrefix + actual);
        assertModelNotEmpty();
        List<Statement> expectedStatements = loadResultStatement(filePrefix + expected);
        int actualStmtSize = getStatementsSize(null, null, null);
        Assert.assertEquals(expectedStatements.size(), actualStmtSize);
        for (Statement statement : expectedStatements) {
            assertContains(
                    statement.getSubject() instanceof BNode ? null : statement.getSubject(),
                    statement.getPredicate(),
                    statement.getObject() instanceof BNode ? null : statement.getObject()
            );
        }
    }

    private List<Statement> loadResultStatement(String resultFilePath)
            throws RDFHandlerException, IOException, RDFParseException {
        RDFParser nQuadsParser = Rio.createParser(RDFFormat.NQUADS);
        TestRDFHandler rdfHandler = new TestRDFHandler();
        nQuadsParser.setRDFHandler(rdfHandler);
        File file = copyResourceToTempFile(resultFilePath);
        nQuadsParser.parse(
                new FileReader(file),
                baseIRI.toString()
        );
        return rdfHandler.getStatements();
    }

    private static class TestRDFHandler implements RDFHandler {

        private final List<Statement> statements = new ArrayList<Statement>();

        protected List<Statement> getStatements() {
            return statements;
        }

        public void startRDF() throws RDFHandlerException {
        }

        public void endRDF() throws RDFHandlerException {
        }

        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void handleStatement(Statement statement) throws RDFHandlerException {
            statements.add(statement);
        }

        public void handleComment(String s) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }
    }
}
