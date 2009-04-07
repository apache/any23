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

	// Will follow redirects
	public abstract InputStream openInputStream(String uri) throws IOException;

	/**
	 * Release all static resources help by the instance. Call this
	 * method only if you are sure you will not use it again in your
	 * application, like for example when shutting down a servlet 
	 * context.
	 */
	public abstract void close();

}