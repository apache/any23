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

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link MicrodataParser} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParserTest extends ToolTestBase {

    public MicrodataParserTest() {
        super(MicrodataParser.class);
    }

    @Test
    public void testRunOnFile() throws Exception {
        runToolCheckExit0("file:"+copyResourceToTempFile("/microdata/microdata-nested.html").getAbsolutePath());
    }
    
    @Ignore("ANY23-140 - Revise Any23 tests to remove fetching of web content")
    @Test
    public void testRunOnHTTPResource() throws Exception {
        runToolCheckExit0("http://www.imdb.com/title/tt1375666/");
    }
    

}
