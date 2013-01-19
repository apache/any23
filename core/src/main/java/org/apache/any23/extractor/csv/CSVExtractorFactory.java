/**
 * 
 */
package org.apache.any23.extractor.csv;

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
public class CSVExtractorFactory extends SimpleExtractorFactory<CSVExtractor> implements
        ExtractorFactory<CSVExtractor> {

    public static final String NAME = "csv";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new CSVExtractorFactory();
    
    public CSVExtractorFactory() {
        super(
                CSVExtractorFactory.NAME, 
                CSVExtractorFactory.PREFIXES,
                Arrays.asList(
                        "text/csv;q=0.1"
                ),
                "example-csv.csv");
    }
    
    @Override
    public CSVExtractor createExtractor() {
        return new CSVExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
