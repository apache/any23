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

package org.apache.any23;

import org.junit.Test;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Tests how Sesame's ValueFactory behaves with null arguments.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SesameTest {

    @Test
    public void testCreateIRIWithNullArgumentThrowsNPE() {
        try {
            SimpleValueFactory.getInstance().createIRI(null);
            fail("should have thrown NPE or assertion error.");
        } catch (AssertionError ae) {
            // espected when assertions are enabled.
        } catch (NullPointerException ex) {
            // expected without assertions.
        } catch (IllegalArgumentException ex) {
            // expected without assertions.
        }
    }

    @Test
    public void testCreateBNodeWithNullArgumentWorks() {
        BNode b = SimpleValueFactory.getInstance().createBNode(null);
        assertNull(b.stringValue());
        assertEquals(b, b);
    }

}
