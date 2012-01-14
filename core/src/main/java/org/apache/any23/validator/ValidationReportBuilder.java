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

/**
 * The report interface is used to generate diagnostics about validation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface ValidationReportBuilder {

    /**
     * @return Returns the validation report.
     */
    ValidationReport getReport();

    /**
     * Reports an issue detected on a specified node.
     *
     * @param issueLevel issue level classifier.
     * @param message human readable message connected to the issue.
     * @param n the node affected by the issue.
     */
    void reportIssue(ValidationReport.IssueLevel issueLevel, String message, Node n);

    /**
     * Reports a detected issue.
     *
     * @param issueLevel issue level classifier.
     * @param message human readable message connected to the issue.
     */
    void reportIssue(ValidationReport.IssueLevel issueLevel, String message);

    /**
     * Traces that a rule has been applied.
     * 
     * @param r activated rule.
     */
    void traceRuleActivation(Rule r);

    /**
     * Reports an error occurred while executing a {@link Rule}.
     *
     * @param r rule originating the error.
     * @param e exception raised.
     * @param msg human readable message.
     */
    void reportRuleError(Rule r, Exception e, String msg);

    /**
     * Reports an error occurred while executing a {@link Fix}.
     * 
     * @param f fix originating the error.
     * @param e exception raised.
     * @param msg human readable message.
     */
    void reportFixError(Fix f, Exception e, String msg);

}
