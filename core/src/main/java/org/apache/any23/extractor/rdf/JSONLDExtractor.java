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

package org.apache.any23.extractor.rdf;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Concrete implementation of {@link org.apache.any23.extractor.Extractor.ContentExtractor}
 * handling <a href="http://www.w3.org/TR/json-ld/">JSON-LD</a> format.
 *
 */
public class JSONLDExtractor extends BaseRDFExtractor {

    private static final JsonFactory JSON_FACTORY = new JsonFactory(new ObjectMapper());

    static {
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
        JSON_FACTORY.disable(JsonParser.Feature.ALLOW_COMMENTS); //handled by JsonCleaningInputStream
        JSON_FACTORY.disable(JsonParser.Feature.ALLOW_MISSING_VALUES); //handled by JsonCleaningInputStream
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS);
        JSON_FACTORY.disable(JsonParser.Feature.ALLOW_SINGLE_QUOTES); //handled by JsonCleaningInputStream
        JSON_FACTORY.disable(JsonParser.Feature.ALLOW_TRAILING_COMMA); //handled by JsonCleaningInputStream
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        JSON_FACTORY.disable(JsonParser.Feature.ALLOW_YAML_COMMENTS); //handled by JsonCleaningInputStream
        JSON_FACTORY.enable(JsonParser.Feature.IGNORE_UNDEFINED);
        JSON_FACTORY.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
        JSON_FACTORY.disable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    }

    /**
     * @deprecated since 2.4. This extractor has never supported these settings. Use {@link #JSONLDExtractor()} instead.
     * @param verifyDataType has no effect
     * @param stopAtFirstError has no effect
     */
    @Deprecated
    public JSONLDExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public JSONLDExtractor() {
        super(false, false);
    }

    @Override
    public ExtractorDescription getDescription() {
        return JSONLDExtractorFactory.getDescriptionInstance();
    }

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, InputStream in, ExtractionResult extractionResult) throws IOException, ExtractionException {
        JSONLDJavaSink handler = new JSONLDJavaSink(extractionResult, new Any23ValueFactoryWrapper(
                SimpleValueFactory.getInstance(),
                extractionResult,
                extractionContext.getDefaultLanguage()
        ));

        JsonLdOptions options = new JsonLdOptions(extractionContext.getDocumentIRI().stringValue());
        options.useNamespaces = true;

        try {
            Object json = JsonUtils.fromJsonParser(JSON_FACTORY.createParser(new JsonCleaningInputStream(in)));
            JsonLdProcessor.toRDF(json, handler, options);
        } catch (JsonProcessingException e) {
            JsonLocation loc = e.getLocation();
            if (loc == null) {
                extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, e.getOriginalMessage(), -1L, -1L);
            } else {
                extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, e.getOriginalMessage(), loc.getLineNr(), loc.getColumnNr());
            }
        } catch (Exception e) {
            // ANY23-420: jsonld-java can sometimes throw IllegalArgumentException
            extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, toString(e), -1, -1);
        }
    }

    /* DEPRECATED METHODS */

    /**
     * @deprecated since 2.4. This extractor has never supported this setting. Do not use.
     * @param stopAtFirstError has no effect
     */
    @Deprecated
    @Override
    public void setStopAtFirstError(boolean stopAtFirstError) {
        super.setStopAtFirstError(stopAtFirstError);
    }

    /**
     * @deprecated since 2.4. This extractor has never supported this setting. Do not use.
     * @param verifyDataType has no effect
     */
    @Deprecated
    @Override
    public void setVerifyDataType(boolean verifyDataType) {
        super.setVerifyDataType(verifyDataType);
    }

    /**
     * @deprecated since 2.4. This extractor no longer wraps an RDF4J {@link RDFParser}. Do not use this method.
     * @param extractionContext the extraction context
     * @param extractionResult the extraction result
     * @return a {@link RDFParser}
     */
    @Deprecated
    @Override
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getJSONLDParser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }

}
