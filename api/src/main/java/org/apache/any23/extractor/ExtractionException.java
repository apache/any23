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

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

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
        printExceptionResult( new PrintWriter(new OutputStreamWriter(ps, StandardCharsets.UTF_8), true));
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
