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

package org.deri.any23.extractor.microdata;

/**
 * Describes a possible value for a <b>Microdata item property</b>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
// TODO: improve datetime support.
public class ItemPropValue {

    /**
     * Supported types.
     */
    public enum Type {
        Plain,
        Link,
        DateTime,
        Nested
    }

    /**
     * Internal content value.
     */
    private final Object content;

    /**
     * Content type.
     */
    private final Type type;

    /**
     * Constructor.
     *
     * @param content content object.
     * @param type content type.
     */
    public ItemPropValue(Object content, Type type) {
        if(content == null) {
            throw new NullPointerException("content cannot be null.");
        }
        if(content instanceof String && ((String) content).trim().length() == 0) {
            throw new IllegalArgumentException("Invalid content '" + content + "'");
        }
        if(type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        if(type == Type.Nested && ! (content instanceof ItemScope) ) {
            throw new IllegalArgumentException(
                    "content must be an " + ItemScope.class + " when type is " + Type.Nested
            );
        }
        this.content = content;
        this.type = type;
    }

    /**
     * @return the content object.
     */
    public Object getContent() {
        return content;
    }

    /**
     * @return the content type.
     */
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format(
                "{ \"content\" : %s, \"type\" : \"%s\" }",
                content instanceof String ? "\"" + content + "\"" : content,
                type
        );
    }

    @Override
    public int hashCode() {
        return content.hashCode() * type.hashCode() * 2;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof ItemPropValue) {
            final ItemPropValue other = (ItemPropValue) obj;
            return content.equals(other.content) && type.equals(other.type);
        }
        return false;
    }

}
