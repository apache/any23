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

import org.apache.any23.configuration.Settings;
import org.eclipse.rdf4j.rio.WriterConfig;

import java.io.OutputStream;

/**
 * <i>RDF/XML</i> {@link TripleWriter} implementation.
 * @author Hans Brende (hansbrende@apache.org)
 */
public class RDFXMLWriter extends RDFWriterTripleHandler {

    static class Internal {
        private static final org.eclipse.rdf4j.rio.rdfxml.RDFXMLWriterFactory rdf4j
                = new org.eclipse.rdf4j.rio.rdfxml.RDFXMLWriterFactory();

        //TODO support pretty printing with RDFXMLPrettyWriterFactory

        static final TripleFormat FORMAT = format(rdf4j);

        static final Settings SUPPORTED_SETTINGS = Settings.of();
    }

    @Override
    void configure(WriterConfig config, Settings settings) {
    }

    public RDFXMLWriter(OutputStream os) {
        this(os, Settings.of());
    }

    public RDFXMLWriter(OutputStream os, Settings settings) {
        super(Internal.rdf4j, Internal.FORMAT, os, settings);
    }

}
