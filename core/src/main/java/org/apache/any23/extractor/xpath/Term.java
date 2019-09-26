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

import org.eclipse.rdf4j.model.Value;

import java.util.Locale;
import java.util.Map;

/**
 * Represents a generic template term.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class Term<T extends Value> {

    /**
     * Internal value.
     */
    private final String internalValue;

    /**
     * if true the #internalValue is a variable name,
     * otherwise is a constant.
     */
    private final boolean isVar;

    /**
     * Constructor.
     *
     * @param internalValue internal term value.
     * @param isVar if true the <code>internalValue</code> is a variable name,
     *              otherwise is a constant.
     */
    protected Term(String internalValue, boolean isVar) {
        this.internalValue = internalValue;
        this.isVar = isVar;
    }

    /**
     * @return the internal value.
     */
    public String getInternalValue() {
        return internalValue;
    }

    /**
     * @return the isVar flag value.
     */
    public boolean isVar() {
        return isVar;
    }

    /**
     * Returns the value represented by this {@link Term}
     * given the <code>varMapping</code>, the #isVar and #internalValue
     * parameters.
     *
     * @param varMapping a map representing values of variables.
     * @return the value for this term.
     */
    public T getValue(Map<String, String> varMapping) {
        final String value;
        if(isVar) {
            value = varMapping.get(internalValue);
            if(value == null) {
                throw new IllegalStateException(
                        String.format(Locale.ROOT, "Cannot find a valid value for variable '%s'", internalValue)
                );
            }
        } else {
            value = internalValue;
        }
        return getValueInternal(value);
    }

    protected abstract T getValueInternal(String value);

    @Override
    public String toString() {
        return isVar ? ( "?" + internalValue ) : internalValue;
    }
}
