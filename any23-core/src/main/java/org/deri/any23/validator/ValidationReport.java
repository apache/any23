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

import org.w3c.dom.Node;

/**
 * The report interface is used to generate diagnostics about validation.
 *
 * @see org.deri.any23.validator.Rule
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface ValidationReport {

    /**
     * Defines the different issue levels.
     */
    enum IssueLevel {
        error,
        warning,
        info
    }

    /**
     * @return the number of detected issues.
     */
    int getNumberOfIssues();

    /**
     * Reports an issue detected on a specified node.
     *
     * @param issueLevel
     * @param message
     * @param n
     */
    void reportIssue(IssueLevel issueLevel, String message, Node n);

    /**
     * Reports a detected issue.
     *
     * @param issueLevel
     * @param message
     */
    void reportIssue(IssueLevel issueLevel, String message);

    /**
     * Traces that a rule has been applied.
     * 
     * @param r
     */
    void traceRuleActivation(Rule r);

    /**
     * Reports an error occurred while executing a {@link org.deri.any23.validator.Rule}. 
     *
     * @param r
     * @param e
     * @param msg
     */
    void reportRuleError(Rule r, Exception e, String msg);

    /**
     * Reports an error occurred while executing a {@link org.deri.any23.validator.Fix}.
     * 
     * @param r
     * @param e
     * @param msg
     */
    void reportFixError(Fix r, Exception e, String msg);

}
