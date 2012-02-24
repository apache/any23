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

import org.apache.any23.extractor.ExampleInputOutput;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.ExtractorRegistry;
import org.apache.any23.util.LogUtils;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.Extractor.BlindExtractor;
import org.apache.any23.extractor.Extractor.ContentExtractor;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.kohsuke.MetaInfServices;

import java.io.IOException;

/**
 * This class provides some command-line documentation
 * about available extractors and their usage.
 */
@MetaInfServices
@ToolRunner.Description("Utility for obtaining documentation about metadata extractors.")
public class ExtractorDocumentation implements Tool {

    /**
     * Main method to access the class functionality.
     *
     * Usage:
     *     ExtractorDocumentation -list
     *       shows the names of all available extractors
     *
     *     ExtractorDocumentation -i extractor-name
     *       shows example input for the given extractor
     *
     *     ExtractorDocumentation -o extractor-name
     *       shows example output for the given extractor
     *
     *     ExtractorDocumentation -all
     *       shows a report about all available extractors
     *
     * @param args allowed arguments
     * @throws ExtractionException
     * @throws IOException
     */
    public static void main(String[] args) throws ExtractionException, IOException {
        System.exit( new ExtractorDocumentation().run(args) );
    }

    public int run(String[] args) {
        LogUtils.setDefaultLogging();
        try {
            if (args.length == 0) {
                printUsage();
                return 1;
            }

            final String option = args[0];
            if ("-list".equals(option)) {
                if (args.length > 1) {
                    printUsage();
                    return 2;
                }
                printExtractorList();
            }
            else if ("-i".equals(option)) {
                if (args.length > 2) {
                    printUsage();
                    return 3;
                }
                if (args.length < 2) {
                    printError("Required argument for -i: extractor name");
                    return 4;
                }
                printExampleInput(args[1]);
            }
            else if ("-o".equals(option)) {
                if (args.length > 2) {
                    printUsage();
                    return 5;
                }
                if (args.length < 2) {
                    printError("Required argument for -o: extractor name");
                    return 6;
                }
                printExampleOutput(args[1]);
            }
            else if ("-all".equals(option)) {
                if (args.length > 1) {
                    printUsage();
                    return 7;
                }
                printReport();
            } else {
                printUsage();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 8;
        }
        return 0;
    }

    /**
     * Prints the command line usage help.
     */
    public void printUsage() {
        System.out.println("Usage:");
        System.out.println("  " + ExtractorDocumentation.class.getSimpleName() + " -list");
        System.out.println("      shows the names of all available extractors");
        System.out.println();
        System.out.println("  " + ExtractorDocumentation.class.getSimpleName() + " -i extractor-name");
        System.out.println("      shows example input for the given extractor");
        System.out.println();
        System.out.println("  " + ExtractorDocumentation.class.getSimpleName() + " -o extractor-name");
        System.out.println("      shows example output for the given extractor");
        System.out.println();
        System.out.println("  " + ExtractorDocumentation.class.getSimpleName() + " -all");
        System.out.println("      shows a report about all available extractors");
        System.out.println();
    }

    /**
     * Print an error message.
     *
     * @param msg the error message to be printed
     */
    public void printError(String msg) {
        System.err.println(msg);
    }

    /**
     * Prints the list of all the available extractors.
     */
    public void printExtractorList() {
        for(ExtractorFactory factory : ExtractorRegistry.getInstance().getExtractorGroup()) {
            System.out.println( String.format("%25s [%15s]", factory.getExtractorName(), factory.getExtractorType()));
        }
    }

    /**
     * Prints an example of input for the provided extractor.
     *
     * @param extractorName the name of the extractor
     * @throws IOException raised if no extractor is found with that name
     */
    public void printExampleInput(String extractorName) throws IOException {
        ExtractorFactory<?> factory = getFactory(extractorName);
        ExampleInputOutput example = new ExampleInputOutput(factory);
        String input = example.getExampleInput();
        if (input == null) {
            throw new IllegalArgumentException("Extractor " + extractorName + " provides no example input");
        }
        System.out.println(input);
    }

    /**
     * Prints an output example for the given extractor.
     *
     * @param extractorName the extractor name
     * @throws IOException raised if no extractor is found with that name
     * @throws ExtractionException
     */
    public void printExampleOutput(String extractorName) throws IOException, ExtractionException {
        ExtractorFactory<?> factory = getFactory(extractorName);
        ExampleInputOutput example = new ExampleInputOutput(factory);
        String output = example.getExampleOutput();
        if (output == null) {
            throw new IllegalArgumentException("Extractor " + extractorName + " provides no example output");
        }
        System.out.println(output);
    }

    /**
     * Prints a complete report on all the available extractors.
     *
     * @throws IOException
     * @throws ExtractionException
     */
    public void printReport() throws IOException, ExtractionException {
        for (String extractorName : ExtractorRegistry.getInstance().getAllNames()) {
            ExtractorFactory<?> factory = ExtractorRegistry.getInstance().getFactory(extractorName);
            ExampleInputOutput example = new ExampleInputOutput(factory);
            System.out.println("Extractor: " + extractorName);
            System.out.println("\ttype: " + getType(factory));
            System.out.println();
            final String exampleInput = example.getExampleInput();
            if(exampleInput == null) {
                System.out.println("(No Example Available)");
            } else {
                System.out.println("-------- Example Input  --------");
                System.out.println(exampleInput);
                System.out.println("-------- Example Output --------");
                String output = example.getExampleOutput();
                System.out.println(output == null || output.trim().length() == 0 ? "(No Output Generated)" : output);
            }
            System.out.println("================================");
            System.out.println();
        }
    }

    private ExtractorFactory<?> getFactory(String name) {
        if (!ExtractorRegistry.getInstance().isRegisteredName(name)) {
            throw new IllegalArgumentException("Unknown extractor name: " + name);
        }
        return ExtractorRegistry.getInstance().getFactory(name);
    }

    private String getType(ExtractorFactory<?> factory) {
        Extractor<?> extractor = factory.createExtractor();
        if (extractor instanceof BlindExtractor) {
            return BlindExtractor.class.getSimpleName();
        }
        if (extractor instanceof TagSoupDOMExtractor) {
            return TagSoupDOMExtractor.class.getSimpleName();
        }
        if (extractor instanceof ContentExtractor) {
            return ContentExtractor.class.getSimpleName();
        }
        return "?";
    }

}
