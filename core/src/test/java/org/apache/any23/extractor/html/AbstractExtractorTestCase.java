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

package org.apache.any23.extractor.html;

import org.apache.any23.AbstractAny23TestBase;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SingleDocumentExtraction;
import org.apache.any23.extractor.SingleDocumentExtractionReport;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.SINDICE;
import org.apache.any23.writer.RepositoryWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Abstract class used to write {@link org.apache.any23.extractor.Extractor}
 * specific test cases.
 */
public abstract class AbstractExtractorTestCase extends AbstractAny23TestBase {

    /**
     * Base test document.
     */
    protected static URI baseURI = RDFUtils.uri("http://bob.example.com/"); // TODO: change base URI string.

    /**
     * Internal connection used to collect extraction results.
     */
    protected RepositoryConnection conn;

    /**
     * The latest generated report.
     */
    private SingleDocumentExtractionReport report;

    /**
     * Constructor.
     */
    public AbstractExtractorTestCase() {
        super();
    }

    /**
     * @return the factory of the extractor to be tested.
     */
    protected abstract ExtractorFactory<?> getExtractorFactory();

    /**
     * Test case initialization.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Sail store = new MemoryStore();
        store.initialize();
        conn = new SailRepository(store).getConnection();
    }

    /**
     * Test case resources release.
     *
     * @throws RepositoryException
     */
    @After
    public void tearDown() throws RepositoryException {
        conn.close();
        conn   = null;
        report = null;
    }

    /**
     * @return the connection to the memory repository.
     */
    protected RepositoryConnection getConnection() {
        return conn;
    }

    /**
     * @return the last generated report.
     */
    protected SingleDocumentExtractionReport getReport() {
        return report;
    }

    /**
     * Returns the list of issues raised by a given extractor.
     *
     * @param extractorName name of the extractor.
     * @return collection of issues.
     */
    protected Collection<IssueReport.Issue> getIssues(String extractorName) {
        for(
                Map.Entry<String, Collection<IssueReport.Issue>> issueEntry
                :
                report.getExtractorToIssues().entrySet()
        ) {
            if(issueEntry.getKey().equals(extractorName)) {
                return issueEntry.getValue();
            }
        }
        return Collections.emptyList();
    }

    /**
     * Returns the list of issues raised by the extractor under testing.
     *
     * @return collection of issues.
     */
    protected Collection<IssueReport.Issue> getIssues() {
        return getIssues(getExtractorFactory().getExtractorName());
    }

    /**
     * Applies the extractor provided by the {@link #getExtractorFactory()} to the specified resource.
     *
     * @param resource resource name.
     * @throws org.apache.any23.extractor.ExtractionException
     * @throws IOException
     */
    // TODO: MimeType detector to null forces the execution of all extractors, but extraction
    //       tests should be based on mimetype detection.
    protected void extract(String resource) throws ExtractionException, IOException {
        SingleDocumentExtraction ex = new SingleDocumentExtraction(
            new HTMLFixture(copyResourceToTempFile(resource)).getOpener(baseURI.toString()),
            getExtractorFactory(), new RepositoryWriter(conn)
        );
        ex.setMIMETypeDetector(null);
        report = ex.run();
    }

    /**
     * Performs data extraction over the content of a resource
     * and assert that the extraction was fine.
     *
     * @param resource resource name.
     * @param assertNoIssues if <code>true</code>invokes {@link #assertNoIssues()}  after the extraction.
     */
    protected void assertExtract(String resource, boolean assertNoIssues) {
        try {
            extract(resource);
            if(assertNoIssues) assertNoIssues();
        } catch (ExtractionException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Performs data extraction over the content of a resource
     *  and assert that the extraction was fine and raised no issues.
     *
     * @param resource
     */
    protected void assertExtract(String resource) {
        assertExtract(resource, true);
    }

    /**
     * Asserts that the extracted triples contain the pattern <code>(_ p o)</code>.
     *
     * @param p predicate
     * @param o object.
     * @throws RepositoryException
     */
    protected void assertContains(URI p, Resource o) throws RepositoryException {
        assertContains(null, p, o);
    }

    /**
     * Asserts that the extracted triples contain the pattern <code>(_ p o)</code>.
     *
     * @param p predicate
     * @param o object.
     * @throws RepositoryException
     */
    protected void assertContains(URI p, String o) throws RepositoryException {
        assertContains(null, p, RDFUtils.literal(o));
    }

    /**
     * Asserts that the extracted triples contain the pattern <code>(_ p o)</code>.
     *
     * @param p predicate
     * @param o object.
     * @throws RepositoryException
     */
    protected void assertNotContains(URI p, Resource o) throws RepositoryException {
        assertNotContains(null, p, o);
    }

    /**
     * Asserts that the extracted triples contain the pattern <code>(s p o)</code>.
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @throws RepositoryException
     */
    protected void assertContains(Resource s, URI p, Value o) throws RepositoryException {
        Assert.assertTrue(
                getFailedExtractionMessage() +
                String.format("Cannot find triple (%s %s %s)", s, p, o),
                conn.hasStatement(s, p, o, false));
    }

    /**
     * Asserts that the extracted triples contain the pattern <code>(s p o)</code>.
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @throws RepositoryException
     */
    protected void assertNotContains(Resource s, URI p, String o) throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.hasStatement(s, p, RDFUtils.literal(o), false));
    }

