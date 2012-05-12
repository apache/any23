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

import org.apache.any23.validator.rule.AboutNotURIRule;
import org.apache.any23.validator.rule.MetaNameMisuseFix;
import org.apache.any23.validator.rule.MetaNameMisuseRule;
import org.apache.any23.validator.rule.MissingOpenGraphNamespaceRule;
import org.apache.any23.validator.rule.OpenGraphNamespaceFix;
import org.w3c.dom.Document;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link Validator}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class DefaultValidator implements Validator {

    private Map<Class<? extends Rule>, List<Class<? extends Fix>>> rulesToFixes;

    private List<Class<? extends Rule>> rulesOrder;

    public DefaultValidator() {
        rulesToFixes = new HashMap<Class<? extends Rule>, List<Class<? extends Fix>>>();
        rulesOrder   = new ArrayList<Class<? extends Rule>>();
        loadDefaultRules();
    }

    public ValidationReport validate(DOMDocument document, boolean applyFix)
    throws ValidatorException {
        final ValidationReportBuilder validationReportBuilder = new DefaultValidationReportBuilder();
        for(Class<? extends Rule> cRule : rulesOrder) {
            Rule rule = newRuleInstance(cRule);
            final RuleContext ruleContext = new DefaultRuleContext();            
            boolean applyOn;
            try {
                applyOn = rule.applyOn(document, ruleContext, validationReportBuilder);
            } catch (Exception e) {
                validationReportBuilder.reportRuleError(rule, e, "Error while processing rule.");
                continue;
            }
            if(applyFix && applyOn) {
                validationReportBuilder.traceRuleActivation(rule);
                List<Class<? extends Fix>> cFixes = getFixes(cRule);
                for(Class<? extends Fix> cFix : cFixes) {
                    Fix fix = newFixInstance(cFix);
                    try {
                        fix.execute(rule, ruleContext, document);
                    } catch (Exception e) {
                        validationReportBuilder.reportFixError(fix, e, "Error while processing fix.");
                    }
                }
            }
        }
        return validationReportBuilder.getReport();
    }

    public ValidationReport validate(URI documentURI, Document document, boolean applyFix)
    throws ValidatorException {
        return validate( new DefaultDOMDocument(documentURI, document), applyFix );
    }

    public synchronized void addRule(Class<? extends Rule> rule, Class<? extends Fix> fix) {
        List<Class<? extends Fix>> fixes = rulesToFixes.get(rule);
        if(fixes == null) {
            fixes = new ArrayList<Class<? extends Fix>>();
        }
        rulesOrder.add(rule);
        rulesToFixes.put(rule, fixes);
        if(fix != null)  {
            fixes.add(fix);
        }
    }

    public void addRule(Class<? extends Rule> rule) {
        addRule(rule, null);
    }

    public synchronized void removeRule(Class<? extends Rule> rule) {
        rulesOrder.remove(rule);
        rulesToFixes.remove(rule);
    }

    public List<Class<? extends Rule>> getAllRules() {
        return Collections.unmodifiableList(rulesOrder);
    }

    public List<Class<? extends Fix>> getFixes(Class<? extends Rule> rule) {
        List<Class<? extends Fix>> fixes = rulesToFixes.get(rule);
        return  fixes == null
                ?
                Collections.<Class<? extends Fix>>emptyList()
                :
                Collections.unmodifiableList( rulesToFixes.get(rule) );
    }

    private void loadDefaultRules() {
        addRule(MetaNameMisuseRule.class, MetaNameMisuseFix.class);
        addRule(MissingOpenGraphNamespaceRule.class, OpenGraphNamespaceFix.class);
        addRule(AboutNotURIRule.class);
    }

    private Fix newFixInstance(Class<? extends Fix> cFix) throws ValidatorException {
        try {
            return cFix.newInstance();
        } catch (Exception e) {
            throw new ValidatorException("An error occurred while instantiating a fix.", e);
        }
    }

    private Rule newRuleInstance(Class<? extends Rule> cRule) throws ValidatorException {
        try {
            return cRule.newInstance();
        } catch (Exception e) {
            throw new ValidatorException("An error occurred while instantiating a rule.", e);
        }
    }

}
