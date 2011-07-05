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

package org.deri.any23.extractor.csv;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.deri.any23.configuration.DefaultConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class is responsible to build a reader first guessing the configuration
 * from the file it self and then, if not successful, from the {@link DefaultConfiguration}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class CSVReaderBuilder {

    private static final String DEFAULT_FIELD_DELIMITER = ",";

    private static final String DEFAULT_COMMENT_DELIMITER = "#";

    public static final char NULL_CHAR = ' ';

    private static final String[] popularDelimiters = {"\t", "|", ",", ";"};

    private static DefaultConfiguration defaultConfiguration =
            DefaultConfiguration.singleton();

    /**
     * Builds a not <code>null</code> {@link org.apache.commons.csv.CSVParser} guessing
     * from the provided <i>CSV</i> file.
     *
     * @param is {@link InputStream} of the <i>CSV</i> file where guess the configuration.
     * @return a {@link CSVParser}
     */
    public static CSVParser build(InputStream is) throws IOException {
        CSVParser parser;
        // now try real CSV reader with popular delimiters, starting with pure TAB and classic CSV first:
        is.mark(0);
        parser = new CSVParser(
                new InputStreamReader(is),
                CSVStrategy.DEFAULT_STRATEGY
        );
        is.reset();
        if (testParser(parser)) {
            is.reset();
            return new CSVParser(
                    new InputStreamReader(is),
                    CSVStrategy.DEFAULT_STRATEGY
            );
        }
        // TAB and others
        for (String dlmt : popularDelimiters) {
            parser = new CSVParser(
                    new InputStreamReader(is),
                    getCsvStrategy(dlmt.charAt(0), NULL_CHAR)
            );
            is.reset();
            if (testParser(parser)) {
                is.reset();
                return new CSVParser(
                        new InputStreamReader(is),
                        getCsvStrategy(dlmt.charAt(0), NULL_CHAR)
                );
            }
        }
        // if is not possible to detect them, then build it from configuration
        is.reset();
        return new CSVParser(
                new InputStreamReader(is),
                getCSVStrategyFromConfiguration()
        );
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
     * @param parser
     * @return
     * @throws IOException
     */
    private static boolean testParser(CSVParser parser) throws IOException {
        final int minColumns = 2;
        int linesToCheck = 25;

        int headerColumnCount = 0;
        while (linesToCheck > 0) {
            String[] row;
            row = parser.getLine();
            if (row == null) {
                break;
            }
            if (row.length < minColumns) {
                return false;
            }
            if (headerColumnCount == 0) {
                // first row
                headerColumnCount = row.length;
            } else {
                // make sure rows have the same number of columns or one more than the header
                if (row.length < headerColumnCount) {
                    return false;
                } else if (row.length - 1 > headerColumnCount) {
                    return false;
                }
            }
            linesToCheck--;
        }
        return true;
    }


}
