/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.cli;

import org.deri.any23.LogUtil;
import org.deri.any23.extractor.ExampleInputOutput;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.ExtractorRegistry;

import java.io.IOException;

/**
 * This class provides some command-line documentation
 * about available extractors and their usage.
 */
public class ExtractorDocumentation {

    /**
     * Main method to access the class functionalities.
     *
     * Usage:
     *     ExtractorDocumentation -list
     *       shows the names of all available extractors
     *
     *     ExtractorDocumentation -i extractor-name
     *       shows example input for the given extractor
     *
     *     ExtractorDocumentation -o extractor-name
     *       shows example input for the given extractor
     *
     *     ExtractorDocumentation -all
     *       shows a report about all available extractors
     *
     * @param args allowed arguments
     * @throws ExtractionException
     * @throws IOException
     */
    public static void main(String[] args) throws ExtractionException, IOException {
        LogUtil.setDefaultLogging();

        if (args.length == 0) {
            printUsageAndExit();
        }

        if ("-list".equals(args[0])) {
            if (args.length > 1) {
                printUsageAndExit();
            }
            printExtractorList();
            return;
        }

        if ("-i".equals(args[0])) {
            if (args.length > 2) {
                printUsageAndExit();
            }
            if (args.length < 2) {
                printErrorAndExit("Required argument for -i: extractor name");
            }
            printExampleInput(args[1]);
            return;
        }

        if ("-o".equals(args[0])) {
            if (args.length > 2) {
                printUsageAndExit();
            }
            if (args.length < 2) {
                printErrorAndExit("Required argument for -o: extractor name");
            }
            printExampleOutput(args[1]);
            return;
        }
        
        if ("-all".equals(args[0])) {
            if (args.length > 1) {
                printUsageAndExit();
            }
            printReport();
            return;
        }
        printUsageAndExit();
    }

    /**
     * Prints the command line usage help.
     */
    public static void printUsageAndExit() {
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
        System.exit(2);
    }

    /**
     * Print an error message.
     *
     * @param msg the error message to be printed
     */
    public static void printErrorAndExit(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    /**
     * Prints the list of all the available extractors.
     */
    public static void printExtractorList() {
        for (String extractorName : ExtractorRegistry.getInstance().getAllNames()) {
            System.out.println(extractorName);
        }
    }

    /**
     * Prints an example of input for the provided extractor.
     *
     * @param extractorName the name of the extractor
     * @throws IOException raised if no extractor is found with that name
     */
    public static void printExampleInput(String extractorName) throws IOException {
        ExtractorFactory<?> factory = getFactory(extractorName);
        ExampleInputOutput example = new ExampleInputOutput(factory);
        String input = example.getExampleInput();
        if (input == null) {
            printErrorAndExit("Extractor " + extractorName + " provides no example input");
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
    public static void printExampleOutput(String extractorName) throws IOException, ExtractionException {
        ExtractorFactory<?> factory = getFactory(extractorName);
        ExampleInputOutput example = new ExampleInputOutput(factory);
        String output = example.getExampleOutput();
        if (output == null) {
            printErrorAndExit("Extractor " + extractorName + " provides no example output");
        }
        System.out.println(output);
    }

    /**
     * Prints a complete report on all the available extractors.
     * 
     * @throws IOException
     * @throws ExtractionException
     */
    public static void printReport() throws IOException, ExtractionException {
        for (String extractorName : ExtractorRegistry.getInstance().getAllNames()) {
            ExtractorFactory<?> factory = ExtractorRegistry.getInstance().getFactory(extractorName);
            ExampleInputOutput example = new ExampleInputOutput(factory);
            System.out.println("Extractor: " + extractorName);
            System.out.println("  type: " + getType(factory));
            String output = example.getExampleOutput();
            if (output == null) {
                System.out.println("(no example output)");
            } else {
                System.out.println("-------- example output --------");
                System.out.println(output);
            }
            System.out.println();
            System.out.println("================================");
        }
    }

    private static ExtractorFactory<?> getFactory(String name) {
        if (!ExtractorRegistry.getInstance().isRegisteredName(name)) {
            printErrorAndExit("Unknown extractor name: " + name);
        }
        return ExtractorRegistry.getInstance().getFactory(name);
    }

    private static String getType(ExtractorFactory<?> factory) {
        Extractor<?> extractor = factory.createExtractor();
        if (extractor instanceof BlindExtractor) {
            return "BlindExtractor";
        }
        if (extractor instanceof TagSoupDOMExtractor) {
            return "TagSoupDOMExtractor";
        }
        if (extractor instanceof ContentExtractor) {
            return "ContentExtractor";
        }
        return "?";
    }
    
}
