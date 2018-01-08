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

package org.apache.any23.cli;

import org.junit.Test;

/**
 * Test case for {@link ExtractorDocumentation} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractorDocumentationTest extends ToolTestBase {

    private static final String TARGET_EXTRACTOR = "html-microdata";

    public ExtractorDocumentationTest() {
        super(ExtractorDocumentation.class);
    }

    @Test
    public void testList() throws Exception {
        runToolCheckExit0("--list");
    }

    @Test
    public void testAll() throws Exception {
        runToolCheckExit0("--all");
    }

    @Test
    public void testExampleInput() throws Exception {
        runToolCheckExit0("-i", TARGET_EXTRACTOR);
    }

    @Test
    public void testExampleOutput() throws Exception {
        runToolCheckExit0("-o", TARGET_EXTRACTOR);
    }

}
