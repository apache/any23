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

package org.apache.any23.mime.purifier;

import org.apache.tika.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Reference test case for {@link WhiteSpacesPurifier}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class WhiteSpacesPurifierTest {

    private Purifier purifier;

    @Before
    public void setUp() {
        this.purifier = new WhiteSpacesPurifier();
    }

    @After
    public void tearDown() {
        this.purifier = null;
    }

    @Test
    public void testPurification() throws IOException {
        InputStream inputStream =
                new BufferedInputStream(this.getClass().getResourceAsStream("/application/xhtml/blank-file-header.xhtml"));
        this.purifier.purify(inputStream);
        Assert.assertNotNull(inputStream);
        Assert.assertTrue(
                validatePurification(
                       IOUtils.toString(inputStream)
                )
        );
        
    }

    /**
     * Checks if a {@link String} starts with a propert character.
     *  
     * @param string
     * @return
     */
    private boolean validatePurification(String string) {
        char firstChar = string.charAt(0);
        return (firstChar != '\t') && (firstChar != '\n') && (firstChar != ' ');
    }

}
