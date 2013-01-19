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
public class HTMLMetaExtractorFactory extends SimpleExtractorFactory<HTMLMetaExtractor> implements
        ExtractorFactory<HTMLMetaExtractor> {

    public static final String NAME = "html-head-meta";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("sindice");

    private static final ExtractorDescription descriptionInstance = new HTMLMetaExtractorFactory();
    
    public HTMLMetaExtractorFactory() {
        super(
                HTMLMetaExtractorFactory.NAME, 
                HTMLMetaExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                "example-meta.html");
    }
    
    @Override
    public HTMLMetaExtractor createExtractor() {
        return new HTMLMetaExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
