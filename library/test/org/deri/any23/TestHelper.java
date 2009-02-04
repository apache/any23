package org.deri.any23;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDF;

public class TestHelper {
	private final static PrefixMapping prefixes = 
		new PrefixMappingImpl()
				.setNsPrefixes(PrefixMapping.Standard)
				.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	
	public static Node toRDF(String s) {
		if ("a".equals(s)) return RDF.Nodes.type;
		if (s.matches("[a-z0-9]+:.*")) {
			return Node.createURI(prefixes.expandPrefix(s));
		}
		return Node.createLiteral(s);
	}
	
	public static Triple toTriple(String s, String p, String o) {
		return Triple.create(toRDF(s), toRDF(p), toRDF(o));
	}
}
