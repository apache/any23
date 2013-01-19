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
public class TitleExtractorFactory extends SimpleExtractorFactory<TitleExtractor> implements
        ExtractorFactory<TitleExtractor> {

    public static final String NAME = "html-head-title";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("dcterms");

    private static final ExtractorDescription descriptionInstance = new TitleExtractorFactory();
    
    public TitleExtractorFactory() {
        super(
                TitleExtractorFactory.NAME, 
                TitleExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                "example-title.html");
    }
    
    @Override
    public TitleExtractor createExtractor() {
        return new TitleExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
