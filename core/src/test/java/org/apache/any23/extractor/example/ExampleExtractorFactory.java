/**
 * 
 */
package org.apache.any23.extractor.example;

import java.util.Collections;

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
// NOTE: Not enabling this in META-INF/services
//@MetaInfServices(ExtractorFactory.class)
public class ExampleExtractorFactory extends SimpleExtractorFactory<ExampleExtractor> implements
        ExtractorFactory<ExampleExtractor> {

    public static final String NAME = "example";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "foaf");

    private static final ExtractorDescription descriptionInstance = new ExampleExtractorFactory();
    
    public ExampleExtractorFactory() {
        super(
                ExampleExtractorFactory.NAME, 
                ExampleExtractorFactory.PREFIXES,
                Collections.singleton("*/*;q=0.01"),
                "http://example.com/");
    }
    
    @Override
    public ExampleExtractor createExtractor() {
        return new ExampleExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
