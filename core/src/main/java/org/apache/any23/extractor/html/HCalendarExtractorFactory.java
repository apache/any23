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
public class HCalendarExtractorFactory extends SimpleExtractorFactory<HCalendarExtractor> implements
        ExtractorFactory<HCalendarExtractor> {

    public static final String NAME = "html-mf-hcalendar";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "ical");

    private static final ExtractorDescription descriptionInstance = new HCalendarExtractorFactory();
    
    public HCalendarExtractorFactory() {
        super(
                HCalendarExtractorFactory.NAME, 
                HCalendarExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-hcalendar.html");
    }
    
    @Override
    public HCalendarExtractor createExtractor() {
        return new HCalendarExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
