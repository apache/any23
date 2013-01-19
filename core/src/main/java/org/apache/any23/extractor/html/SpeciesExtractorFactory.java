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
public class SpeciesExtractorFactory extends SimpleExtractorFactory<SpeciesExtractor> implements
        ExtractorFactory<SpeciesExtractor> {

    public static final String NAME = "html-mf-species";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "wo");

    private static final ExtractorDescription descriptionInstance = new SpeciesExtractorFactory();
    
    public SpeciesExtractorFactory() {
        super(
                SpeciesExtractorFactory.NAME, 
                SpeciesExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-species.html");
    }
    
    @Override
    public SpeciesExtractor createExtractor() {
        return new SpeciesExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
