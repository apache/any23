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
public class AdrExtractorFactory extends SimpleExtractorFactory<AdrExtractor> implements
        ExtractorFactory<AdrExtractor> {

    public static final String NAME = "html-mf-adr";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "vcard");

    private static final ExtractorDescription descriptionInstance = new AdrExtractorFactory();
    
    public AdrExtractorFactory() {
        super(
                AdrExtractorFactory.NAME, 
                AdrExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-adr.html");
    }
    
    @Override
    public AdrExtractor createExtractor() {
        return new AdrExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
