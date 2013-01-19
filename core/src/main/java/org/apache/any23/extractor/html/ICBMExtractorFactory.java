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
public class ICBMExtractorFactory extends SimpleExtractorFactory<ICBMExtractor> implements
        ExtractorFactory<ICBMExtractor> {

    public static final String NAME = "html-head-icbm";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("geo", "rdf");

    private static final ExtractorDescription descriptionInstance = new ICBMExtractorFactory();
    
    public ICBMExtractorFactory() {
        super(
                ICBMExtractorFactory.NAME, 
                ICBMExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.01", "application/xhtml+xml;q=0.01"),
                "example-icbm.html");
    }
    
    @Override
    public ICBMExtractor createExtractor() {
        return new ICBMExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
