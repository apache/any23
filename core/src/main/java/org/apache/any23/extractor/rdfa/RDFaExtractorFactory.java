/**
 * 
 */
package org.apache.any23.extractor.rdfa;

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
public class RDFaExtractorFactory extends SimpleExtractorFactory<RDFaExtractor> implements
        ExtractorFactory<RDFaExtractor> {

    public static final String NAME = "html-rdfa";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new RDFaExtractorFactory();
    
    public RDFaExtractorFactory() {
        super(
                RDFaExtractorFactory.NAME, 
                RDFaExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.3", "application/xhtml+xml;q=0.3"),
                null);
    }
    
    @Override
    public RDFaExtractor createExtractor() {
        return new RDFaExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
