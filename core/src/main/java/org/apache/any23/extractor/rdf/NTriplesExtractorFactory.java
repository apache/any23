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
public class NTriplesExtractorFactory extends SimpleExtractorFactory<NTriplesExtractor> implements
        ExtractorFactory<NTriplesExtractor> {

    public static final String NAME = "rdf-nt";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new NTriplesExtractorFactory();
    
    public NTriplesExtractorFactory() {
        super(
                NTriplesExtractorFactory.NAME, 
                NTriplesExtractorFactory.PREFIXES,
                Arrays.asList(
                        "text/nt;q=0.1",
                        "text/ntriples;q=0.1",
                        "text/plain;q=0.1"
                ),
                "example-ntriples.nt");
    }
    
    @Override
    public NTriplesExtractor createExtractor() {
        return new NTriplesExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
