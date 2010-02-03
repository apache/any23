package org.deri.any23.source;

import java.io.IOException;
import java.io.InputStream;

/**
 * A source of input streams. Mostly intended for
 * situations where opening of an input stream is
 * to be delayed.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface DocumentSource {
    
    InputStream openInputStream() throws IOException;

    public String getContentType();

    public long getContentLength();

    /**
     * @return the actual, final, canonical URI if redirects occur.
     */
    public String getDocumentURI();

    /**
     * A value of <tt>false</tt> indicates that the document
     * resides remotely, and that multiple successive accesses
     * to it should be avoided by copying it to local storage.
     * This can also be used for sources that do not support
     * multiple calls to {@link #openInputStream()}.
     */
    public boolean isLocal();
}
