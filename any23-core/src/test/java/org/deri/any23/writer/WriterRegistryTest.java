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

package org.deri.any23.writer;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test case for {@link WriterRegistry}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WriterRegistryTest {

    private static final int NUM_OF_WRITERS = 7;

    private final WriterRegistry target = WriterRegistry.getInstance();

    @Test
    public void testGetIdentifiers() {
        final String[] ids = target.getIdentifiers();
        Assert.assertTrue(ids.length >= NUM_OF_WRITERS);
        assertUnique(ids);
    }

    @Test
    public void testHasIdentifier() {
        Assert.assertTrue( target.hasIdentifier( target.getIdentifiers()[0] ) );
    }

    @Test
    public void testGetMimeTypes() {
        final String[] mimeTypes = target.getMimeTypes();
        Assert.assertTrue(mimeTypes.length > 0);
    }

    @Test
    public void testGetWriters() {
        Assert.assertTrue( target.getWriters().length >= NUM_OF_WRITERS);
    }

    @Test
    public void testGetWriterByIdentifier() {
        final String[] ids = target.getIdentifiers();
        for(String id : ids) {
            Assert.assertNotNull( target.getWriterByIdentifier(id) );
        }
    }

    @Test
    public void testGetWriterInstanceByIdentifier() {
        final String[] ids = target.getIdentifiers();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(String id : ids) {
            Assert.assertNotNull( target.getWriterInstanceByIdentifier(id, baos) );
        }
    }

    @Test
    public void testGetWritersByMimeType() {
        final Set<Class<? extends FormatWriter>> set = new HashSet<Class<? extends FormatWriter>>();
        final String[] mimeTypes = target.getMimeTypes();
        for(String mimeType : mimeTypes) {
            set.addAll( Arrays.asList(target.getWritersByMimeType(mimeType)) );
        }
        Assert.assertEquals( NUM_OF_WRITERS, set.size() );
    }

    private void assertUnique(String[] list) {
        final Set<String> set = new HashSet<String>();
        for(String elem : list) {
            if(set.contains(elem))
                Assert.fail("Element " + elem + " already defined.");
            set.add(elem);
        }
    }

}
