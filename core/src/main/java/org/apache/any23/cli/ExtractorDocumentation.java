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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.any23.extractor.ExampleInputOutput;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorRegistryImpl;
import org.apache.any23.extractor.Extractor.BlindExtractor;
import org.apache.any23.extractor.Extractor.ContentExtractor;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.ExtractorRegistry;
import org.kohsuke.MetaInfServices;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides some command-line documentation
 * about available extractors and their usage.
 */
@MetaInfServices
@Parameters( commandNames = { "extractor" }, commandDescription= "Utility for obtaining documentation about metadata extractors.")
public class ExtractorDocumentation implements Tool {

    @Parameter( names = { "-l", "--list" }, description = "shows the names of all available extractors" )
    private boolean showList;

    @Parameter( names = { "-i", "--input" }, description = "shows example input for the given extractor" )
    private boolean showInput;

    @Parameter( names = { "-o", "--outut" }, description = "shows example output for the given extractor" )
    private boolean showOutput;

    @Parameter( names = { "-a", "--all" }, description = "shows a report about all available extractors" )
    private boolean showAll;

    @Parameter( arity = 1, description = "Extractor name" )
    private List<String> extractor = new LinkedList<String>();

    public void run() throws Exception {
        if (showList) {
            printExtractorList(ExtractorRegistryImpl.getInstance());
        } else if (showInput) {
            if (extractor.isEmpty()) {
                throw new IllegalArgumentException("Required argument for -i: extractor name");
            }

            printExampleInput(extractor.get(0), ExtractorRegistryImpl.getInstance());
        } else if (showOutput) {
            if (extractor.isEmpty()) {
                throw new IllegalArgumentException("Required argument for -o: extractor name");
            }

            printExampleOutput(extractor.get(0), ExtractorRegistryImpl.getInstance());
        } else if (showAll) {
            printReport(ExtractorRegistryImpl.getInstance());
        }
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
    public void printExtractorList(ExtractorRegistry registry) {
        for (ExtractorFactory factory : registry.getExtractorGroup()) {
            System.out.println( String.format("%25s [%15s]", factory.getExtractorName(), factory.getExtractorLabel()));
        }
    }

    /**
     * Prints an example of input for the provided extractor.
     *
     * @param extractorName the name of the extractor
     * @param registry 
     * @throws IOException raised if no extractor is found with that name
     */
    public void printExampleInput(String extractorName, ExtractorRegistry registry) throws IOException {
        ExtractorFactory<?> factory = getFactory(registry, extractorName);
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
     * @param registry 
     * @throws IOException raised if no extractor is found with that name
     * @throws ExtractionException
     */
    public void printExampleOutput(String extractorName, ExtractorRegistry registry) throws IOException, ExtractionException {
        ExtractorFactory<?> factory = getFactory(registry, extractorName);
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
    public void printReport(ExtractorRegistry registry) throws IOException, ExtractionException {
        for (String extractorName : registry.getAllNames()) {
            ExtractorFactory<?> factory = registry.getFactory(extractorName);
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

    private ExtractorFactory<?> getFactory(ExtractorRegistry registry, String name) {
        if (!registry.isRegisteredName(name)) {
            throw new IllegalArgumentException("Unknown extractor name: " + name);
        }
        return registry.getFactory(name);
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
