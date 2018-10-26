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
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents a setting key paired with a compatible value.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public abstract class Setting<V> implements Cloneable {

    private final Key<V> key;
    private V value;

    /**
     * Constructs a new setting with the specified identifier and default value. This constructor must be called
     * with concrete type arguments.
     * @param identifier the identifier for this setting
     * @param defaultValue the default value for this setting
     * @throws IllegalArgumentException if the identifier or any of the type arguments were invalid
     */
    protected Setting(String identifier, V defaultValue) {
        checkIdentifier(identifier);
        this.key = new Key<>(identifier, lookupValueType(getClass(), identifier), defaultValue);
        this.value = defaultValue;
    }

    /**
     * @return the identifier for this setting
     */
    public final String getIdentifier() {
        return key.identifier;
    }

    /**
     * Subclasses may override this method to check that new values for this setting are valid.
     * The default implementation of this method throws a {@link NullPointerException} if the new value is null and the default value is non-null.
     *
     * @param newValue the new value for this setting
     * @throws Exception if the new value for this setting is invalid
     */
    protected void checkValue(V newValue) throws Exception {
        if (newValue == null && key.defaultValue != null) {
            throw new NullPointerException();
        }
    }

    /**
     * @return the value for this setting
     */
    public final V getValue() {
        return value;
    }

    /**
     * @return the default value for this setting
     */
    public final V getDefaultValue() {
        return key.defaultValue;
    }

    /**
     * @return the type of value supported for this setting
     */
    public final Type getValueType() {
        return key.valueType;
    }

    /**
     * @return this setting, if this setting has the same key as the supplied setting
     */
    @SuppressWarnings("unchecked")
    public final <S extends Setting<?>> Optional<S> as(S setting) {
        return key == ((Setting<?>)setting).key ? Optional.of((S)this) : Optional.empty();
    }

    /**
     * @return a new {@link Setting} object with this setting's key and the supplied value.
     *
     * @throws IllegalArgumentException if the new value was invalid, as determined by:
     * <pre>
     *     {@code this.checkValue(newValue)}
     * </pre>
     *
     * @see Setting#checkValue(Object)
     */
    public final Setting<V> withValue(V newValue) {
        return clone(this, newValue);
    }

    @Override
    protected final Object clone() {
        try {
            //ensure no subclasses override this incorrectly
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @return true if the supplied object is an instance of {@link Setting}
     * and has the same key and value as this setting.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Setting)) return false;

        Setting<?> setting = (Setting<?>) o;
        return key == setting.key && Objects.equals(value, setting.value);
    }

    @Override
    public final int hashCode() {
        return key.hashCode() ^ Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return key.identifier + "=" + value;
    }



    ///////////////////////////////////////
    // Private static helpers
    ///////////////////////////////////////

    private static final class Key<V> {
        final String identifier;
        final Type valueType;
        final V defaultValue;

        Key(String identifier, Type valueType, V defaultValue) {
            this.identifier = identifier;
            this.valueType = valueType;
            this.defaultValue = defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    private static <V, S extends Setting<V>> S clone(S setting, V newValue) {
        try {
            setting.checkValue(newValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid value for key '"
                    + ((Setting<V>)setting).key.identifier + "': " + ((Setting<V>)setting).value, e);
        }

        //important to clone so that we can retain checkValue(), toString() behavior on returned instance
        S s = (S)setting.clone();

        assert ((Setting<V>)s).key == ((Setting<V>)setting).key;
        assert ((Setting<V>)s).getClass().equals(setting.getClass());

        ((Setting<V>)s).value = newValue;
        return s;
    }

    private static final Pattern identifierPattern = Pattern.compile("[a-z][0-9a-z]*(\\.[a-z][0-9a-z]*)*");
    private static void checkIdentifier(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("identifier cannot be null");
        }
        if (!identifierPattern.matcher(identifier).matches()) {
            throw new IllegalArgumentException("identifier does not match " + identifierPattern.pattern());
        }
    }

    private static Type lookupValueType(Class<?> rawType, String identifier) {
        HashMap<TypeVariable<?>, Type> mapping = new HashMap<>();
        assert rawType != Setting.class;
        for (;;) {
            Type superclass = rawType.getGenericSuperclass();
            if (superclass instanceof ParameterizedType) {
                rawType = (Class)((ParameterizedType) superclass).getRawType();
                Type[] args = ((ParameterizedType) superclass).getActualTypeArguments();
                if (Setting.class.equals(rawType)) {
                    Type type = args[0];
                    type = mapping.getOrDefault(type, type);
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
                    return type;
                }
                TypeVariable<?>[] vars = rawType.getTypeParameters();
                for (int i = 0, len = vars.length; i < len; i++) {
                    Type t = args[i];
                    mapping.put(vars[i], t instanceof TypeVariable ? mapping.get(t) : t);
                }
            } else {
                rawType = (Class<?>)superclass;
                if (Setting.class.equals(rawType)) {
                    throw new IllegalArgumentException(rawType + " does not supply type arguments");
                }
            }
        }
    }

}
