/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.cli;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        add(Version.class);
        add(VocabPrinter.class);
    }};

    @Test
    public void testGetToolsInClasspath() throws IOException {
        Class<Tool>[] tools = ToolRunner.getToolsInClasspath();
        Assert.assertTrue("Some core tools have not been detected.", coreTools.containsAll(Arrays.asList(tools)));
    }

}
