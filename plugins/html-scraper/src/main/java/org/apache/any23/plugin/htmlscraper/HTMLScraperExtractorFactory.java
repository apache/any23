/**
 * 
 */
package org.apache.any23.plugin.htmlscraper;

import java.util.Arrays;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.Prefixes;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class HTMLScraperExtractorFactory extends SimpleExtractorFactory<HTMLScraperExtractor> implements
        ExtractorFactory<HTMLScraperExtractor> {

    public static final String NAME = "html-scraper";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new HTMLScraperExtractorFactory();
    
    public HTMLScraperExtractorFactory() {
        super(
                HTMLScraperExtractorFactory.NAME, 
                HTMLScraperExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                null);
    }
    
    @Override
    public HTMLScraperExtractor createExtractor() {
        return new HTMLScraperExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
