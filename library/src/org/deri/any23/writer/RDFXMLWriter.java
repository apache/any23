package org.deri.any23.writer;

import java.io.OutputStream;

public class RDFXMLWriter extends RDFWriterTripleHandler implements FormatWriter {
	
	public RDFXMLWriter(OutputStream out) {
		super(new org.openrdf.rio.rdfxml.RDFXMLWriter(out));
	}

	public String getMIMEType() {
		return "application/rdf+xml";
	}
}
