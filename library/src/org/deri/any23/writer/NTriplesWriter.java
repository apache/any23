package org.deri.any23.writer;

import java.io.OutputStream;

public class NTriplesWriter extends TripleCollector implements FormatWriter {
	private final OutputStream out;
	
	public NTriplesWriter(OutputStream out) {
		super();
		this.out = out;
	}
	
	public void close() {
		super.close();
		getModel().write(out, "N-TRIPLE");
	}
	
	public String getMIMEType() {
		return "text/plain";
	}
}
