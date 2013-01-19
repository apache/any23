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
public class TriXExtractorFactory extends SimpleExtractorFactory<TriXExtractor> implements
        ExtractorFactory<TriXExtractor> {

    public static final String NAME = "rdf-trix";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new TriXExtractorFactory();
    
    public TriXExtractorFactory() {
        super(
                TriXExtractorFactory.NAME, 
                TriXExtractorFactory.PREFIXES,
                Arrays.asList(
                        "application/trix"
                ),
                "example-trix.trx");
    }
    
    @Override
    public TriXExtractor createExtractor() {
        return new TriXExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
