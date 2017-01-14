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
 * Test case for {@link MimeDetector} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MimeDetectorTest extends ToolTestBase {

    public MimeDetectorTest() {
        super(MimeDetector.class);
    }

    @Test
    public void testDetectURL() throws Exception {
        assumeOnlineAllowed();
        runToolCheckExit0("http://twitter.com#micmos");
    }

    @Test
    public void testDetectFile() throws Exception {
        assumeOnlineAllowed();
        runToolCheckExit0("file://"+copyResourceToTempFile("/application/trix/test1.trx").getAbsolutePath());
    }

    @Test
    public void testDetectInline() throws Exception {
        assumeOnlineAllowed();
        runToolCheckExit0( new String[] {"inline://<http://s> <http://p> <http://o> ."} );
    }

}
