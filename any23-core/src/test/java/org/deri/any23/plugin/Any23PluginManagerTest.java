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

package org.deri.any23.plugin;

import org.deri.any23.cli.Tool;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Test case for {@link Any23PluginManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Any23PluginManagerTest {

    private static final File TARGET_TEST_JAR = new File("src/test/resources/org/deri/any23/plugin/target.jar");

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
    public <T> void testGetClassesInPackageFromJAR() throws IOException {
        Set<Class<T>> classes = new HashSet<Class<T>>();
                manager.getClassesInPackageFromJAR(
                        TARGET_TEST_JAR,
                        "org.hsqldb.store",
                        null,
                        classes
                );
        Assert.assertEquals(6, classes.size());
    }

    @Test
    public <T> void testGetClassesInPackageFromDir() throws IOException {
        final File tmpDir = File.createTempFile("test-plugin-manager", ".decompressed");
        tmpDir.delete();
        tmpDir.mkdirs();
        decompressJar(TARGET_TEST_JAR, tmpDir);

        Set<Class<T>> classes = new HashSet<Class<T>>();
        manager.getClassesInPackageFromDir(
                tmpDir,
                "org.hsqldb.store",
                null,
                classes
        );
        Assert.assertEquals(6, classes.size());
    }

    @Test
    public <T> void testGetClassesFromClasspath() throws IOException {
        Set<Class<T>> clazzes = manager.getClassesInPackage("org.deri.any23", null);
        Assert.assertEquals(188, clazzes.size());
    }

    @Test
    public void testGetTools() throws IOException {
        Class<Tool>[] tools = manager.getTools();
        Assert.assertEquals(7, tools.length);
    }

    @Test
    public void testGetPlugins() throws IOException {
        Class<ExtractorPlugin>[] extractorPlugins = manager.getPlugins();
        Assert.assertEquals(0, extractorPlugins.length);
    }

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
