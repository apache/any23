/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.any23.vocab;

import org.openrdf.model.URI;

/**
 * This vocabulary describes model of the yaml file.
 * 
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YAML extends Vocabulary {

	/*
	 * Namespace of YAML vocabulary
	 */
	public static final String NS = "http://yaml.org/spec/1.2/spec.html#";

	public static final String PREFIX = "yaml";

	public static final String ROOT = "Root";

	public static final String DOCUMENT = "Document";

	public static final String NODE = "Node";

	public static final String CONTAINS = "contains";

	private static final YAML _instance = new YAML();

	public YAML() {
		super(NS);
	}

	public static YAML getInstance() {
		return _instance;
	}

	/**
	 * The root node. Representation of the YAML file. NB: one file may contain more than one documents
	 * represented by nodes; e.g. <br/>
	 * <br/>
	 * <code>
	 * %YAML 1.2 
	 * --- 
	 * - data1 
	 * - data2 
	 * --- 
	 * - data3 
	 * </code> <br/>
	 * Contains two documents.
	 */
	public final URI root = createResource(ROOT);

	public final URI document = createResource(DOCUMENT);

	public final URI node = createResource(NODE);

	public final URI contains = createProperty(CONTAINS);

	public URI createResource(String localName) {
		return createProperty(NS, localName);
	}

	/**
	 * @param localName
	 *        name to assign to namespace.
	 * @return the new URI _instance.
	 */
	public URI createProperty(String localName) {
		return createProperty(NS, localName);
	}

}
