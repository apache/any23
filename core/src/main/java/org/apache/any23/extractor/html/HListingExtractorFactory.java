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
public class HListingExtractorFactory extends SimpleExtractorFactory<HListingExtractor> implements
        ExtractorFactory<HListingExtractor> {

    public static final String NAME = "html-mf-hlisting";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "hlisting");

    private static final ExtractorDescription descriptionInstance = new HListingExtractorFactory();
    
    public HListingExtractorFactory() {
        super(
                HListingExtractorFactory.NAME, 
                HListingExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-hlisting.html");
    }
    
    @Override
    public HListingExtractor createExtractor() {
        return new HListingExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
