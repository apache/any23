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

import com.beust.jcommander.Parameters;
import org.apache.any23.Any23OnlineTestBase;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 * Base class for <i>CLI</i> related tests.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class ToolTestBase extends Any23OnlineTestBase {

    public static final String TOOL_RUN_METHOD = "run";

    private final Class<? extends Tool> toolClazz;

    protected ToolTestBase(Class<? extends Tool> tool) {
        toolClazz = Objects.requireNonNull(tool, "Tool class cannot be null.");
    }

    /**
     * Runs the underlying tool.
     *
     * @param args tool arguments.
     * @return the tool exit code.
     * @throws Exception if there is an error asserting the test data.
     */
    protected int runTool(String... args) throws Exception {
        final String commandName = toolClazz.getAnnotation( Parameters.class ).commandNames()[0];

        final String[] enhancedArgs = new String[args.length + 1];
        enhancedArgs[0] = commandName;
        System.arraycopy( args, 0, enhancedArgs, 1, args.length );

        return new ToolRunner().execute(true, enhancedArgs);
    }

    /**
     * Runs the underlying tool.
     *
     * @param args args tool arguments.
     * @return the tool exit code.
     * @throws Exception if there is an error asserting the test data.
     */
    protected int runTool(String args) throws Exception {
        return runTool(args.split(" "));
    }

    /**
     * Runs the underlying tool and verify the exit code to <code>0</code>.
     *
     * @param args tool arguments.
     * @throws Exception if there is an error asserting the test data.
     */
    protected void runToolCheckExit0(String... args) throws Exception {
        assertEquals(
                format(
                        "Unexpected exit code for tool [%s] invoked with %s",
                        toolClazz.getSimpleName(),
                        Arrays.asList(args)
                ),
                0,
                runTool(args)
        );
    }

}
