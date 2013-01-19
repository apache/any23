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
public class LicenseExtractorFactory extends SimpleExtractorFactory<LicenseExtractor> implements
        ExtractorFactory<LicenseExtractor> {

    public static final String NAME = "html-mf-license";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("xhtml");

    private static final ExtractorDescription descriptionInstance = new LicenseExtractorFactory();
    
    public LicenseExtractorFactory() {
        super(
                LicenseExtractorFactory.NAME, 
                LicenseExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.01", "application/xhtml+xml;q=0.01"),
                "example-mf-license.html");
    }
    
    @Override
    public LicenseExtractor createExtractor() {
        return new LicenseExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
