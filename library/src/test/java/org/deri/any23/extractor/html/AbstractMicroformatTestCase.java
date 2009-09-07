package org.deri.any23.extractor.html;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SingleDocumentExtraction;
import org.deri.any23.writer.RepositoryWriter;
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

public abstract class AbstractMicroformatTestCase extends TestCase {
	protected static URI baseURI = Helper.uri("http://bob.example.com/");

	protected RepositoryConnection conn;
	
	public AbstractMicroformatTestCase() {
		super();
	}

	public AbstractMicroformatTestCase(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		Sail store = new MemoryStore();
		store.initialize();
		conn = new SailRepository(store).getConnection();
	}

	protected abstract ExtractorFactory<?> getExtractorFactory();
	
	protected void extract(String name) throws ExtractionException, IOException {
		SingleDocumentExtraction ex = new SingleDocumentExtraction(
				new HTMLFixture(name).getOpener(baseURI.toString()), 
				getExtractorFactory(), new RepositoryWriter(conn));
		ex.setMIMETypeDetector(null);
		ex.run();
	}
	
	protected void assertContains(URI p, Resource o) throws RepositoryException {
		assertContains(null, p, o);
	}

	protected void assertContains(URI p, String o) throws RepositoryException {
		assertContains(null, p, Helper.literal(o));
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

	protected void assertNotExtracts(String fileName) throws RepositoryException {
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
		assertTrue(getFailedExtractionMessage(), conn.hasStatement(subject, property, object, false));
	}

	protected void assertNotContains(Resource subj, URI prop, String obj) throws RepositoryException {
		assertFalse(getFailedExtractionMessage(), conn.hasStatement(subj,prop, Helper.literal(obj), false));
	}
	
	protected void assertNotContains(Resource subj, URI prop, Resource obj) throws RepositoryException {
		assertFalse(getFailedExtractionMessage(), conn.hasStatement(subj,prop,obj, false));
	}

	protected void assertModelNotEmpty() throws RepositoryException {
		assertFalse(getFailedExtractionMessage(), conn.isEmpty());
	}

	protected void assertNotContains(Resource subj, URI prop, Literal obj) throws RepositoryException {
		assertFalse(getFailedExtractionMessage(), conn.hasStatement(subj,prop,obj, false));
	}

	protected void assertModelEmpty() throws RepositoryException {
		assertTrue(getFailedExtractionMessage(), conn.isEmpty());
	}

	protected Resource findExactlyOneBlankSubject(URI p, Value o) throws RepositoryException {
		RepositoryResult<Statement> it = conn.getStatements(null, p, o, false);
		assertTrue(getFailedExtractionMessage(), it.hasNext());
		Statement stmt = it.next();
		Resource result = stmt.getSubject();
		assertTrue(getFailedExtractionMessage(), result instanceof BNode);
		assertFalse(getFailedExtractionMessage(), it.hasNext());
		return result;
	}

	protected void dumpModel() throws RepositoryException {
		System.err.print(dumpModelToString());
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

	private String getFailedExtractionMessage() throws RepositoryException {
		return "Assertion failed! Extracted triples:\n" + dumpModelToString();
	}
	
	protected void assertStatementsSize(URI prop, Resource res, int size) throws RepositoryException {
		assertEquals(size, conn.getStatements(null, prop, res, false).asList().size());
	}

	protected void assertContains(Resource s, URI p, String o) throws RepositoryException {
		assertContains(s, p, Helper.literal(o));
	}
}