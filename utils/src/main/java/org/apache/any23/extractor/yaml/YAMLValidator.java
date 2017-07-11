/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.any23.extractor.yaml;

import com.google.common.collect.Iterables;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Utility class provides static methods for YAML validation.
 *
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YAMLValidator {

    private static final Logger log = LoggerFactory.getLogger(YAMLValidator.class);

    private static final Pattern YAML_PATTERN = Pattern.compile("^%YAML.*", Pattern.CASE_INSENSITIVE);

    /**
     * Detects if is contains valid YAML content.
     * <p>
     * In the first instance it checks if there is "%YAML" head. If not check
     * using the brute force method by parsing input stream with yaml parser.
     * </p>
     * <p>
     * NB. Only "false" results are trusted. Even if result is "true" you cannot
     * be sure that InputStream contains YAML intentional context because
     * comma-separated-values are pars-able by YAML parser as well.
     * </p>
     *
     * @param is {@link InputStream}
     * @return
     * @throws IOException
     */
    public static boolean isYAML(InputStream is) throws IOException {
        if (is == null) {
            return false;
        }

        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }

        boolean result = false;

        // mark the reading frame position. MUST BE FIRST
        is.mark(Integer.MAX_VALUE);

        while (true) {
            // if is is empty than return false
            if (is.available() <= 0) {
                break;
            }

            Scanner sc = new Scanner(is);
            String out = sc.findWithinHorizon(YAML_PATTERN, 0);

            if (out != null && !out.isEmpty()) {
                log.debug("Head: {}", out);
                result = true;
                break;
            }
            log.debug("Still not found. output is: {}", out);
            is.reset();

            try {
                Yaml yml = new Yaml();
                Iterable<Object> parsedOut = yml.loadAll(is);

                if (Iterables.size(parsedOut) > 0) {
                    result = true;
                    break;
                }
            } catch (Exception ex) {
                //do nothing
            }

            // final break 
            break;
        }

        is.reset(); // MUST BE AT THE END
        return result;
    }
}
