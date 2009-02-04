package org.deri.any23.writer;

import java.io.OutputStream;

public class RDFXMLWriter extends TripleCollector implements FormatWriter {
	private final OutputStream out;
	
	public RDFXMLWriter(OutputStream out) {
		super();
		this.out = out;
	}

	public void close() {
		super.close();
		getModel().write(out, "RDF/XML");
	}
	
	public String getMIMEType() {
		return "application/rdf+xml";
	}
}
