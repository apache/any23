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
public class HeadLinkExtractorFactory extends SimpleExtractorFactory<HeadLinkExtractor> implements
        ExtractorFactory<HeadLinkExtractor> {

    public static final String NAME = "html-head-links";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("xhtml", "dcterms");

    private static final ExtractorDescription descriptionInstance = new HeadLinkExtractorFactory();
    
    public HeadLinkExtractorFactory() {
        super(
                HeadLinkExtractorFactory.NAME, 
                HeadLinkExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.05", "application/xhtml+xml;q=0.05"),
                "example-head-link.html");
    }
    
    @Override
    public HeadLinkExtractor createExtractor() {
        return new HeadLinkExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
