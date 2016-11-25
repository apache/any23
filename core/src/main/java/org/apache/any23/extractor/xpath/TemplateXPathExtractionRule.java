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

package org.apache.any23.extractor.xpath;

/**
 * Defines an {@link XPathExtractionRule} able
 * to expand a {@link QuadTemplate} set based
 * on a given template.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateXPathExtractionRule extends XPathExtractionRule {

    /**
     * @return human readable description for this rule.
     */
    public String getName();

    /**
     * Adds a variable to the template.
     *
     * @param variable variable to be added.
     */
    public void add(Variable variable);

    /**
     * Removes a variable from the template.
     *
     * @param variable variable to be removed.
     * @return <i>true</i> if the <code>variable</code> argument was found.
     */
    public boolean remove(Variable variable);

    /**
     * Adds a {@link QuadTemplate} to the rule.
     *
     * @param template template instance to be added.
     */
    public void add(QuadTemplate template);

    /**
     * Removes a quad template from the rule.
     *
     * @param template template to be removed.
     * @return <code>true</code> if the <code>template</code>
     *         argument was found.
     */
    public boolean remove(QuadTemplate template);

}
