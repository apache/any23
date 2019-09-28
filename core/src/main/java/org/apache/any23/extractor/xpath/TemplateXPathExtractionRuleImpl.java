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
import org.apache.any23.extractor.html.DomUtils;
import org.eclipse.rdf4j.model.IRI;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link XPathExtractionRule}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateXPathExtractionRuleImpl implements TemplateXPathExtractionRule {

    private final String name;

    private final String uriRegex;

    private final Pattern uriRegexPattern;

    private final List<Variable> variables;

    private final List<QuadTemplate> templates;

    public TemplateXPathExtractionRuleImpl(String name, String uriRegex) {
        if(name == null) {
            throw new NullPointerException("The rule name cannot be null.");
        }

        this.name = name;
        this.uriRegex = uriRegex;

        try {
            uriRegexPattern = uriRegex != null ? Pattern.compile(uriRegex) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value for uriRegex.", e);
        }
        variables = new ArrayList<Variable>();
        templates = new ArrayList<QuadTemplate>();
    }

    /**
     * @return the regex pattern filtering the template pages.
     */
    public String getUriRegex() {
        return uriRegex;
    }

    public void add(Variable variable) {
        checkVariableNameNotDeclared(variable.getName());
        variables.add(variable);
    }

    public boolean remove(Variable variable) {
        return variables.remove(variable);
    }

    public void add(QuadTemplate template) {
        checkTemplateVariablesDeclared(template);
        templates.add(template);
    }

    public boolean remove(QuadTemplate template) {
        return templates.remove(template);
    }

    public String getName() {
        return name;
    }

    public boolean acceptIRI(IRI uri) {
        if(uriRegexPattern == null) {
            return true;
        }
        return uriRegexPattern.matcher(uri.stringValue()).find();
    }

    public void process(Document in, ExtractionResult er) {
        final Map<String,String> varValues = new HashMap<String, String>();
        String value;
        for(Variable variable : variables) {
            value = DomUtils.find(in, variable.getxPath().toUpperCase(Locale.ROOT));
            varValues.put(variable.getName(), value);
        }

        for(QuadTemplate template : templates) {
            template.printOut(er, varValues);
        }
    }

    private boolean variableNameDeclared(String varName) {
        for(Variable variable : variables) {
            if(variable.getName().equals(varName)) {
                return true;
            }
        }
        return false;
    }

    private void checkVariableNameDeclared(String varName) {
        if (!variableNameDeclared(varName)) {
            throw new IllegalArgumentException(
                    String.format(Locale.ROOT, "A variable with name '%s' was not declared.", varName)
            );
        }
    }

    private void checkVariableNameNotDeclared(String varName) {
        if (variableNameDeclared(varName)) {
            throw new IllegalArgumentException(
                    String.format(Locale.ROOT, "A variable with name '%s' is already declared.", varName)
            );
        }
    }

    private void checkTemplateVariablesDeclared(QuadTemplate template) {
        if( template.getSubject().isVar()   ) checkVariableNameDeclared( template.getSubject().getInternalValue() );
        if( template.getPredicate().isVar() ) checkVariableNameDeclared( template.getPredicate().getInternalValue() );
        if( template.getObject().isVar()    ) checkVariableNameDeclared( template.getObject().getInternalValue() );
        if( template.getGraph() != null && template.getGraph().isVar() ) {
            checkVariableNameDeclared( template.getGraph().getInternalValue() );
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append("name: ").append(name).append('\n');
        sb.append("pattern: '").append(uriRegex).append("'").append('\n');

        sb.append("variables {\n");
        for (Variable variable : variables) {
            sb.append(variable.getName()).append(":").append(variable.getxPath()).append('\n');
        }
        sb.append("}\n");

        sb.append("templates {\n");
        for (QuadTemplate template : templates) {
            sb.append(template).append('\n');
        }
        sb.append("}\n");
        return sb.toString();
    }
}
