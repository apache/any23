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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Prints out the vocabulary <i>RDFSchema</i> as <i>NQuads</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@ToolRunner.Description("Prints out the RDF Schema of the vocabularies used by Any23.")
public class VocabPrinter implements Tool {

    public static void main(String[] args) throws IOException {
        System.exit( new VocabPrinter().run(args) );
    }

    public int run(String[] args) {
        final CommandLineParser parser = new PosixParser();
        final CommandLine commandLine;
        final RDFSchemaUtils.VocabularyFormat format;
        try {
            final Options options = new Options();
            options.addOption(
                    new Option("h", "help", false, "Print this help.")
            );
            options.addOption(
                    new Option(
                        "f", "format",
                        true,
                        "Vocabulary output format, supported values are: " +
                        Arrays.toString(RDFSchemaUtils.VocabularyFormat.values())
                    )
            );
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption("h")) {
                printHelp(options);
                return 0;
            }
            try {
                format = RDFSchemaUtils.VocabularyFormat.valueOf(
                        commandLine.getOptionValue("f", RDFSchemaUtils.VocabularyFormat.NQuads.name())
                );
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Unknown format [" + commandLine.getOptionValue("f") + "'");
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 1;
        }

        final BufferedOutputStream bos = new BufferedOutputStream(System.out);
        try {
            RDFSchemaUtils.serializeVocabularies(format, System.out);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 1;
        } finally {
            try {
                bos.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
            System.out.println();
        }
        return 0;
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getClass().getSimpleName(), options, true);
    }

}
