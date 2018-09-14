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

import java.io.OutputStream;

import org.apache.any23.configuration.Settings;

/**
 * @author Peter Ansell (p_ansell@yahoo.com)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class RDFXMLWriterFactory implements TripleWriterFactory {

    public static final String MIME_TYPE = RDFXMLWriter.Internal.FORMAT.getMimeType();
    public static final String IDENTIFIER = "rdfxml";

    /**
     * 
     */
    public RDFXMLWriterFactory() {
    }

    @Override
    public TripleFormat getTripleFormat() {
        return RDFXMLWriter.Internal.FORMAT;
    }

    @Override
    public Settings getSupportedSettings() {
        return RDFXMLWriter.Internal.SUPPORTED_SETTINGS;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public TripleHandler getTripleWriter(OutputStream os, Settings settings) {
        return new RDFXMLWriter(os, settings);
    }

}
