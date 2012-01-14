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

package org.deri.any23.vocab;


import org.deri.any23.rdf.RDFUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test case for {@link Vocabulary} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class VocabularyTest {

    private static final String namespace = "http://test/vocab#";

    private Vocabulary target;

    @Before
    public void setUp() {
        target = new TargetVocabulary();
    }

    @After
    public void tearDown() {
        target = null;
    }

    @Test
    public void testGetProperties() {
        final URI[] props = target.getProperties();
        Assert.assertEquals(3, props.length);
        final List<URI> propsList = new ArrayList<URI>(Arrays.asList(props));
        Assert.assertTrue(propsList.contains( RDFUtils.uri("http://test/vocab#prop1")) );
        Assert.assertTrue(propsList.contains( RDFUtils.uri("http://test/vocab#prop2")) );
        Assert.assertTrue(propsList.contains( RDFUtils.uri("http://test/vocab#prop3")) );
    }

    @Test
    public void testGetClasses() {
        final URI[] classes = target.getClasses();
        Assert.assertEquals(3, classes.length);
        final List<URI> propsList = new ArrayList<URI>(Arrays.asList(classes));
        Assert.assertTrue(propsList.contains( RDFUtils.uri("http://test/vocab#Class1")) );
        Assert.assertTrue(propsList.contains( RDFUtils.uri("http://test/vocab#Class2")) );
        Assert.assertTrue(propsList.contains( RDFUtils.uri("http://test/vocab#Class3")) );
    }
    
    @Test
    public void testGetComments() {
        Assert.assertEquals( "Comment class 1.", target.getCommentFor(RDFUtils.uri("http://test/vocab#Class1")) );
        Assert.assertEquals( "Comment class 2.", target.getCommentFor(RDFUtils.uri("http://test/vocab#Class2")) );
        Assert.assertEquals( "Comment prop 1." , target.getCommentFor(RDFUtils.uri("http://test/vocab#prop1")) );
        Assert.assertEquals( "Comment prop 2." , target.getCommentFor(RDFUtils.uri("http://test/vocab#prop2")) );
        Assert.assertEquals(4, target.getComments().size());
    }

    /**
     * Target test class.
     */
    class TargetVocabulary extends Vocabulary {

        @Comment("Comment prop 1.")
        public final URI property1 = createProperty(namespace, "prop1");
        @Comment("Comment prop 2.")
        public final URI property2 = createProperty(namespace, "prop2");

        public final URI property3 = createProperty(namespace, "prop3");

        @Comment("Comment class 1.")
        public final URI class1 = createClass(namespace, "Class1");
        @Comment("Comment class 2.")
        public final URI class2 = createClass(namespace, "Class2");

        public final URI class3 = createClass(namespace, "Class3");

        /**
         * Constructor.
         */
        public TargetVocabulary() {
            super(namespace);
        }

    }

}
