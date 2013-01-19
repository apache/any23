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
public class TurtleHTMLExtractorFactory extends SimpleExtractorFactory<TurtleHTMLExtractor> implements
        ExtractorFactory<TurtleHTMLExtractor> {

    public static final String NAME = "html-script-turtle";
    
    public static final Prefixes PREFIXES = PopularPrefixes.get();

    private static final ExtractorDescription descriptionInstance = new TurtleHTMLExtractorFactory();
    
    public TurtleHTMLExtractorFactory() {
        super(
                TurtleHTMLExtractorFactory.NAME, 
                TurtleHTMLExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                "example-script-turtle.html");
    }
    
    @Override
    public TurtleHTMLExtractor createExtractor() {
        return new TurtleHTMLExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
