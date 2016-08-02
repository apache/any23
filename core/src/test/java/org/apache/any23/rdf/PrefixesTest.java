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

package org.apache.any23.rdf;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Reference Test class for {@link Prefixes}
 */
public class PrefixesTest {

    private Prefixes p;

    @Before
    public void setUp() {
        p = new Prefixes();
    }

    @Test
    public void testEmptyPrefixes() {
        Assert.assertTrue(p.isEmpty());
        Assert.assertTrue(p.allPrefixes().isEmpty());
    }

    @Test
    public void testUndefinedPrefix() {
        Assert.assertFalse(p.hasPrefix("ex"));
        Assert.assertFalse(p.hasNamespaceIRI("ex"));
        Assert.assertNull(p.getNamespaceIRIFor("ex"));
    }

    @Test
    public void testCannotAbbreviateUndefined() {
        Assert.assertFalse(p.canAbbreviate("http://example.com/foo"));
    }

    @Test
    public void testCannotExpandUndefined() {
        Assert.assertFalse(p.canExpand("ex:foo"));
    }

    @Test
    public void testAddPrefix() {
        p.add("ex", "http://example.com/");
        Assert.assertFalse(p.isEmpty());
        Assert.assertEquals(Collections.singleton("ex"), p.allPrefixes());
    }

    @Test
    public void testCheckForDeclaredPrefix() {
        p.add("ex", "http://example.com/");
        Assert.assertTrue(p.hasPrefix("ex"));
        Assert.assertTrue(p.hasNamespaceIRI("http://example.com/"));
        Assert.assertEquals("http://example.com/", p.getNamespaceIRIFor("ex"));
    }

    @Test
    public void testCanExpandDeclaredPrefix() {
        p.add("ex", "http://example.com/");
        Assert.assertTrue(p.canExpand("ex:foo"));
        Assert.assertTrue(p.canExpand("ex:"));
        Assert.assertEquals(RDFUtils.iri("http://example.com/foo"), p.expand("ex:foo"));
        Assert.assertEquals(RDFUtils.iri("http://example.com/"), p.expand("ex:"));
    }

    @Test
    public void testCanContractDeclaredNamespace() {
        p.add("ex", "http://example.com/");
        Assert.assertTrue(p.canAbbreviate("http://example.com/foo"));
        Assert.assertTrue(p.canAbbreviate("http://example.com/"));
        Assert.assertEquals("ex:foo", p.abbreviate("http://example.com/foo"));
        Assert.assertEquals("ex:", p.abbreviate("http://example.com/"));
    }

