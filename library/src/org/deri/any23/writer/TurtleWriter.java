package org.deri.any23.writer;

import java.io.OutputStream;

import com.hp.hpl.jena.vocabulary.RDF;

public class TurtleWriter extends TripleCollector implements FormatWriter {
	private final OutputStream out;
	private final boolean useN3;
	
	public TurtleWriter(OutputStream out) {
		this(out, false);
	}
	
	public TurtleWriter(OutputStream out, boolean useN3) {
		super();
		this.out = out;
		this.useN3 = useN3;
	}
	
	public void close() {
		super.close();
		if (getModel().qnameFor(RDF.type.getURI()) == null 
				&& getModel().getNsPrefixURI("rdf") == null) {
			// Jena's Turtle writer doesn't use the "a" syntax for rdf:type,
			// so let's add the rdf: prefix if it's not yet defined
			getModel().setNsPrefix("rdf", RDF.getURI());
		}
		getModel().write(out, "TURTLE");
	}
	
	public String getMIMEType() {
		return useN3 ? "text/rdf+n3;charset=utf-8" : "application/x-turtle";
	}
}
