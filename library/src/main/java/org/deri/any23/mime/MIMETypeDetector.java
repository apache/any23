package org.deri.any23.mime;

import java.io.InputStream;

// TODO return several MIME types with a confidence value?
public interface MIMETypeDetector {
    public MIMEType guessMIMEType(String fileName, InputStream input, MIMEType mimeTypeFromMetadata);

    // TODO: do we really need MIMETypeDetector.requiredBufferSize()?
    public int requiredBufferSize();
}
