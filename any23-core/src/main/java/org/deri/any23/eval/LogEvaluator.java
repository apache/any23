/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.eval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

/**
 * Helper class for evaluating textual log files.
 */
public class LogEvaluator {

    private static final Integer EXTRACTORS = 4;

    private static final File extrFrequ = new File("frequency.extractors.dat");

    private static final File triplesExtactors = new File("triples.extractors.dat");

    Count<String> extractorCounter = new Count<String>();

    Count<String> triplesPerExtractorCounter = new Count<String>();

    private File outDir;

    public LogEvaluator() {
        this(null);
    }

    public LogEvaluator(String outDir) {
        if (outDir != null) this.outDir = new File(outDir);
        else this.outDir = null;
    }

    public void analyseDirectory(String logDir) throws FileNotFoundException {
        File dir = new File(logDir);
        for (File f : dir.listFiles()) {
            System.err.println("Analysing " + f);
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
        String[] fields;
        String[] extractors;
        String extField;
        while (s.hasNextLine()) {
            try {
                line = s.nextLine().trim();
                fields = line.split("\t");
                extField = fields[EXTRACTORS];
                if (extField.trim().length() == 2) {
                    extractorCounter.add("EMPTY");
                } else {
                    extractors = extField.substring(2, extField.length() - 1).split(" ");
                    for (String st : extractors) {
                        extractorCounter.add(st.substring(0, st.indexOf(":")));
                        triplesPerExtractorCounter.add(
                                st.substring(0, st.indexOf(":")),
                                Integer.valueOf(st.substring(st.indexOf(":") + 1))
                        );
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getSimpleName() + " " + e.getMessage() + " for line " + line);
            }

        }


    }

    public void close() {
        try {
            if (outDir != null) {
                outDir.mkdirs();
                FileOutputStream fis = new FileOutputStream(new File(outDir, extrFrequ.toString()));
                extractorCounter.printStats(fis);
                fis.close();

                fis = new FileOutputStream(new File(outDir, triplesExtactors.toString()));
                triplesPerExtractorCounter.printStats(fis);
                fis.close();
            } else {
                extractorCounter.printStats(System.out);
                triplesPerExtractorCounter.printStats(System.out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
	    }

	}

}
