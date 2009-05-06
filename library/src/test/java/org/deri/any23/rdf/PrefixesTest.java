package org.deri.any23.rdf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.deri.any23.Helper;

import junit.framework.TestCase;

public class PrefixesTest extends TestCase {
	private Prefixes p;
	
	public void setUp() {
		p = new Prefixes();
	}
	
	public void testEmptyPrefixes() {
		assertTrue(p.isEmpty());
		assertTrue(p.allPrefixes().isEmpty());
	}
	
	public void testUndefinedPrefix() {
		assertFalse(p.hasPrefix("ex"));
		assertFalse(p.hasNamespaceURI("ex"));
		assertNull(p.getNamespaceURIFor("ex"));
	}
	
	public void testCannotAbbreviateUndefined() {
		assertFalse(p.canAbbreviate("http://example.com/foo"));
	}
	
	public void testCannotExpandUndefined() {
		assertFalse(p.canExpand("ex:foo"));
	}
	
	public void testAddPrefix() {
		p.add("ex", "http://example.com/");
		assertFalse(p.isEmpty());
		assertEquals(Collections.singleton("ex"), p.allPrefixes());
	}
	
	public void testCheckForDeclaredPrefix() {
		p.add("ex", "http://example.com/");
		assertTrue(p.hasPrefix("ex"));
		assertTrue(p.hasNamespaceURI("http://example.com/"));
		assertEquals("http://example.com/", p.getNamespaceURIFor("ex"));
	}
	
	public void testCanExpandDeclaredPrefix() {
		p.add("ex", "http://example.com/");
		assertTrue(p.canExpand("ex:foo"));
		assertTrue(p.canExpand("ex:"));
		assertEquals(Helper.uri("http://example.com/foo"), p.expand("ex:foo"));
		assertEquals(Helper.uri("http://example.com/"), p.expand("ex:"));
	}
	
	public void testCanContractDeclaredNamespace() {
		p.add("ex", "http://example.com/");
		assertTrue(p.canAbbreviate("http://example.com/foo"));
		assertTrue(p.canAbbreviate("http://example.com/"));
		assertEquals("ex:foo", p.abbreviate("http://example.com/foo"));
		assertEquals("ex:", p.abbreviate("http://example.com/"));
	}
	
