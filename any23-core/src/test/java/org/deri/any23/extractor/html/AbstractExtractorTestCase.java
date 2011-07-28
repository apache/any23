/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SingleDocumentExtraction;
import org.deri.any23.parser.NQuadsWriter;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.writer.RepositoryWriter;
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
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class used to write {@link org.deri.any23.extractor.Extractor} specific
 * test cases.
 */
public abstract class AbstractExtractorTestCase {

    protected static URI baseURI = RDFUtils.uri("http://bob.example.com/");

    protected RepositoryConnection conn;

    public AbstractExtractorTestCase() {
        super();
    }

    protected abstract ExtractorFactory<?> getExtractorFactory();

    @Before
    public void setUp() throws Exception {
        Sail store = new MemoryStore();
        store.initialize();
        conn = new SailRepository(store).getConnection();
    }

     protected void assertContains(Statement statement) throws RepositoryException {
        if(statement.getSubject() instanceof BNode) {
            conn.hasStatement(
                    statement.getSubject() instanceof  BNode ? null : statement.getSubject(),
                    statement.getPredicate(),
                    statement.getObject() instanceof BNode   ? null : statement.getObject(),
                    false
            );
        }
    }

    public void assertContains(URI p, Resource o) throws RepositoryException {
        assertContains(null, p, o);
    }

    public void assertContains(URI p, String o) throws RepositoryException {
        assertContains(null, p, RDFUtils.literal(o));
    }

    public void assertNotContains(URI p, Resource o) throws RepositoryException {
        assertNotContains(null, p, o);
    }

    public void assertExtracts(String fileName) {
        try {
            extract(fileName);
        } catch (ExtractionException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void assertContains(Resource subject, URI property, Value object) throws RepositoryException {
        Assert.assertTrue(
                getFailedExtractionMessage() +
                        String.format("Cannot find triple (%s %s %s)", subject, property, object), 
                conn.hasStatement(subject, property, object, false));
    }

    public void assertNotContains(Resource subj, URI prop, String obj) throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.hasStatement(subj, prop, RDFUtils.literal(obj), false));
    }

    public void assertNotContains(Resource subj, URI prop, Resource obj) throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.hasStatement(subj, prop, obj, false));
    }

    public void assertModelNotEmpty() throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.isEmpty());
    }

    public void assertNotContains(Resource subj, URI prop, Literal obj) throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.hasStatement(subj, prop, obj, false));
    }

    public void assertModelEmpty() throws RepositoryException {
        Assert.assertTrue(getFailedExtractionMessage(), conn.isEmpty());
    }

    public Resource findExactlyOneBlankSubject(URI p, Value o) throws RepositoryException {
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

    public Value findExactlyOneObject(Resource s, URI p) throws RepositoryException {
        RepositoryResult<Statement> it = conn.getStatements(s, p, null, false);
        try {
            Assert.assertTrue(getFailedExtractionMessage(), it.hasNext());
            return it.next().getObject();
        } finally {
            it.close();
        }
    }

    public List<Resource> findSubjects(URI p, Value o) throws RepositoryException {
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

    public List<Value> findObjects(Resource s, URI p) throws RepositoryException {
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

    protected void extract(String name) throws ExtractionException, IOException {
        SingleDocumentExtraction ex = new SingleDocumentExtraction(
                new HTMLFixture(name).getOpener(baseURI.toString()),
                getExtractorFactory(), new RepositoryWriter(conn));
        // TODO: MimeType detector to null forces the execution of all extractors, but extraction
        //       tests should be based on mimetype detection.
        ex.setMIMETypeDetector(null);
        ex.run();
    }

    protected String dumpModelToTurtle() throws RepositoryException {
        StringWriter w = new StringWriter();
        try {
            conn.export(new TurtleWriter(w));
            return w.toString();
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String dumpModelToNQuads() throws RepositoryException {
        StringWriter w = new StringWriter();
        try {
            conn.export(new NQuadsWriter(w));
            return w.toString();
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String dumpModelToRDFXML() throws RepositoryException {
        StringWriter w = new StringWriter();
        try {
            conn.export(new RDFXMLWriter(w));
            return w.toString();
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected List<Statement> dumpAsListOfStatements() throws RepositoryException {
        List<Statement> result = conn.getStatements(null, null, null, false).asList();
        conn.remove(null, null, null, new Resource[]{});
        return result;
    }

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

    protected void assertContains(Resource s, URI p, String o) throws RepositoryException {
        assertContains(s, p, RDFUtils.literal(o));
    }

     protected void assertContains(Resource s, URI p, String o, String lang) throws RepositoryException {
        assertContains(s, p, RDFUtils.literal(o, lang));
    }

    protected int getStatementsSize(Resource subject, URI prop, Value obj)
    throws RepositoryException {
        RepositoryResult<Statement> result = conn.getStatements(subject, prop, obj, false);
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

    protected void assertStatementsSize(Resource subject, URI prop, Value obj, int expected)
    throws RepositoryException {
        junit.framework.Assert.assertEquals(expected, getStatementsSize(subject, prop, obj) );
    }

    protected void assertStatementsSize(URI prop, Value obj, int expected) throws RepositoryException {
        assertStatementsSize(null, prop, obj, expected);
    }

    protected void assertStatementsSize(URI prop, String obj, int expected) throws RepositoryException {
        assertStatementsSize(prop, obj == null ? null : RDFUtils.literal(obj), expected );
    }

    protected void assertNotFound(Resource sub, URI prop) throws RepositoryException {
         RepositoryResult<Statement> statements = conn.getStatements(sub, prop, null, true);
        try {
            junit.framework.Assert.assertFalse("Expected no statements.", statements.hasNext());
        } finally {
            statements.close();
        }
    }

    protected Value findObject(Resource sub, URI prop) throws RepositoryException {
        RepositoryResult<Statement> statements = conn.getStatements(sub, prop, null, true);
        try {
            junit.framework.Assert.assertTrue("Expected at least a statement.", statements.hasNext());
            return (statements.next().getObject());
        } finally {
            statements.close();
        }
    }

    protected Resource findObjectAsResource(Resource sub, URI prop) throws RepositoryException {
        return (Resource) findObject(sub, prop);
    }

    protected String findObjectAsLiteral(Resource sub, URI prop) throws RepositoryException {
        return findObject(sub, prop).stringValue();
    }

    private String getFailedExtractionMessage() throws RepositoryException {
        return "Assertion failed! Extracted triples:\n" + dumpModelToTurtle();
    }

}