package org.deri.any23.extractor;

import org.deri.any23.rdf.Prefixes;

/**
 *
 * It defines a minimal signature for an {@link org.deri.any23.extractor.Extractor} description
 *
 */
public interface ExtractorDescription {

    String getExtractorName();

    Prefixes getPrefixes();

}
