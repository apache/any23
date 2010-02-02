package org.deri.any23.source;

import java.io.IOException;


/**
 * A factory that creates local copies of {@link DocumentSource}s.
 * The copies are usually identical in content, but stored
 * locally, and hence it is faster to access them repeatedly.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface LocalCopyFactory {
    DocumentSource createLocalCopy(DocumentSource in) throws IOException;
}
