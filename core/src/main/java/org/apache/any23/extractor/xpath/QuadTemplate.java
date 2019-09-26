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

import org.apache.any23.extractor.ExtractionResult;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.util.Locale;
import java.util.Map;

/**
 * This class models a <i>NQuads</i> template,
 * that is a quadruple in which any component
 * can be a variable.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class QuadTemplate {

    private final TemplateSubject subject;

    private final TemplatePredicate predicate;

    private final TemplateObject object;

    private final TemplateGraph graph;

    /**
     * Constructor.
     *
     * @param subject not <code>null</code> subject template.
     * @param predicate not <code>null</code> predicate template.
     * @param object not <code>null</code> object template.
     * @param graph graph template, can be <code>null</code>.
     */
    public QuadTemplate(
            TemplateSubject subject, TemplatePredicate predicate, TemplateObject object, TemplateGraph graph
    ) {
        if(subject == null) {
            throw new NullPointerException("subject term cannot be null.");
        }
        if(predicate == null) {
            throw new NullPointerException("predicate term cannot be null.");
        }
        if(object == null) {
            throw new NullPointerException("object term cannot be null.");
        }

        this.subject   = subject;
        this.predicate = predicate;
        this.object    = object;
        this.graph     = graph;
    }

    /**
     * Constructor for template with no graph.
     *
     * @param subject a populated {@link org.apache.any23.extractor.xpath.TemplateSubject}
     * @param predicate a populated {@link org.apache.any23.extractor.xpath.TemplatePredicate}
     * @param object a populated {@link org.apache.any23.extractor.xpath.TemplateObject}
     */
    public QuadTemplate(TemplateSubject subject, TemplatePredicate predicate, TemplateObject object) {
        this(subject, predicate, object, null);
    }

    /**
     * @return the template subject.
     */
    public TemplateSubject getSubject() {
        return subject;
    }

    /**
     * @return the template predicate.
     */
    public TemplatePredicate getPredicate() {
        return predicate;
    }

    /**
     * @return the template object.
     */
    public TemplateObject getObject() {
        return object;
    }

    /**
     * @return the template graph, can be <code>null</code>.
     */
    public TemplateGraph getGraph() {
        return graph;
    }

    /**
     * Prints out this quad template in the given {@link org.apache.any23.extractor.ExtractionResult}, using
     * the passed <i>variableAssignment</i> to expand variables.
     *
     * @param er extraction result instance on which write the quad produced by this template.
     * @param variableAssignment the assignment used to expand variables.
     */
    public void printOut(ExtractionResult er, Map<String,String> variableAssignment) {
        final Resource s = subject.getValue(variableAssignment);
        final IRI p      = predicate.getValue(variableAssignment);
        @SuppressWarnings("unchecked")
        final Value o    = object.getValue(variableAssignment);
        if(graph != null) {
            final IRI g = graph.getValue(variableAssignment);
            er.writeTriple(s, p, o, g);
        } else {
            er.writeTriple(s, p, o);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s %s %s %s", subject, predicate, object, graph);
    }

}