	public void testExpandOnlyAcceptsCURIEs() {
		try {
			p.expand("@");
			fail("Should have thrown IllegalArgumentException because argument is not a valid CURIE");
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}
	
	public void testCanExpandOnlyAcceptsCURIEs() {
		try {
			p.expand("@");
			fail("Should have thrown IllegalArgumentException because argument is not a valid CURIE");
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}
	
	public void testEmptyPrefix() {
		p.add("", "http://example.com/");
		assertFalse(p.isEmpty());
		assertEquals(Collections.singleton(""), p.allPrefixes());
		assertTrue(p.hasPrefix(""));
		assertEquals(":foo", p.abbreviate("http://example.com/foo"));
		assertEquals(Helper.uri("http://example.com/foo"), p.expand(":foo"));
		assertEquals(":", p.abbreviate("http://example.com/"));
		assertEquals(Helper.uri("http://example.com/"), p.expand(":"));
	}
	
	public void testCannotAddPrefixTwice() {
		p.add("ex", "http://example.com/");
		try {
			p.add("ex", "http://other.example.com/");
			fail("Should have failed because of duplicate assignment of 'ex' prefix");
		} catch (IllegalStateException ex) {
			// expected
		}
	}
	
	public void testCanReAssignToSameURI() {
		p.add("ex", "http://example.com/");
		p.add("ex", "http://example.com/");
		// should NOT throw IllegalStateException
	}
	
	public void testRemovePrefixResultsInEmptyMapping() {
		p.add("ex", "http://example.com/");
		p.removePrefix("ex");
		assertTrue(p.isEmpty());
		assertFalse(p.hasPrefix("ex"));
		assertFalse(p.hasNamespaceURI("http://example.com/"));
	}
	
	public void testCanAddAfterRemoving() {
		p.add("ex", "http://example.com/");
		p.removePrefix("ex");
		p.add("ex", "http://other.example.com/");
		assertEquals("http://other.example.com/", p.getNamespaceURIFor("ex"));
	}
	
	public void testMergeEmptyPrefixes() {
		p.add(new Prefixes());
		assertTrue(p.isEmpty());
	}
	
	public void testMergePrefixesWithoutConflict() {
		p.add("ex", "http://example.com/");
		p.add(Prefixes.create1("foaf", "http://xmlns.com/foaf/"));
		Set<String> prefixes = p.allPrefixes();
		assertTrue(prefixes.contains("ex"));
		assertTrue(prefixes.contains("foaf"));
		assertEquals(2, prefixes.size());
	}
	
	public void testCreate1() {
		p = Prefixes.create1("ex", "http://example.com/");
		assertEquals(1, p.allPrefixes().size());
		assertEquals("http://example.com/", p.getNamespaceURIFor("ex"));
	}
	
	public void testMergePrefixesWithConflictRaisesException() {
		p.add("ex", "http://example.com/");
		Prefixes p2 = Prefixes.create1("ex", "http://xmlns.com/foaf/");
		try {
			p.add(p2);
			fail("Should have failed because ex is assigned twice");
		} catch (IllegalStateException ex) {
			// expected
		}
	}
	
	public void testMergePrefixesWithConflictButSameNamespace() {
		p.add("ex", "http://example.com/");
		p.add(Prefixes.create1("ex", "http://example.com/"));
		Set<String> prefixes = p.allPrefixes();
		assertTrue(prefixes.contains("ex"));
		assertEquals(1, prefixes.size());
	}
	
	public void testCreateSubset() {
		p.add("ex", "http://example.com/");
		p.add("foaf", "http://xmlns.com/foaf/");
		Prefixes subset = p.createSubset("ex");
		assertEquals(1, subset.allPrefixes().size());
		assertTrue(subset.hasPrefix("ex"));
	}
	
	public void testCreateSubsetWithUndefinedPrefixThrowsException() {
		try {
			p.createSubset("ex");
			fail("Should have failed, p has no mapping for ex");
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}
	
	public void testAsMapEmpty() {
		assertTrue(p.asMap().isEmpty());
	}
	
	public void testAsMapIsUnmodifiable() {
		try {
			p.asMap().put("ex", "http://example.com/");
			fail("Should have failed, result of asMap() is supposed to be unmodifiable");
		} catch (UnsupportedOperationException ex) {
			// expected
		}
	}
	
	public void testAddVolatile() {
		p.addVolatile("ex", "http://example.com/");
		assertTrue(p.allPrefixes().contains("ex"));
		assertEquals("http://example.com/", p.getNamespaceURIFor("ex"));
	}
	
	public void testAddVolatileNeverFails() {
		p.add("ex", "http://example.com/");
		p.addVolatile("ex", "http://other.example.com/");
		p.addVolatile("foaf", "http://xmlns.com/foaf/");
		p.addVolatile("foaf", "http://foaf.example.com/");
		assertEquals(2, p.allPrefixes().size());
		assertTrue(p.hasPrefix("foaf"));
		assertTrue(p.hasPrefix("ex"));
	}
	
	public void testRemoveVolatilePrefix() {
		p.addVolatile("ex", "http://example.com/");
		p.removePrefix("ex");
		assertTrue(p.isEmpty());
		assertFalse(p.isVolatile("ex"));
	}
	
	public void testIsVolatile() {
		p.add("ex", "http://example.com/");
		p.addVolatile("foaf", "http://xmlns.com/foaf/");
		assertTrue(p.isVolatile("foaf"));
		assertFalse(p.isVolatile("ex"));
	}
	
	public void testUndefinedPrefixIsNotVolatile() {
		assertFalse(p.isVolatile("ex"));
	}
	
	public void testAddVolatileDoesNotOverwriteHardMapping() {
		p.add("ex", "http://example.com/");
		p.addVolatile("ex", "http://other.example.com/");
		assertEquals("http://example.com/", p.getNamespaceURIFor("ex"));
		assertFalse(p.isVolatile("ex"));
	}
	
	public void testAddVolatileDoesNotOverwriteVolatileMapping() {
		p.addVolatile("ex", "http://example.com/");
		p.addVolatile("ex", "http://other.example.com/");
		assertEquals("http://example.com/", p.getNamespaceURIFor("ex"));
		assertTrue(p.isVolatile("ex"));
	}
	
	public void testAddHardOverwritesVolatileMapping() {
		p.addVolatile("ex", "http://other.example.com/");
		p.add("ex", "http://example.com/");
		assertEquals("http://example.com/", p.getNamespaceURIFor("ex"));
		assertFalse(p.isVolatile("ex"));
	}
	
	public void testMergeWithVolatile() {
		p.add("a", "http://p1.example.com/");
		p.addVolatile("b", "http://p2.example.com/");
		p.addVolatile("c", "http://p3.example.com/");
		p.addVolatile("d", "http://p4.example.com/");
		Prefixes q = new Prefixes();
		q.addVolatile("a",  "http://q1.example.com/");
		p.add("b", "http://q2.example.com/");
		p.addVolatile("c", "http://q3.example.com/");
		p.addVolatile("e", "http://q5.example.com/");
		p.add(q);
		assertEquals(new HashSet<String>(Arrays.asList("a", "b", "c", "d", "e")), p.allPrefixes());
		assertEquals("http://p1.example.com/", p.getNamespaceURIFor("a"));
		assertEquals("http://q2.example.com/", p.getNamespaceURIFor("b"));
		assertEquals("http://p3.example.com/", p.getNamespaceURIFor("c"));
		assertEquals("http://p4.example.com/", p.getNamespaceURIFor("d"));
		assertEquals("http://q5.example.com/", p.getNamespaceURIFor("e"));
	}

	public void testAddPrefixesAsVolatile() {
		p.addVolatile("ex", "http://example.com/");
		Prefixes q = new Prefixes();
		q.add("ex", "http://other.example.com/");
		p.addVolatile(q);
		assertEquals("http://example.com/", p.getNamespaceURIFor("ex"));
	}
	
	public void testIncompatiblePrefixesInMergeAreDetected() {
		Prefixes p1 = PopularPrefixes.createSubset("rdf");
		Prefixes p2 = Prefixes.create1("rdf", "http://example.com/rdf#");
		try {
			p1.add(p2);
			fail("Should fail because of different mappings for rdf prefix");
		} catch (IllegalStateException ex) {
			// expected
		}
	}
	
	public void testNewPrefixesFromOtherPrefixesAreIndependent() {
		Prefixes p2 = new Prefixes(p);
		p2.add("ex", "http://example.org/");
		assertTrue(p.isEmpty());
	}
}
