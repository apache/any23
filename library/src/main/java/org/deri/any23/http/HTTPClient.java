package org.deri.any23.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for opening an {@link InputStream} on an
 * HTTP URI.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface HTTPClient {

    public abstract void init(String userAgent, String acceptHeader);

    // Will follow redirects
    public abstract InputStream openInputStream(String uri) throws IOException;

    /**
     * Release all static resources help by the instance. Call this
     * method only if you are sure you will not use it again in your
     * application, like for example when shutting down a servlet
     * context.
     */
    public abstract void close();

    /**
     * The value of the Content-Type header reported by the server.
     * Can be <tt>null</tt>.
     */
    public abstract String getContentType();

    public abstract long getContentLength();

    /**
     * Returns the actual URI from which the document was fetched.
     * This might differ from the URI passed to openInputStream()
     * if a redirect was performed. A return value of <tt>null</tt>
     * means that the URI is unchanged and the original URI was used.
     */
    public abstract String getActualDocumentURI();
}