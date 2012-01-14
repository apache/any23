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

package org.apache.any23.rdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

/**
 * This class act as a container for various well-known and adopted <i>RDF</i> Vocabulary prefixes.
 */
public class PopularPrefixes {

    private static final Logger logger = LoggerFactory.getLogger(PopularPrefixes.class);

    private static final String RESOURCE_NAME = "/org/apache/any23/prefixes/prefixes.properties";

    private static Prefixes popularPrefixes = getPrefixes();

    private static Prefixes getPrefixes() {
        Prefixes prefixes = new Prefixes();
        Properties properties = new Properties();
        try {
            logger.info(String.format("Loading prefixes from %s", RESOURCE_NAME));
            properties.load(getResourceAsStream());
        } catch (IOException e) {
            logger.error(String.format("Error while loading prefixes from %s", RESOURCE_NAME), e);
            throw new RuntimeException(String.format("Error while loading prefixes from %s", RESOURCE_NAME));
        }
        popularPrefixes = new Prefixes();
        for (Map.Entry entry : properties.entrySet()) {
            if (testURICompliance((String) entry.getValue())) {
                prefixes.add(
                        (String) entry.getKey(),
                        (String) entry.getValue()
                );
            } else {
                logger.warn(String.format("Prefixes entry '%s' is not a well-formad URI. Skipped.", entry.getValue()));
            }
        }
        return prefixes;
    }

    /**
     * This method perform a prefix lookup. Given a set of prefixes it returns {@link Prefixes} bag
     * class containing them.
     *
     * @param prefixes the input prefixes where perform the lookup
     * @return a {@link Prefixes} containing all the prefixes mathing the input parameter
     */
    public static Prefixes createSubset(String... prefixes) {
        return popularPrefixes.createSubset(prefixes);
    }

    /**
     * @return a {@link Prefixes} with a set of well-known prefixes
     */
    public static Prefixes get() {
        return popularPrefixes;
    }

    /**
     * Checks the compliance of the <i>URI</i>.
     *
     * @param stringUri the string of the URI to be checked
     * @return <code>true</code> if <i> stringUri</i> is a valid URI,
     *         <code>false</code> otherwise.
     */
    private static boolean testURICompliance(String stringUri) {
        try {
            new URI(stringUri);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    /**
      * Loads the prefixes list configuration file.
      *
      * @return the input stream containing the configuration.
      */
     private static InputStream getResourceAsStream() {
         InputStream result;
         result = PopularPrefixes.class.getResourceAsStream(RESOURCE_NAME);
         if (result == null) {
             result = PopularPrefixes.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
             if (result == null) {
                 result = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME);
             }
         }
         return result;
     }


}
