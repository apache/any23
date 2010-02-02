package org.deri.any23;

import org.deri.any23.rdf.PopularPrefixes;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

public class Helper {

    public static URI uri(String uri) {
        return ValueFactoryImpl.getInstance().createURI(uri);
    }

    public static Literal literal(String s) {
        return ValueFactoryImpl.getInstance().createLiteral(s);
    }

    public static Statement triple(Resource s, URI p, Value o) {
        return ValueFactoryImpl.getInstance().createStatement(s, p, o);
    }

    public static Value toRDF(String s) {
        if ("a".equals(s)) return RDF.TYPE;
        if (s.matches("[a-z0-9]+:.*")) {
            return PopularPrefixes.get().expand(s);
        }
        return ValueFactoryImpl.getInstance().createLiteral(s);
    }

    public static Statement toTriple(String s, String p, String o) {
        return ValueFactoryImpl.getInstance().createStatement((Resource) toRDF(s), (URI) toRDF(p), toRDF(o));
    }
}
