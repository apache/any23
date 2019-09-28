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

import java.util.Locale;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * Represents a <i>Quad</i> predicate <i>template</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplatePredicate extends Term<IRI> {

    /**
     * Constructor.
     *
     * @param value internal value.
     * @param isVar if <code>true</code> it the given <code>value</code>
     *              will be resolved with the variable value.
     */
    public TemplatePredicate(String value, boolean isVar) {
        super(value, isVar);
    }

    @Override
    protected IRI getValueInternal(String value) {
        try {
            return SimpleValueFactory.getInstance().createIRI(value);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException(
                    String.format(Locale.ROOT, "Expected a valid IRI for predicate template, found '%s'", value),
                    iae
            );
        }
    }

    @Override
    public String toString() {
        return "<" + super.toString() + ">";
    }

}
