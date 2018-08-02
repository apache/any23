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
package org.apache.any23.rdf.librdfa;

import java.io.IOException;
import java.io.InputStream;
import org.apache.any23.rdf.rdfa.LibrdfaRDFaParser;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.helpers.ParseErrorCollector;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Julio Caguano
 *
 */
public class LibrdfaRDFaParserTest {

    private ValueFactory vf;
    private RDFParser parser;
    private StatementCollector sc;
    private ParseErrorCollector el;

    @Before
    public void setUp() throws Exception {

        vf = SimpleValueFactory.getInstance();
        parser = new LibrdfaRDFaParser();
        sc = new StatementCollector();
        parser.setRDFHandler(sc);
        el = new ParseErrorCollector();
//        parser.setParseErrorListener(el);
    }

    @Test
    public void testHtml() throws IOException {
        try (final InputStream in = this.getClass().getResourceAsStream(
                "/org/apache/any23/rdf/librdfa/site.html");) {
            parser.parse(in, "http://example.org/");
            assertEquals(4, sc.getStatements().size());
        }
    }

}
