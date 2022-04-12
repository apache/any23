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

package org.apache.any23.configuration;

/**
 * Modifiable implementation of {@link Configuration}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ModifiableConfiguration extends Configuration {

    /**
     * Sets a new value <code>propertyValue</code> for property which name <code>propertyName</code>.
     *
     * @param propertyName
     *            name of property.
     * @param propertyValue
     *            value of property.
     *
     * @return the old property value.
     */
    String setProperty(String propertyName, String propertyValue);

}
