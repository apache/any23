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
public class GeoExtractorFactory extends SimpleExtractorFactory<GeoExtractor> implements
        ExtractorFactory<GeoExtractor> {

    public static final String NAME = "html-mf-geo";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "vcard");

    private static final ExtractorDescription descriptionInstance = new GeoExtractorFactory();
    
    public GeoExtractorFactory() {
        super(
                GeoExtractorFactory.NAME, 
                GeoExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-geo.html");
    }
    
    @Override
    public GeoExtractor createExtractor() {
        return new GeoExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
