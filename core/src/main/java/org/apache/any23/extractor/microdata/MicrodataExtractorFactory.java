/**
 * 
 */
package org.apache.any23.extractor.microdata;

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
public class MicrodataExtractorFactory extends SimpleExtractorFactory<MicrodataExtractor> implements
        ExtractorFactory<MicrodataExtractor> {

    public static final String NAME = "html-microdata";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "doac", "foaf");

    private static final ExtractorDescription descriptionInstance = new MicrodataExtractorFactory();
    
    public MicrodataExtractorFactory() {
        super(
                MicrodataExtractorFactory.NAME, 
                MicrodataExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-microdata.html");
    }
    
    @Override
    public MicrodataExtractor createExtractor() {
        return new MicrodataExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
