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

import org.deri.any23.RDFHelper;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SingleDocumentExtraction;
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
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Abstract Test Class. All the classes testing a microformat {@link org.deri.any23.extractor.Extractor}
 * extend this one.
 */
public abstract class AbstractMicroformatTestCase {

    protected static URI baseURI = RDFHelper.uri("http://bob.example.com/");

    protected RepositoryConnection conn;

    public AbstractMicroformatTestCase() {
        super();
    }

    protected abstract ExtractorFactory<?> getExtractorFactory();

    @Before
    public void setUp() throws Exception {
        Sail store = new MemoryStore();
        store.initialize();
        conn = new SailRepository(store).getConnection();
    }

    public void assertContains(URI p, Resource o) throws RepositoryException {
        assertContains(null, p, o);
    }

    public void assertContains(URI p, String o) throws RepositoryException {
        assertContains(null, p, RDFHelper.literal(o));
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
        Assert.assertTrue(getFailedExtractionMessage(), conn.hasStatement(subject, property, object, false));
    }

    public void assertNotContains(Resource subj, URI prop, String obj) throws RepositoryException {
        Assert.assertFalse(getFailedExtractionMessage(), conn.hasStatement(subj, prop, RDFHelper.literal(obj), false));
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

    protected void extract(String name) throws ExtractionException, IOException {
        SingleDocumentExtraction ex = new SingleDocumentExtraction(
                new HTMLFixture(name).getOpener(baseURI.toString()),
                getExtractorFactory(), new RepositoryWriter(conn));
        ex.setMIMETypeDetector(null);
        ex.run();
    }

    protected String dumpModelToString() throws RepositoryException {
        StringWriter w = new StringWriter();
        try {
            conn.export(new TurtleWriter(w));
            return w.toString();
        } catch (RDFHandlerException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void assertContains(Resource s, URI p, String o) throws RepositoryException {
        assertContains(s, p, RDFHelper.literal(o));
    }

    protected void assertStatementsSize(URI prop, Value obj, int expected) throws RepositoryException {
        RepositoryResult<Statement> result = conn.getStatements(null, prop, obj, false);
        int count = 0;
        try {
            while (result.hasNext()) {
                result.next();
                count++;
            }
        } finally {
            result.close();
        }
        junit.framework.Assert.assertEquals(expected, count);
    }

    protected void assertStatementsSize(URI prop, String obj, int expected) throws RepositoryException {
        assertStatementsSize(prop, obj == null ? null : RDFHelper.literal(obj), expected );
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
        return "Assertion failed! Extracted triples:\n" + dumpModelToString();
    }

}