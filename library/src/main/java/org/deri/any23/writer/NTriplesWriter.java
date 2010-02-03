package org.deri.any23.writer;

import java.io.OutputStream;

/**
 * <i>N3</i> triples writer.
 */
public class NTriplesWriter extends RDFWriterTripleHandler implements FormatWriter {

    public NTriplesWriter(OutputStream out) {
        super(new org.openrdf.rio.ntriples.NTriplesWriter(out));
    }

    public String getMIMEType() {
        return "text/plain";
    }
    
}
