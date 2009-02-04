package org.deri.any23.stream;

import java.io.InputStream;

/**
 * A cache that allows opening several {@link InputStream}s
 * all accessing the content of the same input stream.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface InputStreamCache {
	InputStreamOpener cache(InputStreamOpener in);
}
