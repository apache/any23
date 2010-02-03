/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Tests how Sesame's ValueFactory behaves with null arguments.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SesameTest {

    @Test
    public void testCreateURIWithNullArgumentThrowsNPE() {
        try {
            ValueFactoryImpl.getInstance().createURI(null);
            fail("should have thrown NPE");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testCreateLiteralWithNullArgumentWorks() {
        Literal l = ValueFactoryImpl.getInstance().createLiteral((String) null);
        assertNotNull(l);
        assertNull(l.stringValue());
        assertEquals(l, l);
    }

    @Test
    public void testCreateBNodeWithNullArgumentWorks() {
        BNode b = ValueFactoryImpl.getInstance().createBNode(null);
        assertNull(b.stringValue());
        assertEquals(b, b);
    }
    
}
