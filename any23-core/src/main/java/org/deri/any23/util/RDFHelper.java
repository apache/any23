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

package org.deri.any23.util;

import org.deri.any23.rdf.PopularPrefixes;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDF helper class.
 */
public class RDFHelper {

    private RDFHelper(){}

    public static URI uri(String uri) {
        return ValueFactoryImpl.getInstance().createURI(uri);
    }

    public static Literal literal(String s) {
        return ValueFactoryImpl.getInstance().createLiteral(s);
    }

    public static Literal literal(String s, String l) {
        return ValueFactoryImpl.getInstance().createLiteral(s, l);
    }

    public static Literal literal(String s, URI datatype) {
        return ValueFactoryImpl.getInstance().createLiteral(s, datatype);
    }

    public static BNode getBNode(String id) {
        return ValueFactoryImpl.getInstance().createBNode(
            "node" + MathUtils.md5(id)
        );
    }

    public static Statement triple(Resource s, URI p, Value o) {
        return ValueFactoryImpl.getInstance().createStatement(s, p, o);
    }

    public static Statement quad(Resource s, URI p, Value o, Resource g) {
        return ValueFactoryImpl.getInstance().createStatement(s, p, o, g);
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

    public static String toXSDDateTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String s = simpleDateFormat.format(date);
        StringBuilder sb = new StringBuilder(s);
        sb.insert(22, ':');
        return sb.toString();
    }
}
