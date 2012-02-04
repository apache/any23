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

package org.apache.any23.extractor;

import java.io.PrintStream;
import java.util.Collection;

/**
 * This interface models an error reporter.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
// TODO: rename ErrorReporter in IssueReporter ( most of reported errors are not blocking ).
public interface ErrorReporter {

    /**
     * Notifies an error occurred while performing an extraction on an input stream.
     *
     * @param level error level.
     * @param msg   error message.
     * @param row   error row.
     * @param col   error column.
     */
    void notifyError(ErrorLevel level, String msg, int row, int col);

    /**
     * Prints out an errors report.
     *
     * @param ps
     */
    void printErrorsReport(PrintStream ps);

    /**
     * Returns all the collected errors.
     *
     * @return a collection of {@link ErrorReporter.Error}s.
     */
    Collection<Error> getErrors();

    /**
     * Possible error levels.
     */
    enum ErrorLevel {
        WARN,
        ERROR,
        FATAL
    }

    /**
     * This class defines a generic error traced by this extraction result.
     */
    public class Error {

        private ErrorLevel level;
        private String message;
        private int row, col;

        Error(ErrorLevel l, String msg, int r, int c) {
            level = l;
            message = msg;
            row = r;
            col = c;
        }

        public ErrorLevel getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        @Override
        public String toString() {
            return String.format("%s: \t'%s' \t(%d,%d)", level, message, row, col);
        }
    }

}
