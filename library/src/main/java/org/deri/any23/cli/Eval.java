package org.deri.any23.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.deri.any23.eval.LogEvaluator;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Eval {

    /**
     * A simple main for testing
     *
     * @param args a url and an optional format name such as TURTLE,N3,N-TRIPLES,RDF/XML
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Options options = new Options();

        //inputformat
        Option input0 = new Option("i", true, "input file");
        options.addOption(input0);

        Option input1 = new Option("d", true, "input directory containing the log files");
        options.addOption(input1);


        Option outputFile = new Option("o", "output", true, "ouput file (defaults to stdout)");
        options.addOption(outputFile);

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("***ERROR: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Rover [file|url]", options, true);
            return;
        }


        LogEvaluator l = new LogEvaluator(cmd.getOptionValue("o"));
        if (cmd.hasOption("i")) l.analyseFile(cmd.getOptionValue("i"));
        if (cmd.hasOption("d")) l.analyseDirectory(cmd.getOptionValue("d"));

        l.close();
	}
}
