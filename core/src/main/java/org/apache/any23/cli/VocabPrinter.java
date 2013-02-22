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

package org.apache.any23.cli;

import org.apache.any23.vocab.RDFSchemaUtils;
import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Prints out the vocabulary <i>RDFSchema</i> as <i>NQuads</i>.
 * 
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@MetaInfServices
@Parameters(commandNames = { "vocab" }, commandDescription = "Prints out the RDF Schema of the vocabularies used by Any23.")
public class VocabPrinter implements Tool {

    @Parameter(names = { "-f", "--format" }, description = "Vocabulary output format", converter = RDFFormatConverter.class)
    private RDFFormat format = RDFFormat.NQUADS;

    public void run() throws Exception {
        RDFSchemaUtils.serializeVocabularies(format, System.out);
    }

    public static final class RDFFormatConverter implements
            IStringConverter<RDFFormat> {

        @Override
        public RDFFormat convert(String value) {
            try {
                return RDFFormat.valueOf(value);
            } catch (Throwable t) {
                throw new IllegalArgumentException("Unknown format [" + value
                        + "'");
            }
        }

    }

}
