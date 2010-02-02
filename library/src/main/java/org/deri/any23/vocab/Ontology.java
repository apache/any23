
package org.deri.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Base class for the definition of an ontology.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public abstract class Ontology {

    private static final ValueFactory factory = ValueFactoryImpl.getInstance();

    protected static URI createURI(String namespace, String localName) {
        return factory.createURI(namespace, localName);
    }

    protected static URI createURI(String localName) {
        return factory.createURI(localName);
    }


}
