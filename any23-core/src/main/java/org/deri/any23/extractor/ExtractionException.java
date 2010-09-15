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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Defines a specific exception raised during the metadata extraction phase.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class ExtractionException extends Exception {

    private ExtractionResult extractionResult;

    public ExtractionException(String message) {
        super(message);
    }

    public ExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractionException(String message, Throwable cause, ExtractionResult er) {
        super(message, cause);
        extractionResult = er;
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        printExceptionResult( new PrintWriter(ps) );
        super.printStackTrace(ps);
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        printExceptionResult(pw);
        super.printStackTrace(pw);
    }

    private void printExceptionResult(PrintWriter ps) {
        if(extractionResult == null) {
            return;
        }
        ps.println();
        ps.println("------------ BEGIN Exception context ------------");
        ps.print( extractionResult.toString() );
        ps.println("------------ END   Exception context ------------");
        ps.println();
        ps.flush();
    }
}
