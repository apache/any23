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
public class XFNExtractorFactory extends SimpleExtractorFactory<XFNExtractor> implements
        ExtractorFactory<XFNExtractor> {

    public static final String NAME = "html-mf-xfn";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "foaf", "xfn");

    private static final ExtractorDescription descriptionInstance = new XFNExtractorFactory();
    
    public XFNExtractorFactory() {
        super(
                XFNExtractorFactory.NAME, 
                XFNExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-xfn.html");
    }
    
    @Override
    public XFNExtractor createExtractor() {
        return new XFNExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
