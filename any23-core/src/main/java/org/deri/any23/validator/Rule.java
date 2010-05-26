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

/**
 * Models a rule for an issue detection.
 *
 * @see org.deri.any23.validator.Fix
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface Rule {

    /**
     * @return returns the human readable name for this rule.
     */
    String getHRName();

    /**
     * Applies this rule to the given document.
     *
     * @param document the target document.
     * @param context the context used to pass data to an eventual {@link org.deri.any23.validator.Fix}.
     * @param validationReportBuilder the report builder used to collect rule reporting.
     * @return <code>true</code> if al least an issue is detected, <code>false</code> otherwise.
     */
    boolean applyOn(DOMDocument document, RuleContext context, ValidationReportBuilder validationReportBuilder);

}
