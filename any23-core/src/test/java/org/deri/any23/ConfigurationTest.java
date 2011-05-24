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

package org.deri.any23;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link org.deri.any23.Configuration} class.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class ConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = Configuration.instance();
    }

    @After
    public void tearDown() {
        configuration = null;
    }

    @Test
    public void testGetProperties() {
        final String[] properties = configuration.getProperties();
        Assert.assertEquals(7, properties.length);
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
        configuration.getPropertyIntOrFail("any23.http.user.agent.name");
    }

    @Test
    public void testGetFlagProperty() {
        Assert.assertTrue( configuration.getFlagProperty("any23.extraction.metadata") );
    }

}
