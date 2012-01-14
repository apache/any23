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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides specific application logger configuration methods.
 */
public class LogUtils {

    public static void setDefaultLogging() {
        Logger.getLogger("").setLevel(Level.WARNING);
        // Suppress silly cookie warnings.
        Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.SEVERE);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
    }

    public static void setVerboseLogging() {
        Logger.getLogger("").setLevel(Level.INFO);
        Logger.getLogger("org.apache.any23").setLevel(Level.ALL);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
    }

}
