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

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * Represents a <i>Quad</i> subject <i>template</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateSubject extends Term<Resource> {

    /**
     * Supported subject types.
     */
    public enum Type {
        URI,
        BNODE
    }

    /**
     * Instance subject type.
     */
    private final Type type;

    /**
     * Constructor.
     *
     * @param type subject type.
     * @param value internal value.
     * @param isVar if <code>true</code> it the given <code>value</code>
     *              will be resolved with the variable value.
     */
    public TemplateSubject(Type type, String value, boolean isVar) {
        super(value, isVar);
        if (type == null) {
            throw new NullPointerException("object type cannot be null.");
        }
        this.type = type;
    }

    @Override
    protected Resource getValueInternal(String value) {
        switch (type) {
            case URI:
                return SimpleValueFactory.getInstance().createIRI(value);
            case BNODE:
                return SimpleValueFactory.getInstance().createBNode(value);
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        final String superStr = super.toString();
        switch (type) {
            case URI:
                return "<" + superStr + ">";
            case BNODE:
                return "_:" + superStr;
            default:
                throw new IllegalStateException();
        }
    }

}
