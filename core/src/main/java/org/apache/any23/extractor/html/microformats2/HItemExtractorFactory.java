package org.apache.any23.extractor.html.microformats2;

import java.util.Arrays;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.PopularPrefixes;
import org.apache.any23.rdf.Prefixes;

/**
 * @author Nisala Nirmana
 *
 */
public class HItemExtractorFactory extends SimpleExtractorFactory<HItemExtractor> implements
        ExtractorFactory<HItemExtractor> {

    public static final String NAME = "html-mf2-h-item";

    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "vcard");

    private static final ExtractorDescription descriptionInstance = new HItemExtractorFactory();

    public HItemExtractorFactory() {
        super(
                HItemExtractorFactory.NAME,
                HItemExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf2-h-item.html");
    }

    @Override
    public HItemExtractor createExtractor() {
        return new HItemExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}