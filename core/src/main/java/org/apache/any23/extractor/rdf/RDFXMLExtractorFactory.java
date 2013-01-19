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
public class RDFXMLExtractorFactory extends SimpleExtractorFactory<RDFXMLExtractor> implements
        ExtractorFactory<RDFXMLExtractor> {

    public static final String NAME = "rdf-xml";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new RDFXMLExtractorFactory();
    
    public RDFXMLExtractorFactory() {
        super(
                RDFXMLExtractorFactory.NAME, 
                RDFXMLExtractorFactory.PREFIXES,
                Arrays.asList(
                        "application/rdf+xml",
                        "text/rdf",
                        "text/rdf+xml",
                        "application/rdf"
                        // "application/xml;q=0.2",
                        // "text/xml;q=0.2"
                ),
                "example-rdfxml.rdf");
    }
    
    @Override
    public RDFXMLExtractor createExtractor() {
        return new RDFXMLExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
