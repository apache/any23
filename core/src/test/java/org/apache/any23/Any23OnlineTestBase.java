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

package org.apache.any23;

import org.junit.Assume;

/**
 * Base class for any <code>Any23</code> test class containing online tests
 * (test which require online resources to run).
 * This class excluded all online tests if JVM flag {@link #ONLINE_TEST_DISABLED_FLAG} is specified.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class Any23OnlineTestBase extends AbstractAny23TestBase {

    public static final String ONLINE_TEST_DISABLED_FLAG = "any23.online.test.disabled";

    /**
     * Check whether or not running online tests.
     */
    public static void assumeOnlineAllowed() {
        Assume.assumeTrue(System.getProperty(ONLINE_TEST_DISABLED_FLAG, null) == null);
    }
}
