/**
 * 
 */
package org.apache.any23.extractor.html;

import java.util.Arrays;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.PopularPrefixes;
import org.apache.any23.rdf.Prefixes;
import org.kohsuke.MetaInfServices;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
@MetaInfServices(ExtractorFactory.class)
public class HReviewExtractorFactory extends SimpleExtractorFactory<HReviewExtractor> implements
        ExtractorFactory<HReviewExtractor> {

    public static final String NAME = "html-mf-hreview";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "vcard", "rev");

    private static final ExtractorDescription descriptionInstance = new HReviewExtractorFactory();
    
    public HReviewExtractorFactory() {
        super(
                HReviewExtractorFactory.NAME, 
                HReviewExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-hreview.html");
    }
    
    @Override
    public HReviewExtractor createExtractor() {
        return new HReviewExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
