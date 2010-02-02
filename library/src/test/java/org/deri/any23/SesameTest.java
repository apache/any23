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
