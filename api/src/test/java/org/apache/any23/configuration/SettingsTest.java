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

import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SettingsTest {

    @Test
    public void testNonNullSetting() {
        Setting<String> nonNull = Setting.create("nulltest", "A nonnull string");
        try {
            nonNull.withValue(null);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }
    }

    @Test
    public void testNullableSetting() {
        Setting<String> nullable = Setting.create("nulltest", (String)null);
        assertNull(nullable.withValue(null).getValue());
    }

    @Test
    public void testDuplicateIdentifiers() {
        try {
            Setting<String> first = Setting.create("foo", "");
            Setting<String> second = Setting.create("foo", "");

            Settings.of(first, second);

            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }
    }

    @Test
    public void testFind() {
        Setting<String> key = Setting.create("foo", "key");
        Setting<String> element = key.withValue("element");

        Settings settings = Settings.of(element);

        Optional<Setting<String>> actual = settings.find(key);

        assertTrue(actual.isPresent());

        assertSame(element, actual.get());

        assertTrue(settings.contains(element));
        assertFalse(settings.contains(key));
    }

    @Test
    public void testGetPresentSetting() {
        Setting<String> key = Setting.create("foo", "key");

        Setting<String> actual = key.withValue("actual");
        Settings settings = Settings.of(actual);

        assertSame(actual.getValue(), settings.get(key));
    }

    @Test
    public void testGetAbsentSetting() {
        Setting<String> key = Setting.create("foo", "key");

        Setting<String> actual = Setting.create("foo", "actual");
        Settings settings = Settings.of(actual);

        assertSame(key.getValue(), settings.get(key));
    }

    @Test
    public void testGetNullSetting() {
        Setting<String> baseKey = Setting.create("foo", (String)null);

        Settings settings = Settings.of(baseKey);
        assertNull(settings.get(baseKey.withValue("not null")));

        //make sure we can go back to null
        baseKey.withValue("not null").withValue(null);
    }

    @Test
    public void testSettingType() {
        assertEquals(CharSequence.class, Setting.create("foo", CharSequence.class, "").getValueType());
        assertEquals(CharSequence.class, new Setting<CharSequence>("foo", ""){}.getValueType());

        Type mapType = new Setting<Map<String, Integer>>("foo", Collections.emptyMap()){}.getValueType();

        assertTrue(mapType instanceof ParameterizedType);
        assertEquals("java.util.Map<java.lang.String, java.lang.Integer>", mapType.getTypeName());

        class Key0<Bar, V> extends Setting<V> {
            Key0() {
                super("foo", null);
            }
        }

        class Key2<Baz, V, Bar> extends Key0<V, Bar> { }

        class Key3<V> extends Key2<Boolean, Integer, List<Optional<String>>> { }

        class Key4 extends Key3<Boolean> { }

        Type complicatedType = new Key4().withValue(Collections.emptyList()).getValueType();

        assertTrue(complicatedType instanceof ParameterizedType);
        assertEquals("java.util.List<java.util.Optional<java.lang.String>>", complicatedType.getTypeName());

        class Key3Simple<V> extends Key2<Boolean, Integer, String> { }

        class Key4Simple extends Key3Simple<Boolean> { }

        Type simpleType = new Key4Simple().withValue("").getValueType();

        assertEquals(String.class, simpleType);
    }

    @Test
    public void testCustomValueChecking() {
        class PositiveIntegerSetting extends Setting<Integer> {
            private PositiveIntegerSetting(String identifier, Integer defaultValue) {
                super(identifier, defaultValue);
            }

            @Override
            protected void checkValue(Integer newValue) throws Exception {
                if (newValue < 0) {
                    throw new NumberFormatException();
                }
            }

            @Override
            public String toString() {
                return getValue().toString();
            }
        }

        Setting<Integer> setting = new PositiveIntegerSetting("foo", 0);

        assertNotSame(setting, setting.withValue(0));
        assertEquals(setting, setting.withValue(0));

        setting = setting.withValue(5).withValue(6).withValue(7);

        assertEquals(setting.getClass(), PositiveIntegerSetting.class);
        assertEquals(setting.toString(), "7");

        try {
            setting.withValue(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof NumberFormatException);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBadSetting() {
        try {
            new Setting("foo", null) {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            new Setting<Integer>(null, null) {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.create(null, 0);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            new Setting<Integer>(" ", null) {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.create(" ", 0);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.create("foo", List.class, null);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.create("foo", boolean.class, null);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.create("foo", Integer[].class, null);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            new Setting<Integer[]>("foo", null) {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            new Setting<List<Integer>[]>("foo", null) {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        class BadKeyCreator {
            private <V> void badKey() {
                new Setting<V>("foo", null) {};
            }
        }

        try {
            new BadKeyCreator().badKey();
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }
    }


}
