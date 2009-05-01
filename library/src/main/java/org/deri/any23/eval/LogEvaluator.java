package org.deri.any23.eval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

public class LogEvaluator {

	private boolean _append;
	private File _outDir;
	private static final File extrFrequ = new File("frequency.extractors.dat");
	private static final File triplesExtactors = new File("triples.extractors.dat");
	
	
	public LogEvaluator() {
		this(null,false);
	}
	public LogEvaluator(String outDir) {
			this(outDir,false);
	}
	
	/**
	 * 
	 * @param append - process multiple files and store the stats
	 * @see LoggingTripleHandler
	 */
	public LogEvaluator(String outDir, boolean append) {
		_append = append;
		if(outDir != null) _outDir = new File(outDir);
		else _outDir = null;
	}
	
	/*
 * Typical log lines
 *tab separeted list of values
 *	http://vodpod.com/watch/1502483-how-i-met-your-mother-not-a-prostitute	68283	31	true	[ html-head-title:1 html-rdfa:4]
 * http://www.eastvillagepodcasts.com/2008/12/19/breaking-news-east-village-blizzard-hits/	28382	23	true	[ html-head-title:1 html-rdfa:31]
 */
	
	Count<String> extractorCounter = new Count<String>();
	Count<String> triplesPerExtractorCounter = new Count<String>();
	
	private final static Integer CONTENT_LENGTH=1;
	private final static Integer PROCESSING_TIME=2;
	private final static Integer EXTRACTORS=4;
	
	public static void main(String[] args) {
		String s ="[ ]";
		System.out.println(s.substring(2,s.length()-1));
	}
	
	public void analyseDirectory(String logDir) throws FileNotFoundException {
		File dir = new File(logDir);
		for(File f: dir.listFiles()){
			System.err.println("Analysing "+f);
			analyseFile(f.getAbsolutePath());
		}
	}
	
	/**
	 * @param logFile - the log file to analyse
	 * @throws FileNotFoundException 
	 */
	public void analyseFile(String logFile) throws FileNotFoundException {
		Scanner s = new Scanner(new File(logFile));
		String line = "";
		String [] fields;
		String [] extractors;
		String extField;
		while(s.hasNextLine()){
			try{
			line = s.nextLine().trim();
			fields = line.split("\t");
			extField = fields[EXTRACTORS];
			if(extField.trim().length()==2){
				extractorCounter.add("EMPTY");
//				triplesPerExtractorCounter.add(st.substring(0,st.indexOf(":")),Integer.valueOf(st.substring(st.indexOf(":")+1) ));
			}
			else{
				extractors = extField.substring(2,extField.length()-1).split(" ");
				for(String st: extractors){
					extractorCounter.add(st.substring(0,st.indexOf(":")));
					triplesPerExtractorCounter.add(st.substring(0,st.indexOf(":")),Integer.valueOf(st.substring(st.indexOf(":")+1) ));
				}
			}
			}catch(Exception e){
				System.err.println(e.getClass().getSimpleName()+" "+e.getMessage()+" for line "+line);
			}
			
		}
		
		
		
	}
	
	public void close() {
		try{
			if(_outDir != null){
				_outDir.mkdirs();
				FileOutputStream fis = new FileOutputStream(new File(_outDir,extrFrequ.toString()));
				extractorCounter.printStats(fis);
				fis.close();
				
				fis = new FileOutputStream(new File(_outDir,triplesExtactors.toString()));
				triplesPerExtractorCounter.printStats(fis);
				fis.close();
			}
			else{
				extractorCounter.printStats(System.out);
				triplesPerExtractorCounter.printStats(System.out);
			}
			}catch(Exception e){
				e.printStackTrace();
			}

	}
	
	
}
