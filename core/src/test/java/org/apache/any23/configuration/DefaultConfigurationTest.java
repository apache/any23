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

package org.apache.any23.configuration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Configuration} class.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class DefaultConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = DefaultConfiguration.singleton();
    }

    @After
    public void tearDown() {
        configuration = null;
    }

    @Test
    public void testSingletonAccessor() {
        final Configuration s1 = DefaultConfiguration.singleton();
        final Configuration s2 = DefaultConfiguration.singleton();
        Assert.assertTrue("Invalid singleton condition.", s1 == s2);
    }

    @Test
    public void testCopyAccessor() {
        final ModifiableConfiguration c1 = DefaultConfiguration.copy();
        final ModifiableConfiguration c2 = DefaultConfiguration.copy();
        Assert.assertTrue("Invalid copy condition.", c1 != c2);
    }

    @Test
    public void testCopyIsolation() {
        final String TARGET_PROPERTY = "any23.http.client.max.connections";
        final ModifiableConfiguration copy = DefaultConfiguration.copy();
        copy.setProperty(TARGET_PROPERTY, "100");
        Assert.assertEquals( DefaultConfiguration.singleton().getPropertyIntOrFail(TARGET_PROPERTY), 5);
        Assert.assertEquals( copy.getPropertyIntOrFail(TARGET_PROPERTY), 100 );
    }

    @Test
    public void testModifiableConfigurationSuccess() {
        final String TARGET_PROPERTY = "any23.extraction.metadata.nesting";
        final ModifiableConfiguration modifiable = DefaultConfiguration.copy();
        final String oldValue = modifiable.setProperty(TARGET_PROPERTY, "off");
        Assert.assertEquals("on", oldValue);
        Assert.assertEquals("off", modifiable.getPropertyOrFail(TARGET_PROPERTY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModifiableConfigurationFail() {
        final ModifiableConfiguration modifiable = DefaultConfiguration.copy();
        modifiable.setProperty("fake.property", "fake.value");
    }

    @Test
    public void testGetProperties() {
        final String[] properties = configuration.getProperties();
        Assert.assertTrue(properties.length > 6);
        for(String property : properties) {
            Assert.assertTrue(property.startsWith("any23."));
        }
    }

    @Test
    public void testDefineProperty() {
        Assert.assertTrue( configuration.defineProperty("any23.core.version") );
        Assert.assertFalse( configuration.defineProperty("any23.fake") );
    }

    @Test
    public void testGetProperty() {
        final String value1 = configuration.getProperty("any23.rdfa.extractor.xslt", null);
        Assert.assertEquals("rdfa.xslt", value1);
        final String value2 = configuration.getProperty("any23.fake", "fake.default");
        Assert.assertEquals("fake.default", value2);
    }

    @Test
    public void testGetPropertySysOverride() {
        System.setProperty("any23.rdfa.extractor.xslt", "fake.rdfa.xslt");
        try {
            final String value = configuration.getPropertyOrFail("any23.rdfa.extractor.xslt");
            Assert.assertEquals("fake.rdfa.xslt", value);
        } finally {
            System.clearProperty("any23.rdfa.extractor.xslt");
        }
    }

    @Test
    public void testGetPropertyOrFailOk() {
        final String value = configuration.getPropertyOrFail("any23.rdfa.extractor.xslt");
        Assert.assertEquals("rdfa.xslt", value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyOrFailNok() {
        configuration.getPropertyOrFail("any23.fake");
    }

    @Test
    public void testGetPropertyIntOrFailOk() {
        final int value = configuration.getPropertyIntOrFail("any23.http.client.timeout");
        Assert.assertEquals(10000, value);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetPropertyIntOrFailNok() {
        configuration.getPropertyIntOrFail("any23.http.user.agent.default");
    }

    @Test
    public void testGetFlagProperty() {
        Assert.assertTrue( configuration.getFlagProperty("any23.extraction.metadata.nesting") );
    }

}
