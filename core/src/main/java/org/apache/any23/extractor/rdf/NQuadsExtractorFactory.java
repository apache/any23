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
public class NQuadsExtractorFactory extends SimpleExtractorFactory<NQuadsExtractor> implements
        ExtractorFactory<NQuadsExtractor> {

    public static final String NAME = "rdf-nq";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new NQuadsExtractorFactory();
    
    public NQuadsExtractorFactory() {
        super(
                NQuadsExtractorFactory.NAME, 
                NQuadsExtractorFactory.PREFIXES,
                Arrays.asList(
                        "text/x-nquads;q=0.1",
                        "text/rdf+nq;q=0.1",
                        "text/nq;q=0.1",
                        "text/nquads;q=0.1",
                        "text/n-quads;q=0.1"
                ),
                "example-nquads.nq");
    }
    
    @Override
    public NQuadsExtractor createExtractor() {
        return new NQuadsExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
