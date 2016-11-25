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

import java.io.OutputStream;

/**
 * Defines a serializer for validation reports.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ValidationReportSerializer {

    /**
     * Serializes the validation report on the given output stream.
     *
     * @param vr the validation report to be serialized.
     * @param os the output stream used to produce the serialization.
     * @throws SerializationException if there is an error serializing data
     */
    void serialize(ValidationReport vr, OutputStream os) throws SerializationException;

}
