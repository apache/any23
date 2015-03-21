/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.plugin.officescraper;

import java.util.Arrays;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.Prefixes;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
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
                        "application/x-ms-excel;q=0.1",
                        "application/x-tika-ooxml;q=0.1",
                        "application/x-tika-msoffice;q=0.1",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;q=0.1"
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
