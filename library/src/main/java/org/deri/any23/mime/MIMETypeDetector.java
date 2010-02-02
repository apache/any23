package org.deri.any23.mime;

import java.io.InputStream;

/**
 * This detector is able to estimate the <code>MIME</code> type of
 * some given raw data. 
 */
public interface MIMETypeDetector {

    /**
     * Estimates the <code>MIME</code> type of the content of input file.
     *
     * @param fileName name of the file.
     * @param input content of the file.
     * @param mimeTypeFromMetadata mimetype declared in metadata.
     * @return the supposed mime type or <code>null</code> if nothing appropriate found.
     */
    public MIMEType guessMIMEType(String fileName, InputStream input, MIMEType mimeTypeFromMetadata);

    /**
     * The minimum buffer size expected from the detected to work properly.
     *
     * @return a non negative value.
     */
    //TODO: low - it seems to be unnecessary, remove it.
    public int requiredBufferSize();
}
