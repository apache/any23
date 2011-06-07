/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.vocab;

import org.deri.any23.util.RDFHelper;
import org.openrdf.model.URI;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for the definition of a vocabulary.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public abstract class Vocabulary {

    /**
     * Map of vocabulary resources.
     */
    private Map<String,URI> resources;

    /**
     * Map of vocabulary properties.
     */
    private Map<String,URI> properties;

    /**
     * Returns a resource defined within this vocabulary.
     *
     * @param name resource name.
     * @return the URI associated to such resource.
     */
    public URI getResource(String name) {
        URI res = resources.get(name);
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
    public URI getPropertyCamelized(String property) {
        String[] names = property.split("\\W");
        String camelCase = names[0];
        for (int i = 1; i < names.length; i++) {
            String tmp = names[i];
            camelCase += tmp.replaceFirst("(.)", tmp.substring(0, 1).toUpperCase());
        }
        return getProperty(camelCase);
    }

    /**
     * Creates a URI.
     *
     * @param uriStr the URI string
     * @return the URI instance.
     */
    protected URI createURI(String uriStr) {
        return RDFHelper.uri(uriStr);
    }

    /**
     * Creates a resource and register it to the {@link #resources} map.
     *
     * @param namespace vocabulary namespace.
     * @param resource name of the resource.
     * @return the created resource URI.
     */
    protected URI createResource(String namespace, String resource) {
        URI res = createURI(namespace, resource);
        if(resources == null) {
            resources = new HashMap<String, URI>(10);
        }
        resources.put(resource, res);
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
        return RDFHelper.uri(namespace, localName);
    }

}
