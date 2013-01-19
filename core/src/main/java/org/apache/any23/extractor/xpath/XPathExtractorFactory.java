/**
 * 
 */
package org.apache.any23.extractor.xpath;

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
public class XPathExtractorFactory extends SimpleExtractorFactory<XPathExtractor> implements
        ExtractorFactory<XPathExtractor> {

    public static final String NAME = "html-xpath";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new XPathExtractorFactory();
    
    public XPathExtractorFactory() {
        super(
                XPathExtractorFactory.NAME, 
                XPathExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                null);
    }
    
    @Override
    public XPathExtractor createExtractor() {
        return new XPathExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
