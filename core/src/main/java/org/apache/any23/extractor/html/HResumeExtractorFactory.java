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
public class HResumeExtractorFactory extends SimpleExtractorFactory<HResumeExtractor> implements
        ExtractorFactory<HResumeExtractor> {

    public static final String NAME = "html-mf-hresume";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "doac", "foaf");

    private static final ExtractorDescription descriptionInstance = new HResumeExtractorFactory();
    
    public HResumeExtractorFactory() {
        super(
                HResumeExtractorFactory.NAME, 
                HResumeExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-hresume.html");
    }
    
    @Override
    public HResumeExtractor createExtractor() {
        return new HResumeExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
