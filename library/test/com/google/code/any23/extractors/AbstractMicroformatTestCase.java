package com.google.code.any23.extractors;

import java.net.URI;


import com.google.code.any23.vocab.XFN;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import junit.framework.TestCase;

public abstract class AbstractMicroformatTestCase extends TestCase {

	protected static URI baseURI = URI.create("http://bob.example.com/");
	protected Resource thePage ;
	protected Model model;

	public AbstractMicroformatTestCase() {
		super();
	}

	public AbstractMicroformatTestCase(String name) {
		super(name);
	}

	public void setUp() {
		model = ModelFactory.createDefaultModel();
		model.createResource(baseURI.toString());
	}

	protected void assertContains(Property action, Resource resource) {
		assertContains(null,action, resource);
	}

	protected void assertContains(Property action, String string) {
		Literal literal= model.createLiteral(string);
		assertContains(null, action, literal);
	}

	protected void assertNotContains(Property action, Resource resource) {
		assertNotContains(null,action, resource);
	}
	

	protected String absolute(String uri) {
		return baseURI.resolve(uri).toString();
	}
	protected abstract boolean extract(String name);	
	
	protected void assertExtracts(String fileName) {
		assertTrue(extract(fileName));
	}

	protected void assertNotExtracts(String fileName) {
		assertFalse(extract(fileName));
	}

	protected void assertContains(Resource subject, Property property, Resource object) {
		assertTrue(model.contains(subject, property, object ));
	}

	protected void assertContains(Resource subject, Property property, Literal object) {
		assertTrue(model.contains(subject, property, object ));
	}

	
	protected void assertContainsEXFN(Resource subj, String prop, Resource obj) {
		assert(model.contains(subj,XFN.getExtendedProperty(prop), obj));
	}

	protected void assertNotContains(Resource subj, Property prop, String obj) {
		assertFalse(model.contains(subj,prop,obj));
	}
	protected void assertNotContains(Resource subj, Property prop, Resource obj) {
		assertFalse(model.contains(subj,prop,obj));
	}

	protected void assertModelNotEmpty() {
		assertFalse(model.isEmpty());
	}

	protected void assertNotContains(Resource subj, Property prop, Literal obj) {
		assertFalse(model.contains(subj,prop,obj));
	}

	protected void assertModelEmpty() {
		assertTrue(model.isEmpty());
	}

	protected Resource findExactlyOneBlankSubject(Property property, RDFNode object) {
		StmtIterator it = model.listStatements(null, property, object);
		assertTrue(it.hasNext());
		Statement stmt = it.nextStatement();
		Resource result = stmt.getSubject();
		assertTrue(result.isAnon());
		assertFalse(it.hasNext());
		return result;
	}

	protected void dumpModel() {
		model.write(System.err,"TURTLE");
	}

	protected void assertStatementsSize(Property prop, Resource res, int size) {
		StmtIterator iter = model.listStatements(null, prop, res);
		assertEquals(size, iter.toSet().size());
	}

	protected void assertContains(Resource vcard, Property fn, String string) {
		assertContains(vcard, fn, model.createLiteral(string));
	}



	
}