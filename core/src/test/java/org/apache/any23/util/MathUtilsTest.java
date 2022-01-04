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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link org.apache.any23.util.MathUtils}
 *
 */
public class MathUtilsTest {

    /**
     * Test method for {@link org.apache.any23.util.MathUtils#md5(java.lang.String)}.
     */
    @Test
    public void testMd5() {
        String testString1 = "https://any23.apache.org";
        String expectedMd5String1 = "86f7ce8a90e269890b66ba13e6cdcf";
        assertEquals(expectedMd5String1, MathUtils.md5(testString1));
        String testString2 = "Apache Any23 is written in Java and licensed under the Apache "
                + "License v2.0. Apache Any23 can be used in various ways: As a library "
                + "in Java applications that consume structured data from the Web. As "
                + "a command-line tool for extracting and converting between the supported "
                + "formats. As online service API available at any23.org. ";
        String expectedMd5String2 = "d621f85f79fff905c17bd59e3cc61e1";
        assertEquals(expectedMd5String2, MathUtils.md5(testString2));
    }

}
