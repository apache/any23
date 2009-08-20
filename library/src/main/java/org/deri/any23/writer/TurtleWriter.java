package org.deri.any23.writer;

import java.io.OutputStream;

public class TurtleWriter extends RDFWriterTripleHandler implements FormatWriter {
	private final boolean useN3;
	
	public TurtleWriter(OutputStream out) {
		this(out, false);
	}
	
	public TurtleWriter(OutputStream out, boolean useN3) {
		super(new org.openrdf.rio.turtle.TurtleWriter(out));
		this.useN3 = useN3;
	}
	
	public String getMIMEType() {
		return useN3 ? "text/rdf+n3;charset=utf-8" : "text/turtle";
	}
}
