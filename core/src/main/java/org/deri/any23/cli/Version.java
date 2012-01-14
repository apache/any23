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

package org.deri.any23.cli;

import org.deri.any23.Any23;

/**
 * Prints out the <b>Any23</b> library version.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@ToolRunner.Description("Prints out the current library version and configuration information.")
public class Version implements Tool {

    public static void main(String[] args) {
        System.exit( new Version().run(args) );
    }

    public int run(String[] args) {
        final String version = Any23.VERSION;
        if(version == null) {
            System.err.println("Error while retrieving configuration info.");
            return 1;
        }
        System.out.println(String.format("Any23 Core v. %s", version));
        System.out.println();
        return 0;
    }

}
