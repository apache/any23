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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SettingsTest {

    @Test
    public void testNonNullSetting() {
        Setting<String> nonNull = Setting.newKey("nulltest", String.class).withValue("A nonnull string");
        try {
            nonNull.withValue(null);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }
    }

    @Test
    public void testNullableSetting() {
        Setting<String> nullable = Setting.newKey("nulltest", String.class).withValue(null);
        assertNull(nullable.withValue(null).getValue());
    }

    @Test
    public void testDuplicateIdentifiers() {
        try {
            Setting<String> first = Setting.newKey("foo", String.class).withValue("");
            Setting<String> second = Setting.newKey("foo", String.class).withValue("");

            Settings.of(first, second);

            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }
    }

    @Test
    public void testFind() {
        Setting<String> key = Setting.newKey("foo", String.class).withValue("key");
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
        Setting<String> key = Setting.newKey("foo", String.class).withValue("key");

        Setting<String> actual = key.withValue("actual");
        Settings settings = Settings.of(actual);

        assertSame(actual.getValue(), settings.get(key));
    }

    @Test
    public void testGetAbsentSetting() {
        Setting<String> key = Setting.newKey("foo", String.class).withValue("key");

        Setting<String> actual = Setting.newKey("foo", String.class).withValue("actual");
        Settings settings = Settings.of(actual);

        assertSame(key.getValue(), settings.get(key));
    }

    @Test
    public void testGetNullSetting() {
        Setting.Key<String> baseKey = Setting.newKey("foo", String.class);

        Settings settings = Settings.of(baseKey.withValue(null));
        assertNull(settings.get(baseKey.withValue("not null")));
    }

    @Test
    public void testSettingType() {
        assertEquals(CharSequence.class, Setting.newKey("foo", CharSequence.class).withValue("").getValueType());
        assertEquals(CharSequence.class, new Setting.Key<CharSequence>("foo"){}.withValue("").getValueType());

        Type mapType = new Setting.Key<Map<String, Integer>>(
                "foo"){}.withValue(Collections.emptyMap()).getValueType();

        assertTrue(mapType instanceof ParameterizedType);
        assertEquals("java.util.Map<java.lang.String, java.lang.Integer>", mapType.getTypeName());

        class Key0<Bar, V> extends Setting.Key<V> {
            Key0() {
                super("foo");
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
    public void testBadSetting() {
        try {
            new Setting.Key("foo") {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.newKey("foo", null);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.newKey(null, Integer.class);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.newKey(" ", Integer.class);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.newKey("foo", boolean.class);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            Setting.newKey("foo", Integer[].class);
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            new Setting.Key<Integer[]>("foo") {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        try {
            new Setting.Key<List<Integer>[]>("foo") {};
            fail();
        } catch (IllegalArgumentException e) {
            //test passes; ignore
        }

        class BadKeyCreator {
            private <V> void badKey() {
                new Setting.Key<V>("foo") {};
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
