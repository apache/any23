package org.deri.any23.cli;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.deri.any23.Any23;
import org.deri.any23.LogUtil;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.deri.any23.writer.BenchmarkTripleHandler;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 * 
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Rover {
	private static final Logger logger = LoggerFactory.getLogger(Rover.class);
	
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
	public static void main(String[] args) {
		Options options = new Options();
		
		//output format
		Option outputFormat = new Option("f", "format", true,"["+TURTLE+" (default), "+NTRIPLE+", "+RDFXML+"]");
		options.addOption(outputFormat);
		
		//inputformat
		Option input0 = new Option("I",true,"["+ZIP+", "+WARC+"]");
		options.addOption(input0);

		Option extractor = new Option("e", true, "comma-separated list of extractors, e.g. rdf-xml,rdf-turtle");
		options.addOption(extractor);
		
		Option outputFile = new Option("o", "output", true,"ouput file (defaults to stdout)");
		options.addOption(outputFile);
		
		Option filterTrivial = new Option("t", "notrivial", false, "filter trivial statements");
		options.addOption(filterTrivial);
		
		Option stats = new Option("s", "stats",false,"print out statistics of Any23");
		options.addOption(stats);
		
		Option verbose = new Option("v", "verbose", false, "show progress and debug information");
		options.addOption(verbose);
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("***ERROR: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Rover [file|url]", options,true );
			return;
		}
		
		if (cmd.hasOption('v')) {
			LogUtil.setVerboseLogging();
		} else {
			LogUtil.setDefaultLogging();
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
			try {
				inputURI = new URL(inputURI.trim()).toString();
			} catch (MalformedURLException ex) {
				System.err.println("Malformed URL: " + ex + "(" + ex.getMessage() + ")");
				System.exit(-1);
			}
		else{
			if(!new File(inputURI.trim()).exists()){
				System.err.println("FileNotFoundException for input file "+new File(inputURI.trim()).toURI().toString());
				System.exit(-1);
			}
			inputURI = new File(inputURI.trim()).toURI().toString();
		}
		
		String[] extractorNames = null;
		if (cmd.hasOption('e')) {
			extractorNames = cmd.getOptionValue('e').split(",");
		}
		
		String format = TURTLE;
		//check if an output format was specified
		if(cmd.hasOption("f")) {
			format = cmd.getOptionValue("f");
		}
		TripleHandler outputHandler = null;
		if (TURTLE.equals(format)) {
			outputHandler = new TurtleWriter(System.out);
		} else if (NTRIPLE.equals(format)) {
			outputHandler = new NTriplesWriter(System.out);
		} else 
			outputHandler = new RDFXMLWriter(System.out);

		if (cmd.hasOption('t')) {
			outputHandler = new IgnoreAccidentalRDFa(new IgnoreTitlesOfEmptyDocuments(outputHandler));
		}
		if(cmd.hasOption('s')){
			outputHandler = new BenchmarkTripleHandler(outputHandler);	
		}
		 
		long start = System.currentTimeMillis();
		Any23 any23 = (extractorNames == null || extractorNames.length == 0) ? new Any23() : new Any23(extractorNames);
		any23.setHTTPUserAgent(USER_AGENT_NAME + "/" + Any23.VERSION);
		if(cmd.hasOption("I")) {
			String inputFormat = cmd.getOptionValue("I");
			if(inputFormat.equals(ZIP)) {
				if (!any23.extractZipFile(inputURI, outputHandler)) {
					System.err.println("No suitable extractors");
					System.exit(2);
				}
				
			}
			if(inputFormat.equals(WARC)) {
				if (!any23.extractWARCFile(inputURI, outputHandler)) {
					System.err.println("No suitable extractors");
					System.exit(2);
				}
			}
		}
		else {
			try {
				if (!any23.extract(inputURI, outputHandler)) {
					System.err.println("No suitable extractors");
					System.exit(2);
				}
			} catch (ExtractionException ex) {
				logger.debug("Exception in Any23", ex);
				System.err.println(ex.getMessage());
				System.exit(3);
			} catch (IOException ex) {
				logger.debug("Exception in Any23", ex);
				System.err.println(ex.getMessage());
				System.exit(4);
			}
		}
		outputHandler.close();
		if(outputHandler instanceof BenchmarkTripleHandler) {
			System.err.println(((BenchmarkTripleHandler)outputHandler).report());
		}
		logger.info("Time elapsed: "+(System.currentTimeMillis()-start)+"ms");
	}
}
