/**
 * 
 */
package org.apache.any23.extractor.rdf;

import java.util.Arrays;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.Prefixes;
import org.kohsuke.MetaInfServices;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
@MetaInfServices(ExtractorFactory.class)
public class TurtleExtractorFactory extends SimpleExtractorFactory<TurtleExtractor> implements
        ExtractorFactory<TurtleExtractor> {

    public static final String NAME = "rdf-turtle";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new TurtleExtractorFactory();
    
    public TurtleExtractorFactory() {
        super(
                TurtleExtractorFactory.NAME, 
                TurtleExtractorFactory.PREFIXES,
                Arrays.asList(
                        "text/rdf+n3",
                        "text/n3",
                        "application/n3",
                        "application/x-turtle",
                        "application/turtle",
                        "text/turtle"
                ),
                "example-turtle.ttl");
    }
    
    @Override
    public TurtleExtractor createExtractor() {
        return new TurtleExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
