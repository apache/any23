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

package org.apache.any23.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains general utility functions for handling streams.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    private StreamUtils(){}

    /**
     * Returns all the lines read from an input stream.
     *
     * @param is input stream.
     * @return list of not <code>null</code> lines.
     * @throws IOException
     */
    public static String[] asLines(InputStream is) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        final List<String> lines = new ArrayList<String>();
        try {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray( new String[ lines.size() ] );
        } finally {
            closeGracefully(br);
        }
    }

    /**
     * Returns the string content of a stream.
     *
     * @param is input stream.
     * @param preserveNL preserves new line chars.
     * @return the string content.
     * @throws IOException if an error occurs while consuming the <code>is</code> stream.
     */
    public static String asString(InputStream is, boolean preserveNL) throws IOException {
        if (is == null) {
            throw new NullPointerException("input stream is null.");
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            final StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                if(preserveNL) content.append('\n');
            }
            return content.toString();
        } finally {
            closeGracefully(br);
        }
    }

    /**
     * Returns the string content of a stream, new line chars will be removed.
     *
     * @param is input stream.
     * @return the string content.
     * @throws IOException if an error occurs while consuming the <code>is</code> stream.
     */
     public static String asString(InputStream is) throws IOException {
         return asString(is, false);
     }

    /**
     * Closes the closable interface and reports error if any.
     *
     * @param closable the closable object to be closed.
     */
    public static void closeGracefully(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (Exception e) {
                logger.error("Error while closing object " + closable, e);
            }
        }
    }

}
