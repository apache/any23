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
package org.apache.any23.extractor.yaml;

import java.util.Arrays;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.Prefixes;

/**
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YAMLExtractorFactory extends SimpleExtractorFactory<YAMLExtractor>
		implements ExtractorFactory<YAMLExtractor>
{

	public static final String NAME = "yaml";

	public static final Prefixes prefixes = null;

	private static final ExtractorDescription descriptionInstance = new YAMLExtractorFactory();

	public YAMLExtractorFactory() {
		super(NAME, prefixes, Arrays.asList("text/x-yaml;q=0.5"), "example.yaml");
	}

	@Override
	public YAMLExtractor createExtractor() {
		return new YAMLExtractor();
	}

	public static ExtractorDescription getDescriptionInstance() {
		return descriptionInstance;
	}

}
