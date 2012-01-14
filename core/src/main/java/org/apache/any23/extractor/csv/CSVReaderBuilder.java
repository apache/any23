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

package org.apache.any23.extractor.csv;

import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class is responsible to build a reader first guessing the configuration
 * from the file it self and then, if not successful, from the {@link org.apache.any23.configuration.DefaultConfiguration}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 */
public class CSVReaderBuilder {

    private static final String DEFAULT_FIELD_DELIMITER = ",";

    private static final String DEFAULT_COMMENT_DELIMITER = "#";

    public static final char NULL_CHAR = ' ';

    private static final char[] popularDelimiters = {'\t', '|', ',', ';'};

    private static DefaultConfiguration defaultConfiguration =
            DefaultConfiguration.singleton();

    private static final CSVStrategy[] strategies;

    static {
        strategies = new CSVStrategy[ popularDelimiters.length + 1 ];
        strategies[0] = CSVStrategy.DEFAULT_STRATEGY;
        int index = 1;
        for(char dlmt : popularDelimiters) {
            strategies[index++] = getCsvStrategy(dlmt, NULL_CHAR);
        }
    }

    /**
     * Builds a not <code>null</code> {@link org.apache.commons.csv.CSVParser} guessing
     * from the provided <i>CSV</i> file.
     *
     * @param is {@link InputStream} of the <i>CSV</i> file where guess the configuration.
     * @return a {@link CSVParser}
     * @throws java.io.IOException
     */
    public static CSVParser build(InputStream is) throws IOException {
        CSVStrategy bestStrategy = getBestStrategy(is);
        if(bestStrategy == null) bestStrategy = getCSVStrategyFromConfiguration();
        return new CSVParser( new InputStreamReader(is), bestStrategy );
    }

    /**
     * Checks whether the given input stream is a CSV or not.
     *
     * @param is input stream to be verified.
     * @return
     * @throws IOException
     */
    public static boolean isCSV(InputStream is) throws IOException {
        return getBestStrategy(is) != null;
    }

    private static CSVStrategy getBestStrategy(InputStream is) throws IOException {
        for( CSVStrategy strategy : strategies ) {
            if( testStrategy(is, strategy) ) {
                return strategy;
            }
        }
        return null;
    }

    private static CSVStrategy getCsvStrategy(char delimiter, char comment) {
        return new CSVStrategy(delimiter, '\'', comment);
    }

    private static CSVStrategy getCSVStrategyFromConfiguration() {
        char fieldDelimiter = getCharValueFromConfiguration(
                "any23.extraction.csv.field",
                DEFAULT_FIELD_DELIMITER
        );
        char commentDelimiter = getCharValueFromConfiguration(
                "any23.extraction.csv.comment",
                DEFAULT_COMMENT_DELIMITER
        );
        return new CSVStrategy(fieldDelimiter, '\'', commentDelimiter);
    }

    private static char getCharValueFromConfiguration(String property, String defaultValue) {
        String delimiter = defaultConfiguration.getProperty(
                property,
                defaultValue
        );
        if (delimiter.length() != 1 || delimiter.equals("")) {
            throw new RuntimeException(property + " value must be a single character");
        }
        return delimiter.charAt(0);
    }

    /**
     * make sure the reader has correct delimiter and quotation set.
     * Check first lines and make sure they have the same amount of columns and at least 2
     *
     * @param is input stream to be checked
     * @param strategy strategy to be verified.
     * @return
     * @throws IOException
     * @param is
     */
    private static boolean testStrategy(InputStream is, CSVStrategy strategy) throws IOException {
        final int MIN_COLUMNS = 2;

        is.mark(Integer.MAX_VALUE);
        try {
            final CSVParser parser = new CSVParser(new InputStreamReader(is), strategy);
            int linesToCheck = 5;
            int headerColumnCount = -1;
            while (linesToCheck > 0) {
                String[] row;
                row = parser.getLine();
                if (row == null) {
                    break;
                }
                if (row.length < MIN_COLUMNS) {
                    return false;
                }
                if (headerColumnCount == -1) { // first row
                    headerColumnCount = row.length;
                } else { // make sure rows have the same number of columns or one more than the header
                    if (row.length < headerColumnCount) {
                        return false;
                    } else if (row.length - 1 > headerColumnCount) {
                        return false;
                    }
                }
                linesToCheck--;
            }
            return true;
        } finally {
            is.reset();
        }
    }


}
