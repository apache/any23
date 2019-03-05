/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.any23;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * This file encapsulates access to test resource files using temporary files
 * that are automatically cleaned up by JUnit after each test.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class AbstractAny23TestBase {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    protected File tempDirectory;

    public AbstractAny23TestBase() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        tempDirectory = testFolder.newFolder();
    }

    protected DocumentSource getDocumentSourceFromResource(
            String resourceLocation) throws IOException {
        return new FileDocumentSource(copyResourceToTempFile(resourceLocation));
    }

    protected DocumentSource getDocumentSourceFromResource(
            String resourceLocation, String baseUri) throws IOException {
        return new FileDocumentSource(copyResourceToTempFile(resourceLocation),
                baseUri);
    }

    /**
     * Copies a resource to a temporary directory and returns a file handle that
     * can be used to access the resource as a file from the temp directory.
     * 
     * @param resourceLocation
     *            The absolute location of the resource in the classpath, which
     *            can be used with this.getClass().getResourceAsStream.
     * @return temporary {@link java.io.File}
     * @throws FileNotFoundException if the temp file location cannot be converted to a {@link java.io.FileOutputStream}
     * @throws IOException if there is an issue with the input
     */
    protected File copyResourceToTempFile(String resourceLocation)
            throws FileNotFoundException, IOException {
        Assert.assertNotNull(
                "Temporary directory was null. Did you forget to call super.setUp() to initialise it?",
                tempDirectory);
        String fileEnding = resourceLocation.substring(resourceLocation
                .lastIndexOf("/") + 1);

        File tempFile = File.createTempFile("any23test-", "-" + fileEnding,
                tempDirectory);

        FileOutputStream output = new FileOutputStream(tempFile);

        InputStream input = this.getClass().getResourceAsStream(
                resourceLocation);

        Assert.assertNotNull(
                "Test resource was not found: " + resourceLocation, input);

        IOUtils.copy(input, output);

        return tempFile;
    }

}