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
public class RDFa11ExtractorFactory extends SimpleExtractorFactory<RDFa11Extractor> implements
        ExtractorFactory<RDFa11Extractor> {

    public static final String NAME = "html-rdfa11";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new RDFa11ExtractorFactory();
    
    public RDFa11ExtractorFactory() {
        super(
                RDFa11ExtractorFactory.NAME, 
                RDFa11ExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.3", "application/xhtml+xml;q=0.3"),
                "example-rdfa11.html");
    }
    
    @Override
    public RDFa11Extractor createExtractor() {
        return new RDFa11Extractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
