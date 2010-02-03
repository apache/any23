package org.deri.any23.writer;

/**
 * Base interface used for the definition of <i>formatted writers</i>.
 */
public interface FormatWriter extends TripleHandler {

    /**
     * The MIME type used by the writer.
     *
     * @return a MIME type.
     */
    String getMIMEType();
    
}
