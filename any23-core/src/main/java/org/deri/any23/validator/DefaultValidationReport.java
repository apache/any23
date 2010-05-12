/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.validator;

import org.deri.any23.extractor.html.DomUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link ValidationReport}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class DefaultValidationReport implements ValidationReport {

    private List<Issue> issues;

    private List<RuleActivation> ruleActivations;

    private List<Error> errors;

    public DefaultValidationReport() {}

    public int getNumberOfIssues() {
        return issues == null ? 0 : issues.size();
    }

    public void reportIssue(IssueLevel issueLevel, String message, Node n) {
        if(issues == null) {
            issues = new ArrayList<Issue>();
        }
        issues.add( new Issue(issueLevel, message, n) );
    }

    public void reportIssue(IssueLevel issueLevel, String message) {
        reportIssue(issueLevel, message, null);
    }

    public void traceRuleActivation(Rule r) {
        if(ruleActivations == null) {
            ruleActivations = new ArrayList<RuleActivation>();
        }
        ruleActivations.add( new RuleActivation(r) );
    }

    public void reportRuleError(Rule r, Exception e, String msg) {
        if(errors == null) {
            errors = new ArrayList<Error>();
        }
        errors.add( new RuleError(r, e, msg) );
    }

    public void reportFixError(Fix f, Exception e, String msg) {
        if(errors == null) {
            errors = new ArrayList<Error>();
        }
        errors.add( new FixError(f, e, msg) );

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if(ruleActivations != null) {
            sb.append("Rules {\n");
            for(RuleActivation ra :ruleActivations) {
                sb.append(ra).append('\n');
            }
            sb.append("}\n");
        }
        if(issues != null) {
            sb.append("Issues {\n");
            for(Issue issue : issues) {
                sb.append( issue.toString() ).append('\n');
            }
            sb.append("}\n");
        }
        if (errors != null) {
            sb.append("Errors {\n");
            for (Error error : errors) {
                sb.append( error.toString() ).append('\n');
            }
            sb.append("}\n");
        }
        return sb.toString();
    }

    class Issue {
        final IssueLevel level;
        final String message;
        final Node origin;

        public Issue(IssueLevel level, String message, Node origin) {
            this.level = level;
            this.message = message;
            this.origin = origin;
        }

        @Override
        public String toString() {
            return String.format(
                    "Issue %s '%s' %s",
                    level,
                    message,
                    DomUtils.getXPathForNode(origin)
            );
        }
    }

    class RuleActivation {
        private final String ruleStr;

        RuleActivation(Rule r) {
            ruleStr = r.getHRName();
        }
        @Override
         public String toString() {
            return ruleStr;
        }
    }

    abstract class Error {
        private final Exception cause;
        private final String message;

        public Error(Exception e, String msg) {
            cause   = e;
            message = msg;
        }

        @Override
        public String toString() {
            return String.format("%s %s %s", this.getClass().getName(), cause, message);
        }
    }

    class RuleError extends Error {
        private final Rule origin;

        RuleError(Rule r, Exception e, String msg) {
            super(e, msg);
            origin = r;
        }

        @Override
        public String toString() {
            return String.format("%s - %s", super.toString(), origin.getHRName());
        }
    }

    class FixError extends Error {
        private final Fix origin;

        FixError(Fix f, Exception e, String msg) {
             super(e, msg);
             origin = f;
        }

        @Override
        public String toString() {
            return String.format("%s - %s", super.toString(), origin.getHRName());
        }
    }
}
