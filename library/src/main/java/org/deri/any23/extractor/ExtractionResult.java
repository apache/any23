package org.deri.any23.extractor;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Interface defining the methods that a representation of an extraction result must have.
 */
public interface ExtractionResult {

    /**
     * Write a triple.
     *
     * @param s Subject
     * @param p Predicate
     * @param o Object
     *
     * parameters can be null, then the triple will be silently ignored.
     *
     */
    void writeTriple(Resource s, URI p, Value o);

    /**
     * Write a namespace
     * @param prefix the prefix of the namespace
     * @param uri the long URI identifying the namespace
     */
    void writeNamespace(String prefix, String uri);

    /**
     *
     * Close the result.
     * Extractors should close their results as soon as possible, but
     * don't have to, the environment will close any remaining ones.
     * Implementations should be robust against multiple close()
     * invocations.
     *
     */
    void close();

    ExtractionResult openSubResult(Object context);

}