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
package org.apache.any23.yaml.utils;

import org.apache.any23.extractor.yaml.YAMLValidator;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jacek Grzebyta ( grzebyta.dev [at] gmail.com)
 */
@RunWith(Parameterized.class)
public class YAMLValidatorTest {

    private String path;

    private Boolean expected;

    private Logger log = LoggerFactory.getLogger(getClass());

    public YAMLValidatorTest(String path, Boolean expected) {
        this.path = path;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getFiles() {
        return Arrays.asList(new Object[][]{
            {"/org/apache/any23/extractor/yaml/simple-load.yml", Boolean.TRUE},
            {"/org/apache/any23/extractor/yaml/simple-load_no_head.yml", Boolean.TRUE},
            {"/org/apache/any23/extractor/yaml/different-integers.yml", Boolean.TRUE},
            {"/org/apache/any23/extractor/yaml/different-float.yml", Boolean.TRUE},
            {"/org/apache/any23/extractor/csv/test-comma.csv", Boolean.TRUE}});
    }

    @Test
    public void runTest()
            throws Exception {
        log.info("Try path: {}", path);
        InputStream is = YAMLValidatorTest.class.getResourceAsStream(path);
        boolean result = YAMLValidator.isYAML(is);
        log.debug("Test resutl: {}", result);
        Assert.assertSame(expected, result);

    }
}
