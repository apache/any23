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

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link ToolRunner}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ToolRunnerTest {

    private final Set<Class<? extends Tool>> coreTools = new HashSet<Class<? extends Tool>>(){{
        add(ExtractorDocumentation.class);
        add(MicrodataParser.class);
        add(MimeDetector.class);
        add(PluginVerifier.class);
        add(Rover.class);
        add(VocabPrinter.class);
    }};

    @Test
    public void testGetToolsInClasspath() throws IOException {
        Iterator<Tool> tools = new ToolRunner().getToolsInClasspath();
        assertTrue("No core tools have been detected", tools.hasNext());
        while (tools.hasNext()) {
            assertTrue("Some core tools have not been detected.", coreTools.contains(tools.next().getClass()));
        }
    }

    @Test
    public void testGetVersion() throws Exception {
        Assert.assertEquals(0, new ToolRunner().execute("-v") );
    }

    @Test
    public void testGetHelp() throws Exception {
        Assert.assertEquals(0, new ToolRunner().execute("-h") );
    }

}
