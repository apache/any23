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

package org.apache.any23.extractor.calendar;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.Prefixes;

import java.util.Collections;

/**
 * @author Hans Brende (hansbrende@apache.org)
 */
public class ICalExtractorFactory extends SimpleExtractorFactory<ICalExtractor> {

    private static final String NAME = "ical";
    private static final Prefixes PREFIXES = null;
    private static final ExtractorDescription descriptionInstance = new ICalExtractorFactory();

    public ICalExtractorFactory() {
        super(NAME, PREFIXES, Collections.singletonList("text/calendar"), null);
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }

    @Override
    public ICalExtractor createExtractor() {
        return new ICalExtractor();
    }
}
