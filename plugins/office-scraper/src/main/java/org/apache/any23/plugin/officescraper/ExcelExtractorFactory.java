/**
 * 
 */
package org.apache.any23.plugin.officescraper;

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
public class ExcelExtractorFactory extends SimpleExtractorFactory<ExcelExtractor> implements
        ExtractorFactory<ExcelExtractor> {

    public static final String NAME = "excel";
    
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new ExcelExtractorFactory();
    
    public ExcelExtractorFactory() {
        super(
                ExcelExtractorFactory.NAME, 
                ExcelExtractorFactory.PREFIXES,
                Arrays.asList(
                        "application/vnd.ms-excel;q=0.1",
                        "application/msexcel;q=0.1",
                        "application/x-msexcel;q=0.1",
                        "application/x-ms-excel;q=0.1"
                ),
                null);
    }
    
    @Override
    public ExcelExtractor createExtractor() {
        return new ExcelExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
