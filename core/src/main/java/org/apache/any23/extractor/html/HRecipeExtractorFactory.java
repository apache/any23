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
public class HRecipeExtractorFactory extends SimpleExtractorFactory<HRecipeExtractor> implements
        ExtractorFactory<HRecipeExtractor> {

    public static final String NAME = "html-mf-hrecipe";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "hrecipe");

    private static final ExtractorDescription descriptionInstance = new HRecipeExtractorFactory();
    
    public HRecipeExtractorFactory() {
        super(
                HRecipeExtractorFactory.NAME, 
                HRecipeExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-hrecipe.html");
    }
    
    @Override
    public HRecipeExtractor createExtractor() {
        return new HRecipeExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