    /**
     * Asserts that the extracted triples contain the pattern <code>(s p o)</code>.
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @throws RepositoryException
     */
    protected void assertNotContains(Resource s, URI p, Resource o) throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.hasStatement(s, p, o, false));
    }

    /**
     * Asserts that the model contains at least a statement.
     *
     * @throws RepositoryException
     */
    protected void assertModelNotEmpty() throws RepositoryException {
        Assert.assertFalse(
                "The model is expected to not be empty." + getFailedExtractionMessage(),
                conn.isEmpty()
        );
    }

    /**
     * Asserts that the model doesn't contain the pattern <code>(s p o)</code>
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @throws RepositoryException
     */
    protected void assertNotContains(Resource s, URI p, Literal o) throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.hasStatement(s, p, o, false));
    }

    /**
     * Asserts that the model is expected to contains no statements.
     *
     * @throws RepositoryException
     */
    protected void assertModelEmpty() throws RepositoryException {
        Assert.assertTrue(getFailedExtractionMessage(), conn.isEmpty());
    }

    /**
     * Asserts that the extraction generated no issues.
     */
    protected void assertNoIssues() {
        for( Map.Entry<String, Collection<IssueReport.Issue>> entry : report.getExtractorToIssues().entrySet() ) {
            if(entry.getValue().size() > 0) {
                Assert.fail("Unexpected issue for extractor " + entry.getKey() + " : " + entry.getValue());
            }
        }
    }

    /**
     * Asserts that an issue has been produced by the processed {@link org.apache.any23.extractor.Extractor}.
     *
     * @param level expected issue level
     * @param issueRegex regex matching the expected human readable issue message.
     */
    protected void assertIssue(IssueReport.IssueLevel level, String issueRegex) {
        final Collection<IssueReport.Issue> issues = getIssues(getExtractorFactory().getExtractorName());
        boolean found = false;
        for(IssueReport.Issue issue : issues) {
            if(issue.getLevel() == level && issue.getMessage().matches(issueRegex)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(
                String.format("Cannot find issue with level %s matching expression '%s'", level, issueRegex),
                found
        );
    }

    /**
     * Verifies that the current model contains all the given statements.
     *
     * @param statements list of statements to be verified.
     * @throws RepositoryException
     */
    public void assertContainsModel(Statement[] statements) throws RepositoryException {
        for(Statement statement : statements) {
            assertContains(statement);
        }
    }

    /**
     * Verifies that the current model contains all the statements declared in the
     * specified <code>modelFile</code>.
     *
     * @param modelResource the resource containing the model.
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     * @throws RepositoryException
     */
    public void assertContainsModel(String modelResource)
    throws RDFHandlerException, IOException, RDFParseException, RepositoryException {
        getConnection().remove(null, SINDICE.getInstance().date, (Value) null, (Resource) null);
        getConnection().remove(null, SINDICE.getInstance().size, (Value) null, (Resource) null);
        assertContainsModel(RDFUtils.parseRDF(modelResource));
    }

    /**
     * Asserts that the given pattern <code>(s p o)</code> satisfies the expected number of statements.
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @param expected expected matches.
     * @throws RepositoryException
     */
    protected void assertStatementsSize(Resource s, URI p, Value o, int expected)
    throws RepositoryException {
        Assert.assertEquals(
                "Unexpected number of matching statements.",
                expected,
                getStatementsSize(s, p, o)
        );
    }

    /**
     * Asserts that the given pattern <code>(_ p o)</code> satisfies the expected number of statements.
     *
     * @param p predicate.
     * @param o object.
     * @param expected expected matches.
     * @throws RepositoryException
     */
    protected void assertStatementsSize(URI p, Value o, int expected) throws RepositoryException {
        assertStatementsSize(null, p, o, expected);
    }

    /**
     * Asserts that the given pattern <code>(_ p o)</code> satisfies the expected number of statements.
     *
     * @param p predicate.
     * @param o object.
     * @param expected expected matches.
     * @throws RepositoryException
     */
    protected void assertStatementsSize(URI p, String o, int expected) throws RepositoryException {
        assertStatementsSize(p, o == null ? null : RDFUtils.literal(o), expected);
    }

    /**
     * Asserts that the given pattern <code>(s p _)</code> is not present.
     *
     * @param s subject.
     * @param p predicate.
     * @throws RepositoryException
     */
    protected void assertNotFound(Resource s, URI p) throws RepositoryException {
         RepositoryResult<Statement> statements = conn.getStatements(s, p, null, true);
        try {
            Assert.assertFalse("Expected no statements.", statements.hasNext());
        } finally {
            statements.close();
        }
    }

    /**
     * Returns the blank subject matching the pattern <code>(_:b p o)</code>,
     * it is expected to exists and be just one.
     *
     * @param p predicate.
     * @param o object.
     * @return the matching blank subject.
     * @throws RepositoryException
     */
    protected Resource findExactlyOneBlankSubject(URI p, Value o) throws RepositoryException {
        RepositoryResult<Statement> it = conn.getStatements(null, p, o, false);
        try {
            Assert.assertTrue(getFailedExtractionMessage(), it.hasNext());
            Statement stmt = it.next();
            Resource result = stmt.getSubject();
            Assert.assertTrue(getFailedExtractionMessage(), result instanceof BNode);
            Assert.assertFalse(getFailedExtractionMessage(), it.hasNext());
            return result;
        } finally {
            it.close();
        }
    }

    /**
     * Returns the object matching the pattern <code>(s p o)</code>,
     * it is expected to exists and be just one.
     *
     * @param s subject.
     * @param p predicate.
     * @return the matching object.
     * @throws RepositoryException
     */
    protected Value findExactlyOneObject(Resource s, URI p) throws RepositoryException {
        RepositoryResult<Statement> it = conn.getStatements(s, p, null, false);
        try {
            Assert.assertTrue(getFailedExtractionMessage(), it.hasNext());
            return it.next().getObject();
        } finally {
            it.close();
        }
    }

    /**
     * Returns all the subjects matching the pattern <code>(s? p o)</code>.
     *
     * @param p predicate.
     * @param o object.
     * @return list of matching subjects.
     * @throws RepositoryException
     */
    protected List<Resource> findSubjects(URI p, Value o) throws RepositoryException {
        RepositoryResult<Statement> it = conn.getStatements(null, p, o, false);
        List<Resource> subjects = new ArrayList<Resource>();
        try {
            Statement statement;
            while( it.hasNext() ) {
                statement = it.next();
                subjects.add( statement.getSubject() );
            }
        } finally {
            it.close();
        }
        return subjects;
    }

    /**
     * Returns all the objects matching the pattern <code>(s p _)</code>.
     *
     * @param s predicate.
     * @param p predicate.
     * @return list of matching objects.
     * @throws RepositoryException
     */
    protected List<Value> findObjects(Resource s, URI p) throws RepositoryException {
        RepositoryResult<Statement> it = conn.getStatements(s, p, null, false);
        List<Value> objects = new ArrayList<Value>();
        try {
            Statement statement;
            while( it.hasNext() ) {
                statement = it.next();
                objects.add( statement.getObject() );
            }
        } finally {
            it.close();
        }
        return objects;
    }

    /**
     * Finds the object matching the pattern <code>(s p _)</code>, asserts to find
     * exactly one result.
     *
     * @param s subject.
     * @param p predicate
     * @return matching object.
     * @throws org.openrdf.repository.RepositoryException
     */
    protected Value findObject(Resource s, URI p) throws RepositoryException {
        RepositoryResult<Statement> statements = conn.getStatements(s, p, null, true);
        try {
            Assert.assertTrue("Expected at least a statement.", statements.hasNext());
            return (statements.next().getObject());
        } finally {
            statements.close();
        }
    }

    /**
     * Finds the resource object matching the pattern <code>(s p _)</code>, asserts to find
     * exactly one result.
     *
     * @param s subject.
     * @param p predicate.
     * @return matching object.
     * @throws RepositoryException
     */
    protected Resource findObjectAsResource(Resource s, URI p) throws RepositoryException {
        final Value v = findObject(s, p);
        try {
            return (Resource) v;
        } catch (ClassCastException cce) {
            Assert.fail("Expected resource object, found: " + v.getClass().getSimpleName());
            throw new IllegalStateException();
        }
    }

    /**
     * Finds the literal object matching the pattern <code>(s p _)</code>, asserts to find
     * exactly one result.
     *
     * @param s subject.
     * @param p predicate.
     * @return matching object.
     * @throws RepositoryException
     */
    protected String findObjectAsLiteral(Resource s, URI p) throws RepositoryException {
        return findObject(s, p).stringValue();
    }

    /**
     * Dumps the extracted model in <i>Turtle</i> format.
     *
     * @return a string containing the model in Turtle.
     * @throws RepositoryException
     */
    protected String dumpModelToTurtle() throws RepositoryException {
        StringWriter w = new StringWriter();
        try {
            conn.export(Rio.createWriter(RDFFormat.TURTLE, w));
            return w.toString();
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Dumps the extracted model in <i>NQuads</i> format.
     *
     * @return a string containing the model in NQuads.
     * @throws RepositoryException
     */
    protected String dumpModelToNQuads() throws RepositoryException {
        StringWriter w = new StringWriter();
        try {
            conn.export(Rio.createWriter(RDFFormat.NQUADS, w));
            return w.toString();
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

     /**
     * Dumps the extracted model in <i>RDFXML</i> format.
     *
     * @return a string containing the model in RDFXML.
     * @throws RepositoryException
     */
    protected String dumpModelToRDFXML() throws RepositoryException {
        StringWriter w = new StringWriter();
        try {
            conn.export(Rio.createWriter(RDFFormat.RDFXML, w));
            return w.toString();
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Dumps the list of statements contained in the extracted model.
     *
     * @return list of extracted statements.
     * @throws RepositoryException
     */
    protected List<Statement> dumpAsListOfStatements() throws RepositoryException {
        return conn.getStatements(null, null, null, false).asList();
    }

    /**
     * @return string containing human readable statements.
     * @throws RepositoryException
     */
    protected String dumpHumanReadableTriples() throws RepositoryException {
        StringBuilder sb = new StringBuilder();
        RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
        while(result.hasNext()) {
            Statement statement = result.next();
            sb.append(String.format("%s %s %s %s\n",
                    statement.getSubject(),
                    statement.getPredicate(),
                    statement.getObject(),
                    statement.getContext()
                    )
            );
            
        }
        return sb.toString();
    }

    /**
     * Checks that a statement is contained in the extracted model.
     * If the statement declares bnodes, they are replaced with <code>_</code> patterns.
     *
     * @param statement
     * @throws RepositoryException
     */
    // TODO: bnode check is too weak, introduce graph omomorphism check.
    protected void assertContains(Statement statement) throws RepositoryException {
        Assert.assertTrue(
                "Cannot find statement " + statement + " in model.",
                conn.hasStatement(
                        statement.getSubject() instanceof BNode ? null : statement.getSubject(),
                        statement.getPredicate(),
                        statement.getObject()  instanceof BNode ? null : statement.getObject(),
                        false
                )
        );
    }

    /**
     * Assert that the model contains the statement <code>(s p l)</code> where <code>l</code> is a literal.
     *
     * @param s subject.
     * @param p predicate.
     * @param l literal content.
     * @throws RepositoryException
     */
    protected void assertContains(Resource s, URI p, String l) throws RepositoryException {
        assertContains(s, p, RDFUtils.literal(l));
    }

    /**
     * Assert that the model contains the statement <code>(s p l)</code> where <code>l</code>
     * is a language literal.
     *
     * @param s subject.
     * @param p predicate.
     * @param l literal content.
     * @param lang literal language.
     * @throws RepositoryException
     */
    protected void assertContains(Resource s, URI p, String l, String lang) throws RepositoryException {
        assertContains(s, p, RDFUtils.literal(l, lang));
    }

    /**
     * Returns all statements matching the pattern <code>(s p o)</code>.
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @return list of statements.
     * @throws RepositoryException
     */
    protected RepositoryResult<Statement> getStatements(Resource s, URI p, Value o)
    throws RepositoryException {
        return conn.getStatements(s, p, o, false);
    }

    /**
     * Counts all statements matching the pattern <code>(s p o)</code>.
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @return number of matches.
     * @throws RepositoryException
     */
    protected int getStatementsSize(Resource s, URI p, Value o)
    throws RepositoryException {
        RepositoryResult<Statement> result = getStatements(s, p, o);
        int count = 0;
        try {
            while (result.hasNext()) {
                result.next();
                count++;
            }
        } finally {
            result.close();
        }
        return count;
    }

    private String getFailedExtractionMessage() throws RepositoryException {
        return "Assertion failed! Extracted triples:\n" + dumpModelToTurtle();
    }

}