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
 * An implementation of {@link org.deri.any23.validator.ValidationReport} with no data.
 *
 * @author Davide Palmisano (palmisano@fbk.eu)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class EmptyValidationReport implements ValidationReport {

    public static final EmptyValidationReport INSTANCE = new EmptyValidationReport();

    public static EmptyValidationReport getInstance() {
        return INSTANCE; 
    }

    private EmptyValidationReport() {}

    public void reportIssue(IssueLevel issueLevel, String message, Node n) {
        throw new UnsupportedOperationException();
    }

    public void reportIssue(IssueLevel issueLevel, String message) {
        throw new UnsupportedOperationException();
    }

    public void traceRuleActivation(Rule r) {
        throw new UnsupportedOperationException();
    }

    public void reportRuleError(Rule r, Exception e, String msg) {
        throw new UnsupportedOperationException();
    }

    public void reportFixError(Fix r, Exception e, String msg) {
        throw new UnsupportedOperationException();
    }
}
