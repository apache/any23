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

package org.apache.any23.extractor.microdata;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.any23.util.StringUtils;

/**
 * Describes a possible value for a <b>Microdata item property</b>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ItemPropValue {

    /**
     * Internal content value.
     */
    private final Object content;

    /**
     * Content type.
     */
    private final Type type;

    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<>();

    /**
     * Supported types.
     */
    public enum Type {
        Plain,
        Link,
        Date,
        Nested
    }

    public static Date parseDateTime(String dateStr) throws ParseException {
        return getSdf().parse(dateStr);
    }

    public static String formatDateTime(Date in) {
        return getSdf().format(in);
    }
    
    private static SimpleDateFormat getSdf() {
        SimpleDateFormat simpleDateFormat = sdf.get();
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            sdf.set(simpleDateFormat);
        }
        return simpleDateFormat;
    }

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
        if(type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        if(type == Type.Nested && ! (content instanceof ItemScope) ) {
            throw new IllegalArgumentException(
                    "content must be an " + ItemScope.class + " when type is " + Type.Nested
            );
        }
        if(type == Type.Date && !(content instanceof Date) ) {
            throw new IllegalArgumentException(
                    "content must be a " + Date.class.getName() + " whe type is " + Type.Date
            );
        }
        if(content instanceof String && ((String) content).trim().length() == 0) {
            // ANY23-115 Empty spans seem to break ANY23
            // instead of throwing the exception and in effect failing the entire
            // parse job we wish to be lenient on web content publishers and add
            // Null (String) as content.
            content = "Null";
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

   /**
     * @return <code>true</code> if type is plain text.
     */
    public boolean isPlain() {
        return type == Type.Plain;
    }

    /**
     * @return <code>true</code> if type is a link.
     */
    public boolean isLink() {
        return type == Type.Link;
    }

    /**
     * @return <code>true</code> if type is a date.
     */
    public boolean isDate() {
        return type == Type.Date;
    }

    /**
     * @return <code>true</code> if type is a nested {@link ItemScope}.
     */
    public boolean isNested() {
        return type == Type.Nested;
    }

    /**
     * @return <code>true</code> if type is an integer.
     */
    public boolean isInteger() {
        if(type != Type.Plain)
            return false;
         try {
             Integer.parseInt((String) content);
             return true;
         } catch (Exception e) {
             return false;
         }
     }

    /**
     * @return <code>true</code> if type is a float.
     */
     public boolean isFloat() {
         if(type != Type.Plain)
             return false;
         try {
             Float.parseFloat((String) content);
             return true;
         } catch (Exception e) {
             return false;
         }
     }

    /**
     * @return <code>true</code> if type is a number.
     */
     public boolean isNumber() {
         return isInteger() || isFloat();
     }

    /**
     * @return the content value as integer, or raises an exception.
     * @throws NumberFormatException if the content is not an integer.
     * @throws ClassCastException if content is not plain.
     */
     public int getAsInteger() {
         return Integer.parseInt((String) content);
     }

    /**
     * @return the content value as float, or raises an exception.
     * @throws NumberFormatException if the content is not an float.
     * @throws ClassCastException if content is not plain.
     */
     public float getAsFloat() {
         return Float.parseFloat((String) content);
     }


    /**
     * @return the content as {@link Date}
     *         if <code>type == Type.DateTime</code>,
     * @throws ClassCastException if content is not a valid date.
     */
    public Date getAsDate() {
        return (Date) content;
    }

    /**
     * @return the content value as URL, or raises an exception.
     */
    public URL getAsLink() {
        try {
            return new URL((String) content);
        } catch (MalformedURLException murle) {
            throw new IllegalStateException("Error while parsing IRI.", murle);
        }
    }

    /**
     * @return the content value as {@link ItemScope}.
     */
    public ItemScope getAsNested() {
        return (ItemScope) content;
    }

    public String toJSON() {
        String contentStr;
        if(content instanceof String) {
            contentStr = "\"" + StringUtils.escapeAsJSONString((String) content) + "\"";
        } else if(content instanceof Date) {
            contentStr = "\"" + getSdf().format((Date) content) + "\"";
        } else {
            contentStr = content.toString();
        }

        return String.format( "{ \"content\" : %s, \"type\" : \"%s\" }", contentStr, type );
    }

    @Override
    public String toString() {
        return toJSON();
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
