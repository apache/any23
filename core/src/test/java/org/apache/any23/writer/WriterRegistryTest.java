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

package org.apache.any23.writer;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.any23.configuration.Settings;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WriterFactoryRegistry}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WriterRegistryTest {

    private static final int NUM_OF_WRITERS = 8;

    private final WriterFactoryRegistry target = WriterFactoryRegistry.getInstance();

    @Test
    public void testGetIdentifiers() {
        final List<String> ids = target.getIdentifiers();
        Assert.assertTrue(ids.size() >= NUM_OF_WRITERS);
        assertUnique(ids);
    }

    @Test
    public void testHasIdentifier() {
        Assert.assertTrue( target.hasIdentifier( target.getIdentifiers().get(0) ) );
    }

    @Test
    public void testGetMimeTypes() {
        final Collection<String> mimeTypes = target.getMimeTypes();
        Assert.assertTrue(mimeTypes.size() > 0);
    }

    @Test
    public void testGetWriters() {
        Assert.assertTrue( target.getWriters().size() >= NUM_OF_WRITERS);
    }

    @Test
    public void testGetWriterByIdentifier() {
        final List<String> ids = target.getIdentifiers();
        for(String id : ids) {
            Assert.assertNotNull( target.getWriterByIdentifier(id) );
        }
    }

    @Test
    public void testGetWriterInstanceByIdentifier() {
        final List<String> ids = target.getIdentifiers();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final CompositeTripleHandler delegate = new CompositeTripleHandler();
        for (String id : ids) {
            WriterFactory f = target.getWriterByIdentifier(id);
            if (f instanceof TripleWriterFactory) {
                Assert.assertNotNull(((TripleWriterFactory) f).getTripleWriter(baos, Settings.of()));
            } else if (f instanceof DecoratingWriterFactory) {
                Assert.assertNotNull(((DecoratingWriterFactory) f).getTripleWriter(delegate, Settings.of()));
            } else {
                Assert.fail(id + " is not a valid writer factory");
            }
        }
    }

    @Test
    public void testGetWritersByMimeType() {
        final Set<WriterFactory> set = new HashSet<WriterFactory>();
        final Collection<String> mimeTypes = target.getMimeTypes();
        for(String mimeType : mimeTypes) {
            set.addAll( target.getWritersByMimeType(mimeType) );
        }
        Assert.assertEquals( NUM_OF_WRITERS, set.size() );
    }

    private void assertUnique(List<String> list) {
        final Set<String> set = new HashSet<String>();
        for(String elem : list) {
            if(set.contains(elem))
                Assert.fail("Element " + elem + " already defined.");
            set.add(elem);
        }
    }

}
