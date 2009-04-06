package org.deri.any23.extractor.html;

import java.io.IOException;

import junit.framework.TestCase;

import org.deri.any23.TestHelper;
import org.deri.any23.extractor.ExtractionException;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;

public abstract class AbstractMicroformatTestCase extends TestCase {
	protected static URI baseURI = TestHelper.uri("http://bob.example.com/");

	protected RepositoryConnection conn;
	
	public AbstractMicroformatTestCase() {
		super();
	}

	public AbstractMicroformatTestCase(String name) {
		super(name);
	}

	public void setUp() {
		
	}

	protected abstract void extract(String name) throws ExtractionException, IOException;	
	
	protected void assertContains(URI p, Resource o) throws RepositoryException {
		assertContains(null, p, o);
	}

	protected void assertContains(URI p, String o) throws RepositoryException {
		assertContains(null, p, TestHelper.literal(o));
	}

	protected void assertNotContains(URI p, Resource o) throws RepositoryException {
		assertNotContains(null, p, o);
	}
	
	protected void assertExtracts(String fileName) {
		try {
			extract(fileName);
		} catch (ExtractionException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void assertNotExtracts(String fileName) {
		try {
			extract(fileName);
			fail();
		} catch (ExtractionException ex) {
			// expected
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void assertContains(Resource subject, URI property, Value object) throws RepositoryException {
		assertTrue(conn.hasStatement(subject, property, object, false));
	}

	protected void assertNotContains(Resource subj, URI prop, String obj) throws RepositoryException {
		assertFalse(conn.hasStatement(subj,prop, TestHelper.literal(obj), false));
	}
	
	protected void assertNotContains(Resource subj, URI prop, Resource obj) throws RepositoryException {
		assertFalse(conn.hasStatement(subj,prop,obj, false));
	}

	protected void assertModelNotEmpty() throws RepositoryException {
		assertFalse(conn.isEmpty());
	}

	protected void assertNotContains(Resource subj, URI prop, Literal obj) throws RepositoryException {
		assertFalse(conn.hasStatement(subj,prop,obj, false));
	}

	protected void assertModelEmpty() throws RepositoryException {
		assertTrue(conn.isEmpty());
	}

	protected Resource findExactlyOneBlankSubject(URI p, Value o) throws RepositoryException {
		RepositoryResult<Statement> it = conn.getStatements(null, p, o, false);
		assertTrue(it.hasNext());
		Statement stmt = it.next();
		Resource result = stmt.getSubject();
		assertTrue(result instanceof BNode);
		assertFalse(it.hasNext());
		return result;
	}

	protected void dumpModel() throws RDFHandlerException, RepositoryException {
		conn.export(new TurtleWriter(System.err));
	}

	protected void assertStatementsSize(URI prop, Resource res, int size) throws RepositoryException {
		assertEquals(size, conn.getStatements(null, prop, res, false).asList().size());
	}

	protected void assertContains(Resource s, URI p, String o) throws RepositoryException {
		assertContains(s, p, TestHelper.literal(o));
	}
}