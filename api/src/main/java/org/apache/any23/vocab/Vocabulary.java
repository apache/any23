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

package org.apache.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Base class for the definition of a vocabulary.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public abstract class Vocabulary {

    /**
     * Allows to add comments to <code>namespaces</code>,
     * <code>classes</code> and <code>properties</code>.
     */
    @Target({FIELD})
    @Retention(RUNTIME)
    @interface Comment {
        String value();
    }

    /**
     * Vocabulary namespace.
     */
    private final URI namespace;

    /**
     * Map of vocabulary resources.
     */
    private Map<String,URI> classes;

    /**
     * Map of vocabulary properties.
     */
    private Map<String,URI> properties;

    /**
     * Map any resource with the relative comment.
     */
    private Map<URI,String> resourceToCommentMap;

    /**
     * Constructor.
     *
     * @param namespace the namespace URI prefix.
     */
    public Vocabulary(String namespace) {
        try {
        this.namespace =  ValueFactoryImpl.getInstance().createURI(namespace);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid namespace '" + namespace + "'", e);
        }
    }

    /**
     * @return the namespace associated to this vocabulary.
     */
    public URI getNamespace() {
        return namespace;
    }

    /**
     * Returns a class defined within this vocabulary.
     *
     * @param name class name.
     * @return the URI associated to such resource.
     */
    public URI getClass(String name) {
        URI res = classes.get(name);
        if (null == res) {
            throw new IllegalArgumentException("Unknown resource name '" + name + "'");
        }
        return res;
    }

    /**
     * Returns a property defined within this vocabulary.
     *
     * @param name property name.
     * @return the URI associated to such property.
     */
    public URI getProperty(String name) {
        URI prop = properties.get(name);
        if (null == prop) {
            throw new IllegalArgumentException("Unknown property name '" + name + "'");
        }
        return prop;
    }

    /**
     * Returns a property defined within this vocabulary, if not found the
     * <code>defaultValue</code> will be returned.
     *
     * @param name property name.
     * @param defaultValue the default value if property name not found.
     * @return the URI associated to such property.
     */
    public URI getProperty(String name, URI defaultValue) {
        URI prop = properties.get(name);
        if (null == prop) {
            return defaultValue;
        }
        return prop;
    }

    /**
     * Returns the property URI for the specified property string.
     * If the string contains a list of words separated by blank chars,
     * such words are merged and camel case separated.
     *
     * @param property property name.
     * @return property URI.
     */
    public URI getPropertyCamelCase(String property) {
        String[] names = property.split("\\W");
        String camelCase = names[0];
        for (int i = 1; i < names.length; i++) {
            String tmp = names[i];
            camelCase += tmp.replaceFirst("(.)", tmp.substring(0, 1).toUpperCase());
        }
        return getProperty(camelCase);
    }

    /**
     * @return the list of all defined classes.
     */
    public URI[] getClasses() {
        if(classes == null) {
            return new URI[0];
        }
        final Collection<URI> uris = classes.values();
        return uris.toArray( new URI[ uris.size() ] );
    }

    /**
     * @return the list of all defined properties.
     */
    public URI[] getProperties() {
        if(properties == null) {
            return new URI[0];
        }
        final Collection<URI> uris = properties.values();
        return uris.toArray( new URI[ uris.size() ] );
    }

    /**
     * Returns all the defined comments for resources.
     *
     * @return unmodifiable list of comments.
     */
    public Map<URI,String> getComments() {
        fillResourceToCommentMap();
        return Collections.unmodifiableMap(resourceToCommentMap);
    }

    /**
     * Returns the comment for the given resource.
     *
     * @param resource input resource to have a comment.
     * @return the human readable comment associated to the
     *         given resource.
     */
    public String getCommentFor(URI resource) {
        fillResourceToCommentMap();
        return resourceToCommentMap.get(resource);
    }
    
    /**
     * Creates a URI.
     *
     * @param uriStr the URI string
     * @return the URI instance.
     */
    protected URI createURI(String uriStr) {
        return ValueFactoryImpl.getInstance().createURI(uriStr);
    }

    /**
     * Creates a resource and register it to the {@link #classes} map.
     *
     * @param namespace vocabulary namespace.
     * @param resource name of the resource.
     * @return the created resource URI.
     */
    protected URI createClass(String namespace, String resource) {
        URI res = createURI(namespace, resource);
        if(classes == null) {
            classes = new HashMap<String, URI>(10);
        }
        classes.put(resource, res);
        return res;
    }

    /**
     * Creates a property and register it to the {@link #properties} map.
     *
     * @param namespace vocabulary namespace.
     * @param property name of the property.
     * @return the created property URI.
     */
    protected URI createProperty(String namespace, String property) {
        URI res = createURI(namespace, property);
        if(properties == null) {
            properties = new HashMap<String, URI>(10);
        }
        properties.put(property, res);
        return res;
    }

    /**
     * Creates a URI.
     *
     * @param namespace
     * @param localName
     * @return
     */
    private URI createURI(String namespace, String localName) {
        return ValueFactoryImpl.getInstance().createURI(namespace, localName);
    }

    private void fillResourceToCommentMap() {
        if(resourceToCommentMap != null) return;
        final Map<URI,String> newMap = new HashMap<URI, String>();
        for (Field field : this.getClass().getFields()) {
            try {
                final Object value = field.get(this);
                if(value instanceof URI) {
                    final Comment comment = field.getAnnotation(Comment.class);
                    if(comment != null) newMap.put((URI) value, comment.value());
                }
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("Error while creating resource to comment map.", iae);
            }
        }
        resourceToCommentMap = newMap;
    }

}
