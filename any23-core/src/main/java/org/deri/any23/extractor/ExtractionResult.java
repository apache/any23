/**
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.deri.any23.extractor;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.io.PrintStream;
import java.util.Collection;

/**
 * Interface defining the methods that a representation of an extraction result must have.
 */
public interface ExtractionResult {

    enum ErrorLevel {
        WARN,
        ERROR,
        FATAL
    }

    /**
     * This class defines a generic error traced by this extraction result.
     */
    class Error {

        private ErrorLevel level;
        private String     message;
        private int        row, col;

        Error(ErrorLevel l, String msg, int r, int c) {
            level = l;
            message = msg;
            row = r;
            col = c;
        }

        @Override
        public String toString() {
            return String.format("%s: '%s' (%d,%d)", level, message, row, col);
        }

    }

    /**
     * Write a triple.
     * Parameters can be null, then the triple will be silently ignored.
     *
     * @param s Subject
     * @param p Predicate
     * @param o Object
     */
    void writeTriple(Resource s, URI p, Value o);

    /**
     * Write a namespace.
     *
     * @param prefix the prefix of the namespace
     * @param uri    the long URI identifying the namespace
     */
    void writeNamespace(String prefix, String uri);

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
     * Close the result.
     * <p/>
     * Extractors should close their results as soon as possible, but
     * don't have to, the environment will close any remaining ones.
     * Implementations should be robust against multiple close()
     * invocations.
     */
    void close();

    /**
     * Open a result nested in the current one.
     *
     * @param context
     * @return the instance of the nested extraction result.
     */
    ExtractionResult openSubResult(Object context);

    /**
     * Prints out an errors report.
     *
     * @param ps
     */
    void printErrorsReport(PrintStream ps);

    /**
     * Returns all the collected errors.
     *
     * @return a collection of {@link org.deri.any23.extractor.ExtractionResult.Error}s.
     */
    Collection<Error> getErrors();

}