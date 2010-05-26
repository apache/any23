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

package org.deri.any23.validator.rule;

import org.deri.any23.validator.DOMDocument;
import org.deri.any23.validator.Fix;
import org.deri.any23.validator.Rule;
import org.deri.any23.validator.RuleContext;

/**
 * This fixes the missing <i>Open Graph</i> protocol.
 *
 * @see org.deri.any23.validator.rule.MissingOpenGraphNamespaceRule
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class OpenGraphNamespaceFix implements Fix {

    public static final String OPENGRAPH_PROTOCOL_NS = "http://opengraphprotocol.org/schema/";

    public String getHRName() {
        return "opengraph-namespace-fix";
    }

    public void execute(Rule rule, RuleContext context, DOMDocument document) {
        document.addAttribute("/HTML", "xmlns:og", OPENGRAPH_PROTOCOL_NS);
    }

}
