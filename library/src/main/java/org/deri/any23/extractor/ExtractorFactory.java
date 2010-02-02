package org.deri.any23.extractor;

import org.deri.any23.mime.MIMEType;

import java.util.Collection;


public interface ExtractorFactory<T extends Extractor<?>> extends ExtractorDescription {
    T createExtractor();

    // Supports wildcards, e.g. "*/*" for blind extractors that merely call a web service
    Collection<MIMEType> getSupportedMIMETypes();

    /**
     * An example input file for the extractor, to be used in auto-generated
     * documentation. For the {@link BlindExtractor}, this is an arbitrary URI.
     * For extractors that require content, it is the name of a file, relative
     * to the factory's class file's location, it will be opened using
     * factory.getClass().getResourceAsStream(filename). The example should be
     * a short file that produces characteristic output if sent through the
     * extractor. The file will be read as UTF-8, so it should either use that
     * encoding or avoid characters outside of the US-ASCII range.
     */
    String getExampleInput();
}
