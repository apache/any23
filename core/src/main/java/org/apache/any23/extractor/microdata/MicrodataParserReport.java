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

package org.apache.any23.extractor.microdata;

/**
 * This class describes the report of the {@link MicrodataParser}.
 * Such report contains the detected {@link ItemScope}s and errors.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParserReport {

    private static final MicrodataParserException[] NO_ERRORS = new MicrodataParserException[0];

    private final ItemScope[] detectedItemScopes;

    private final MicrodataParserException[] errors;

    public MicrodataParserReport(ItemScope[] detectedItemScopes, MicrodataParserException[] errors) {
        if(detectedItemScopes == null) {
            throw new NullPointerException("detected item scopes list cannot be null.");
        }
        this.detectedItemScopes = detectedItemScopes;
        this.errors = errors == null ? NO_ERRORS : errors;
    }

    public MicrodataParserReport(ItemScope[] detectedItemScopes) {
        this(detectedItemScopes, null);
    }

    public ItemScope[] getDetectedItemScopes() {
        return detectedItemScopes;
    }

    public MicrodataParserException[] getErrors() {
        return errors;
    }

}
