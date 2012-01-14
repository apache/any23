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

package org.apache.any23.writer;

/**
 * This annotation describes {@link FormatWriter} metadata.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
public @interface Writer {

    /**
     * The mnemonic identifier for the format.
     *
     * @return a not <code>null</code> identifier.
     */
    String identifier();

    /**
     * The MIME type used by the writer.
     *
     * @return a not <code>null</code> MIME type.
     */
    String mimeType();

}
