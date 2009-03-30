package org.deri.any23.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.writer.BenchmarkTripleHandler;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 * 
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Rover {

	private static final String USER_AGENT_NAME = "Any23-CLI";
	
	//output writer constants
	private final static String TURTLE = "turtle";
	private final static String NTRIPLE = "ntriples";
	private final static String RDFXML = "rdfxml";
	private final static String ZIP = "zip";
	private final static String WARC = "warc";
	/**
	 * A simple main for testing
	 * @param args a url and an optional format name such as TURTLE,N3,N-TRIPLES,RDF/XML
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, ExtractionException {
		Options options = new Options();
		
		//output format
		Option outputFormat = new Option("o",true,"["+TURTLE+" (default), "+NTRIPLE+" , "+RDFXML+"]");
		options.addOption(outputFormat);
		
		//inputformat
		Option input0 = new Option("I",true,"["+ZIP+" , "+WARC+"]");
		options.addOption(input0);
				
		Option outputFile = new Option("O",true,"ouput file, if omitted output is written to stdout");
		options.addOption(outputFile);
		
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("***ERROR: " + e.getClass() + ": " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Rover [file|url]", options,true );
			return;
		}
		
		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Rover [file|url]", options, true );
			return;
		}
		if(cmd.getArgs().length != 1) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Rover [file|url]", options, true );
			return;
		}
		
		//get the input location
		String inputURI = cmd.getArgs()[0]; 
		if(inputURI.toLowerCase().startsWith("http:"))
			inputURI = new URL(inputURI.trim()).toString();
		else
			inputURI = new File(inputURI.trim()).toURI().toString();

		String format = TURTLE;
		//check if an output format was specified
		if(cmd.hasOption("o")) {
			format = cmd.getOptionValue("o");
		}
		TripleHandler outputHandler = null;
		if (TURTLE.equals(format)) {
			outputHandler = new TurtleWriter(System.out);
		} else if (NTRIPLE.equals(format)) {
			outputHandler = new NTriplesWriter(System.out);
		} else 
			outputHandler = new RDFXMLWriter(System.out);

		
		BenchmarkTripleHandler bH = new BenchmarkTripleHandler(outputHandler);
		long start = System.currentTimeMillis();
		Any23 any23 = new Any23();
		any23.setHTTPUserAgent(USER_AGENT_NAME + "/" + Any23.VERSION);
		if(cmd.hasOption("I")) {
			String inputFormat = cmd.getOptionValue("I");
			if(inputFormat.equals(ZIP)) {
				if (!any23.extractZipFile(inputURI, bH)) {
					System.err.println("No suitable extractors");
					System.exit(2);
				}
				
			}
			if(inputFormat.equals(WARC)) {
				if (!any23.extractWARCFile(inputURI, bH)) {
					System.err.println("No suitable extractors");
					System.exit(2);
				}
			}
		}
		else {
			if (!any23.extract(inputURI, bH)) {
				System.err.println("No suitable extractors");
				System.exit(2);
			}
		}
		bH.close();
		System.err.println(bH.report());
		System.err.println("Time elapsed: "+(System.currentTimeMillis()-start)+" ms!");
	}
}
