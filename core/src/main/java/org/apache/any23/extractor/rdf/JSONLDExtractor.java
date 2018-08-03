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
import com.fasterxml.jackson.core.JsonParser;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.eclipse.rdf4j.rio.RDFParser;

import java.lang.reflect.Field;

/**
 * Concrete implementation of {@link org.apache.any23.extractor.Extractor.ContentExtractor}
 * handling <a href="http://www.w3.org/TR/json-ld/">JSON-LD</a> format.
 *
 */
public class JSONLDExtractor extends BaseRDFExtractor {

    static {
        //See https://issues.apache.org/jira/browse/ANY23-336
        try {
            //This field was introduced in jsonld-java version 0.12.0
            if ((Object)JsonUtils.JSONLD_JAVA_USER_AGENT instanceof Void) {
                throw new Error("This error will never be thrown.");
            }
        } catch (NoSuchFieldError th) {
            throw new AssertionError("You have an outdated version of jsonld-java on the classpath. " +
                    "Upgrade to at least version 0.12.0. See: https://issues.apache.org/jira/browse/ANY23-336", th);
        }

        JsonFactory JSON_FACTORY;
        try {
            Field field = JsonUtils.class.getDeclaredField("JSON_FACTORY");
            field.setAccessible(true);
            JSON_FACTORY = (JsonFactory)field.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_COMMENTS);
        JSON_FACTORY.disable(JsonParser.Feature.ALLOW_MISSING_VALUES); //handled by JsonCleaningInputStream
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        JSON_FACTORY.disable(JsonParser.Feature.ALLOW_TRAILING_COMMA); //handled by JsonCleaningInputStream
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        JSON_FACTORY.enable(JsonParser.Feature.IGNORE_UNDEFINED);
        JSON_FACTORY.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
        JSON_FACTORY.disable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    }


    public JSONLDExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public JSONLDExtractor() {
        this(false, false);
    }

    @Override
    public ExtractorDescription getDescription() {
        return JSONLDExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getJSONLDParser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }
}
