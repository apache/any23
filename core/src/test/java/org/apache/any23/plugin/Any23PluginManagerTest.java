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

package org.apache.any23.plugin;

import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.any23.extractor.ExtractorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Any23PluginManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Any23PluginManagerTest {

    private Any23PluginManager manager;

    @Before
    public void before() {
        manager = Any23PluginManager.getInstance();
    }

    @After
    public void after() {
        manager = null;
    }

    @Test
    public void testGetPlugins() throws IOException {
        Iterator<ExtractorFactory> extractorPlugins = manager.getExtractors();
        assertTrue(extractorPlugins.hasNext());
    }

    // TODO: move in FileUtils
    private void decompressJar(File jarFile, File destination) throws IOException {
        final int BUFFER = 1024 * 1024;
        BufferedOutputStream dest = null;
        FileOutputStream fos = null;
        ZipInputStream zis = null;
        final byte data[] = new byte[BUFFER];
        try {
            FileInputStream fis = new FileInputStream(jarFile);
            zis  = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                final File destinationFile = new File(destination, entry.getName() );
                if(entry.getName().endsWith("/")) {
                    destinationFile.mkdirs();
                } else {
                    fos = new FileOutputStream(destinationFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                        dest.flush();
                    }
                    dest.close();
                    fos.close();
                }
            }
        } finally {
            if(zis  != null) zis.close();
            if(dest != null) dest.close();
            if(fos  != null) fos.close();
        }
    }

}
