/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.deri.any23.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Contains general utility functions for handling streams.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StreamUtils {

    private StreamUtils(){}

    /**
     * Returns the string content of a stream.
     *
     * @param is input stream.
     * @return the string content.
     * @throws IOException if an error occurs while consuming the <code>is</code> stream.
     */
    public static String asString(InputStream is) throws IOException {
        if (is == null) {
            throw new NullPointerException("input stream is null.");
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            final StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } finally {
            br.close();
        }
    }

}
