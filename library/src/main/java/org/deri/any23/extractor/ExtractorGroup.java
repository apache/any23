package org.deri.any23.extractor;

import org.deri.any23.mime.MIMEType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * It simple models a group of {@link org.deri.any23.extractor.ExtractorFactory} providing
 * simple accessing methods.
 *
 */
public class ExtractorGroup implements Iterable<ExtractorFactory<?>> {

    // TODO: Add method getAcceptHeader(), probably move it from the Any23 class to here

    private final Collection<ExtractorFactory<?>> factories;

    public ExtractorGroup(Collection<ExtractorFactory<?>> factories) {
        this.factories = factories;
    }

    public boolean isEmpty() {
        return factories.isEmpty();
    }

    /**
     * Returns a {@link ExtractorGroup} with a set of {@link org.deri.any23.extractor.Extractor} able to
     * process the provided mime type
     * 
     * @param mimeType to perform the selection
     * @return an {@link org.deri.any23.extractor.ExtractorGroup} able to process the provided mime type
     */
    public ExtractorGroup filterByMIMEType(MIMEType mimeType) {
        // @@@ wildcards, q values
        Collection<ExtractorFactory<?>> matching = new ArrayList<ExtractorFactory<?>>();
        for (ExtractorFactory<?> factory : factories) {
            if (supportsAllContentTypes(factory) || supports(factory, mimeType)) {
                matching.add(factory);
            }
        }
        return new ExtractorGroup(matching);
    }

    public Iterator<ExtractorFactory<?>> iterator() {
        return factories.iterator();
    }

    /**
     * @return true if all the {@link org.deri.any23.extractor.Extractor} contained in the group
     * supports all the content types.
     */
    public boolean allExtractorsSupportAllContentTypes() {
        for (ExtractorFactory<?> factory : factories) {
            if (!supportsAllContentTypes(factory)) return false;
        }
        return true;
    }

    private boolean supportsAllContentTypes(ExtractorFactory<?> factory) {
        return factory.getSupportedMIMETypes().contains("*/*");
    }

    private boolean supports(ExtractorFactory<?> factory, MIMEType mimeType) {
        for (MIMEType supported : factory.getSupportedMIMETypes()) {
            if (supported.isAnyMajorType()) return true;
            if (supported.isAnySubtype() && supported.getMajorType().equals(mimeType.getMajorType())) return true;
            if (supported.getFullType().equals(mimeType.getFullType())) return true;
        }
        return false;
    }

}