    @Test
    public void testExpandOnlyAcceptsCURIEs() {
        try {
            p.expand("@");
            Assert.fail("Should have thrown IllegalArgumentException because argument is not a valid CURIE");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testCanExpandOnlyAcceptsCURIEs() {
        try {
            p.expand("@");
            Assert.fail("Should have thrown IllegalArgumentException because argument is not a valid CURIE");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testEmptyPrefix() {
        p.add("", "http://example.com/");
        Assert.assertFalse(p.isEmpty());
        Assert.assertEquals(Collections.singleton(""), p.allPrefixes());
        Assert.assertTrue(p.hasPrefix(""));
        Assert.assertEquals(":foo", p.abbreviate("http://example.com/foo"));
        Assert.assertEquals(RDFUtils.iri("http://example.com/foo"), p.expand(":foo"));
        Assert.assertEquals(":", p.abbreviate("http://example.com/"));
        Assert.assertEquals(RDFUtils.iri("http://example.com/"), p.expand(":"));
    }

    @Test
    public void testCannotAddPrefixTwice() {
        p.add("ex", "http://example.com/");
        try {
            p.add("ex", "http://other.example.com/");
            Assert.fail("Should have failed because of duplicate assignment of 'ex' prefix");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void testCanReAssignToSameIRI() {
        p.add("ex", "http://example.com/");
        p.add("ex", "http://example.com/");
        // should NOT throw IllegalStateException
    }

    @Test
    public void testRemovePrefixResultsInEmptyMapping() {
        p.add("ex", "http://example.com/");
        p.removePrefix("ex");
        Assert.assertTrue(p.isEmpty());
        Assert.assertFalse(p.hasPrefix("ex"));
        Assert.assertFalse(p.hasNamespaceIRI("http://example.com/"));
    }

    @Test
    public void testCanAddAfterRemoving() {
        p.add("ex", "http://example.com/");
        p.removePrefix("ex");
        p.add("ex", "http://other.example.com/");
        Assert.assertEquals("http://other.example.com/", p.getNamespaceIRIFor("ex"));
    }

    @Test
    public void testMergeEmptyPrefixes() {
        p.add(new Prefixes());
        Assert.assertTrue(p.isEmpty());
    }

    @Test
    public void testMergePrefixesWithoutConflict() {
        p.add("ex", "http://example.com/");
        p.add(Prefixes.create1("foaf", "http://xmlns.com/foaf/"));
        Set<String> prefixes = p.allPrefixes();
        Assert.assertTrue(prefixes.contains("ex"));
        Assert.assertTrue(prefixes.contains("foaf"));
        Assert.assertEquals(2, prefixes.size());
    }

    @Test
    public void testCreate1() {
        p = Prefixes.create1("ex", "http://example.com/");
        Assert.assertEquals(1, p.allPrefixes().size());
        Assert.assertEquals("http://example.com/", p.getNamespaceIRIFor("ex"));
    }

    @Test
    public void testMergePrefixesWithConflictRaisesException() {
        p.add("ex", "http://example.com/");
        Prefixes p2 = Prefixes.create1("ex", "http://xmlns.com/foaf/");
        try {
            p.add(p2);
            Assert.fail("Should have failed because ex is assigned twice");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void testMergePrefixesWithConflictButSameNamespace() {
        p.add("ex", "http://example.com/");
        p.add(Prefixes.create1("ex", "http://example.com/"));
        Set<String> prefixes = p.allPrefixes();
        Assert.assertTrue(prefixes.contains("ex"));
        Assert.assertEquals(1, prefixes.size());
    }

    @Test
    public void testCreateSubset() {
        p.add("ex", "http://example.com/");
        p.add("foaf", "http://xmlns.com/foaf/");
        Prefixes subset = p.createSubset("ex");
        Assert.assertEquals(1, subset.allPrefixes().size());
        Assert.assertTrue(subset.hasPrefix("ex"));
    }

    @Test
    public void testCreateSubsetWithUndefinedPrefixThrowsException() {
        try {
            p.createSubset("ex");
            Assert.fail("Should have failed, p has no mapping for ex");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testAsMapEmpty() {
        Assert.assertTrue(p.asMap().isEmpty());
    }

    @Test
    public void testAsMapIsUnmodifiable() {
        try {
            p.asMap().put("ex", "http://example.com/");
            Assert.fail("Should have failed, result of asMap() is supposed to be unmodifiable");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    @Test
    public void testAddVolatile() {
        p.addVolatile("ex", "http://example.com/");
        Assert.assertTrue(p.allPrefixes().contains("ex"));
        Assert.assertEquals("http://example.com/", p.getNamespaceIRIFor("ex"));
    }


    @Test
    public void testAddVolatileNeverFails() {
        p.add("ex", "http://example.com/");
        p.addVolatile("ex", "http://other.example.com/");
        p.addVolatile("foaf", "http://xmlns.com/foaf/");
        p.addVolatile("foaf", "http://foaf.example.com/");
        Assert.assertEquals(2, p.allPrefixes().size());
        Assert.assertTrue(p.hasPrefix("foaf"));
        Assert.assertTrue(p.hasPrefix("ex"));
    }

    @Test
    public void testRemoveVolatilePrefix() {
        p.addVolatile("ex", "http://example.com/");
        p.removePrefix("ex");
        Assert.assertTrue(p.isEmpty());
        Assert.assertFalse(p.isVolatile("ex"));
    }

    @Test
    public void testIsVolatile() {
        p.add("ex", "http://example.com/");
        p.addVolatile("foaf", "http://xmlns.com/foaf/");
        Assert.assertTrue(p.isVolatile("foaf"));
        Assert.assertFalse(p.isVolatile("ex"));
    }

    @Test
    public void testUndefinedPrefixIsNotVolatile() {
        Assert.assertFalse(p.isVolatile("ex"));
    }

    @Test
    public void testAddVolatileDoesNotOverwriteHardMapping() {
        p.add("ex", "http://example.com/");
        p.addVolatile("ex", "http://other.example.com/");
        Assert.assertEquals("http://example.com/", p.getNamespaceIRIFor("ex"));
        Assert.assertFalse(p.isVolatile("ex"));
    }

    @Test
    public void testAddVolatileDoesNotOverwriteVolatileMapping() {
        p.addVolatile("ex", "http://example.com/");
        p.addVolatile("ex", "http://other.example.com/");
        Assert.assertEquals("http://example.com/", p.getNamespaceIRIFor("ex"));
        Assert.assertTrue(p.isVolatile("ex"));
    }

    @Test
    public void testAddHardOverwritesVolatileMapping() {
        p.addVolatile("ex", "http://other.example.com/");
        p.add("ex", "http://example.com/");
        Assert.assertEquals("http://example.com/", p.getNamespaceIRIFor("ex"));
        Assert.assertFalse(p.isVolatile("ex"));
    }

    @Test
    public void testMergeWithVolatile() {
        p.add("a", "http://p1.example.com/");
        p.addVolatile("b", "http://p2.example.com/");
        p.addVolatile("c", "http://p3.example.com/");
        p.addVolatile("d", "http://p4.example.com/");
        Prefixes q = new Prefixes();
        q.addVolatile("a", "http://q1.example.com/");
        p.add("b", "http://q2.example.com/");
        p.addVolatile("c", "http://q3.example.com/");
        p.addVolatile("e", "http://q5.example.com/");
        p.add(q);
        Assert.assertEquals(new HashSet<String>(Arrays.asList("a", "b", "c", "d", "e")), p.allPrefixes());
        Assert.assertEquals("http://p1.example.com/", p.getNamespaceIRIFor("a"));
        Assert.assertEquals("http://q2.example.com/", p.getNamespaceIRIFor("b"));
        Assert.assertEquals("http://p3.example.com/", p.getNamespaceIRIFor("c"));
        Assert.assertEquals("http://p4.example.com/", p.getNamespaceIRIFor("d"));
        Assert.assertEquals("http://q5.example.com/", p.getNamespaceIRIFor("e"));
    }

    @Test
    public void testAddPrefixesAsVolatile() {
        p.addVolatile("ex", "http://example.com/");
        Prefixes q = new Prefixes();
        q.add("ex", "http://other.example.com/");
        p.addVolatile(q);
        Assert.assertEquals("http://example.com/", p.getNamespaceIRIFor("ex"));
    }

    @Test
    public void testIncompatiblePrefixesInMergeAreDetected() {
        Prefixes p1 = PopularPrefixes.createSubset("rdf");
        Prefixes p2 = Prefixes.create1("rdf", "http://example.com/rdf#");
        try {
            p1.add(p2);
            Assert.fail("Should fail because of different mappings for rdf prefix");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void testNewPrefixesFromOtherPrefixesAreIndependent() {
        Prefixes p2 = new Prefixes(p);
        p2.add("ex", "http://example.org/");
        Assert.assertTrue(p.isEmpty());
    }

}
