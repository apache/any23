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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents a {@link Setting.Key Key} paired with a compatible value.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public final class Setting<V> {

    /**
     * Convenience method for creating a new setting key with the specified identifier and value class.
     * If the desired value type is a {@link ParameterizedType} such as {@code List<String>},
     * or custom value-checking is required, then this method is not appropriate; instead,
     * extend the {@link Key} class directly.
     *
     * @param identifier a unique identifier for this key
     * @param valueType the type of value allowed by this key
     * @return a new {@link Key} instance initialized with the specified identifier and value type
     * @throws IllegalArgumentException if the identifier or value type is invalid
     */
    public static <V> Key<V> newKey(String identifier, Class<V> valueType) {
        return new Key<V>(identifier, valueType) {};
    }

    /**
     * Represents the key for a {@link Setting}.
     */
    public static abstract class Key<V> {
        private final String identifier;
        private final Type valueType;

        private Key(String identifier, Class<V> valueType) {
            this.identifier = checkIdentifier(identifier);
            if ((this.valueType = valueType) == null) {
                throw new IllegalArgumentException("value type cannot be null");
            }

            if (valueType.isArray()) {
                throw new IllegalArgumentException(identifier + " value class must be immutable");
            } else if (valueType.getTypeParameters().length != 0) {
                throw new IllegalArgumentException(identifier + " setting key must fill in type parameters for " + valueType.toGenericString());
            } else if (valueType.isPrimitive()) {
                //ensure using primitive wrapper classes
                //so that Class.isInstance(), etc. will work as expected
                throw new IllegalArgumentException(identifier + " value class cannot be primitive");
            }
        }

        private static final Pattern identifierPattern = Pattern.compile("[a-z][0-9a-z]*(\\.[a-z][0-9a-z]*)*");
        private static String checkIdentifier(String identifier) {
            if (identifier == null) {
                throw new IllegalArgumentException("identifier cannot be null");
            }
            if (!identifierPattern.matcher(identifier).matches()) {
                throw new IllegalArgumentException("identifier does not match " + identifierPattern.pattern());
            }
            return identifier;
        }

        /**
         * Constructs a new key with the specified identifier.
         * @param identifier the identifier for this key
         * @throws IllegalArgumentException if the identifier is invalid, or the value type was determined to be invalid
         */
        protected Key(String identifier) {
            this.identifier = checkIdentifier(identifier);

            Type type = valueType = getValueType();

            if (type instanceof Class) {
                if (((Class) type).isArray()) {
                    throw new IllegalArgumentException(identifier + " value class must be immutable");
                } else if (((Class) type).getTypeParameters().length != 0) {
                    throw new IllegalArgumentException(identifier + " setting key must fill in type parameters for " + ((Class) type).toGenericString());
                }
            } else if (type instanceof GenericArrayType) {
                throw new IllegalArgumentException(identifier + " value class must be immutable");
            } else if (type instanceof TypeVariable) {
                throw new IllegalArgumentException("Invalid setting key type 'Key<" + type.getTypeName() + ">' for identifier " + identifier);
            } else if (!(type instanceof ParameterizedType)) {
                throw new IllegalArgumentException(identifier + " invalid key type " + type + " (" + type.getClass().getName() + ")");
            }
        }

        private Type getValueType() {
            HashMap<TypeVariable<?>, Type> mapping = new HashMap<>();
            Class<?> rawType = getClass();
            assert rawType != Key.class;
            for (;;) {
                Type superclass = rawType.getGenericSuperclass();
                if (superclass instanceof ParameterizedType) {
                    rawType = (Class)((ParameterizedType) superclass).getRawType();
                    Type[] args = ((ParameterizedType) superclass).getActualTypeArguments();
                    if (Key.class.equals(rawType)) {
                        Type t = args[0];
                        return mapping.getOrDefault(t, t);
                    }
                    TypeVariable<?>[] vars = rawType.getTypeParameters();
                    for (int i = 0, len = vars.length; i < len; i++) {
                        Type t = args[i];
                        mapping.put(vars[i], t instanceof TypeVariable ? mapping.get(t) : t);
                    }
                } else {
                    rawType = (Class<?>)superclass;
                    if (Key.class.equals(rawType)) {
                        throw new IllegalArgumentException(getClass() + " does not supply type arguments");
                    }
                }
            }
        }

        /**
         * Subclasses may override this method to check that new settings for this key are valid.
         * The default implementation of this method throws a {@link NullPointerException} if the new value is null and the initial value was non-null.
         *
         * @param initial the setting containing the initial value for this key, or null if the setting has not yet been initialized
         * @param newValue the new value for this setting
         * @throws Exception if the new value for this setting was invalid
         */
        protected void checkValue(Setting<V> initial, V newValue) throws Exception {
            if (newValue == null && initial != null && initial.value != null) {
                throw new NullPointerException();
            }
        }

        private Setting<V> checked(Setting<V> origin, V value) {
            try {
                checkValue(origin, value);
            } catch (Exception e) {
                throw new IllegalArgumentException("invalid value for key '" + identifier + "': " + value, e);
            }
            return new Setting<>(this, value);
        }

        /**
         * @return a new {@link Setting} object with this key and the supplied value.
         *
         * @throws IllegalArgumentException if the new value was invalid, as determined by:
         * <pre>
         *      {@code this.checkValue(null, value)}
         * </pre>
         *
         * @see #checkValue(Setting, Object)
         */
        public final Setting<V> withValue(V value) {
            return checked(null, value);
        }

        /**
         * @param o the object to check for equality
         * @return {@code this == o}
         */
        public final boolean equals(Object o) {
            return super.equals(o);
        }

        /**
         * @return the identity-based hashcode of this key
         */
        public final int hashCode() {
            return super.hashCode();
        }

        public String toString() {
            return identifier + ": " + valueType.getTypeName();
        }
    }

    private final Key<V> key;
    private final V value;

    private Setting(Key<V> key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the identifier for this setting
     */
    public String getIdentifier() {
        return key.identifier;
    }

    /**
     * @return the value for this setting
     */
    public V getValue() {
        return value;
    }

    /**
     * @return the type of value supported for this setting
     */
    public Type getValueType() {
        return key.valueType;
    }

    /**
     * @return the supplied setting, if it has the same key as this setting
     */
    @SuppressWarnings("unchecked")
    public final Optional<Setting<V>> cast(Setting<?> setting) {
        return setting == null || setting.key != this.key ? Optional.empty() : Optional.of((Setting<V>)setting);
    }

    /**
     * @return a new {@link Setting} object with this setting's {@link Key Key} and the supplied value.
     *
     * @throws IllegalArgumentException if the new value was invalid, as determined by:
     * <pre>
     *     {@code this.key.checkValue(this, newValue)}
     * </pre>
     *
     * @see Key#checkValue(Setting, Object)
     */
    public Setting<V> withValue(V newValue) {
        return key.checked(this, newValue);
    }

    /**
     * @return true if the supplied object is an instance of {@link Setting} and has the same key and value as this object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Setting)) return false;

        Setting<?> setting = (Setting<?>) o;

        if (key != setting.key) return false;
        return value != null ? value.equals(setting.value) : setting.value == null;
    }

    @Override
    public int hashCode() {
        return 31 * key.hashCode() + (value != null ? value.hashCode() : 0);
    }

    @Override
    public String toString() {
        return key.identifier + "=" + value;
    }

}
