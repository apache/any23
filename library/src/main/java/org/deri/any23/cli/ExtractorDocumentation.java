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

public class ExtractorDocumentation {

    public static void main(String[] args) throws ExtractionException, IOException {
        LogUtil.setDefaultLogging();
//		LogUtil.setVerboseLogging();
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

    public static void printUsageAndExit() {
        System.out.println("Usage:");
        System.out.println("  ExtractorDocumentation -list");
        System.out.println("      shows the names of all available extractors");
        System.out.println();
        System.out.println("  ExtractorDocumentation -i extractor-name");
        System.out.println("      shows example input for the given extractor");
        System.out.println();
        System.out.println("  ExtractorDocumentation -o extractor-name");
        System.out.println("      shows example input for the given extractor");
        System.out.println();
        System.out.println("  ExtractorDocumentation -all");
        System.out.println("      shows a report about all available extractors");
        System.out.println();
        System.exit(2);
    }

    public static void printErrorAndExit(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    public static void printExtractorList() {
        for (String extractorName : ExtractorRegistry.get().getAllNames()) {
            System.out.println(extractorName);
        }
    }

    public static void printExampleInput(String extractorName) throws IOException {
        ExtractorFactory<?> factory = getFactory(extractorName);
        ExampleInputOutput example = new ExampleInputOutput(factory);
        String input = example.getExampleInput();
        if (input == null) {
            printErrorAndExit("Extractor " + extractorName + " provides no example input");
        }
        System.out.println(input);
    }

    public static void printExampleOutput(String extractorName) throws IOException, ExtractionException {
        ExtractorFactory<?> factory = getFactory(extractorName);
        ExampleInputOutput example = new ExampleInputOutput(factory);
        String output = example.getExampleOutput();
        if (output == null) {
            printErrorAndExit("Extractor " + extractorName + " provides no example output");
        }
        System.out.println(output);
    }

    public static void printReport() throws IOException, ExtractionException {
        for (String extractorName : ExtractorRegistry.get().getAllNames()) {
            ExtractorFactory<?> factory = ExtractorRegistry.get().getFactory(extractorName);
            ExampleInputOutput example = new ExampleInputOutput(factory);
            System.out.println("Extractor: " + extractorName);
            System.out.println("  type: " + getType(factory));
            String output = example.getExampleOutput();
            if (output == null) {
                System.out.println("  (no example output)");
            } else {
                System.out.println("-------- example output --------");
                System.out.println(output);
            }
            System.out.println();
            System.out.println("================================");
        }
    }

    private static ExtractorFactory<?> getFactory(String name) {
        if (!ExtractorRegistry.get().isRegisteredName(name)) {
            printErrorAndExit("Unknown extractor name: " + name);
        }
        return ExtractorRegistry.get().getFactory(name);
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
