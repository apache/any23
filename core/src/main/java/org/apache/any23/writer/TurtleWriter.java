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
import org.eclipse.rdf4j.common.net.ParsedIRI;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;

import java.io.OutputStream;
import java.io.Writer;
import java.net.URISyntaxException;

/**
 * <i>N3</i> notation {@link TripleWriter} implementation.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public class TurtleWriter extends RDFWriterTripleHandler {

    static class Internal {
        // rdf4j-internal ArrangedWriter + -ea causes AssertionError
        // when writing example output of html-mf-hlisting extractor!
        // Override to return rdf4j TurtleWriter instances instead.
        private static final org.eclipse.rdf4j.rio.turtle.TurtleWriterFactory rdf4j = new org.eclipse.rdf4j.rio.turtle.TurtleWriterFactory() {
            @Override
            public RDFWriter getWriter(OutputStream out) {
                return new org.eclipse.rdf4j.rio.turtle.TurtleWriter(out);
            }

            @Override
            public RDFWriter getWriter(OutputStream out, String baseURI) throws URISyntaxException {
                return new org.eclipse.rdf4j.rio.turtle.TurtleWriter(out, new ParsedIRI(baseURI));
            }

            @Override
            public RDFWriter getWriter(Writer writer) {
                return new org.eclipse.rdf4j.rio.turtle.TurtleWriter(writer);
            }

            @Override
            public RDFWriter getWriter(Writer writer, String baseURI) throws URISyntaxException {
                return new org.eclipse.rdf4j.rio.turtle.TurtleWriter(writer, new ParsedIRI(baseURI));
            }
        };

        static final TripleFormat FORMAT = format(rdf4j);

        static final Settings SUPPORTED_SETTINGS = Settings.of(WriterSettings.PRETTY_PRINT);
    }

    @Override
    void configure(WriterConfig config, Settings settings) {
        config.set(BasicWriterSettings.PRETTY_PRINT, settings.get(WriterSettings.PRETTY_PRINT));
    }

    /**
     * Constructor.
     *
     * @param out
     *            stream to write on.
     */
    public TurtleWriter(OutputStream out) {
        this(out, Settings.of());
    }

    public TurtleWriter(OutputStream os, Settings settings) {
        super(Internal.rdf4j, Internal.FORMAT, os, settings);
    }

}
