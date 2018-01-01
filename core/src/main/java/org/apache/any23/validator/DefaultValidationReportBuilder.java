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

package org.apache.any23.validator;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link ValidationReportBuilder}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class DefaultValidationReportBuilder implements ValidationReportBuilder {

    private List<ValidationReport.Issue> issues;
    private List<ValidationReport.RuleActivation> ruleActivations;
    private List<ValidationReport.Error> errors;

    public DefaultValidationReportBuilder() {
      //default constructor
    }

    public ValidationReport getReport() {
        return new DefaultValidationReport(
                issues == null ? Collections.<ValidationReport.Issue>emptyList() : issues,
                ruleActivations == null ? Collections.<ValidationReport.RuleActivation>emptyList() : ruleActivations,
                errors == null ? Collections.<ValidationReport.Error>emptyList() : errors 
        );
    }

    public void reportIssue(ValidationReport.IssueLevel issueLevel, String message, Node n) {
        if(issues == null) {
            issues = new ArrayList<>();
        }
        issues.add( new ValidationReport.Issue(issueLevel, message, n) );
    }

    public void reportIssue(ValidationReport.IssueLevel issueLevel, String message) {
        reportIssue(issueLevel, message, null);
    }

    public void traceRuleActivation(Rule r) {
        if(ruleActivations == null) {
            ruleActivations = new ArrayList<>();
        }
        ruleActivations.add( new ValidationReport.RuleActivation(r) );
    }

    public void reportRuleError(Rule r, Exception e, String msg) {
        if(errors == null) {
            errors = new ArrayList<>();
        }
        errors.add( new ValidationReport.RuleError(r, e, msg) );
    }

    public void reportFixError(Fix f, Exception e, String msg) {
        if(errors == null) {
            errors = new ArrayList<>();
        }
        errors.add( new ValidationReport.FixError(f, e, msg) );

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if(ruleActivations != null) {
            sb.append("Rules {\n");
            for(ValidationReport.RuleActivation ra :ruleActivations) {
                sb.append(ra).append('\n');
            }
            sb.append("}\n");
        }
        if(issues != null) {
            sb.append("Issues {\n");
            for(ValidationReport.Issue issue : issues) {
                sb.append( issue.toString() ).append('\n');
            }
            sb.append("}\n");
        }
        if (errors != null) {
            sb.append("Errors {\n");
            for (ValidationReport.Error error : errors) {
                sb.append( error.toString() ).append('\n');
            }
            sb.append("}\n");
        }
        return sb.toString();
    }

}
