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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Facility class providing centralized configuration parameters.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class Configuration {

    /**
     * Default configuration file.
     */
    public static final String DEFAULT_CONFIG_FILE = "/default-configuration.properties";

    public static final String FLAG_PROPERTY_ON  = "on";

    public static final String FLAG_PROPERTY_OFF = "off";

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static final Configuration configuration = new Configuration();

    private final Properties properties;

    public static synchronized Configuration instance() {
        return configuration;
    }

    private Configuration() {
        properties = new Properties();
        try {
            properties.load( this.getClass().getResourceAsStream(DEFAULT_CONFIG_FILE) );
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while loading default configuration.", ioe);
        }
        if(logger.isInfoEnabled()) {
            final String[] defaultProperties = getProperties();
            final StringBuilder sb = new StringBuilder();
            sb.append("\n======================= Configuration Properties =======================\n");
            for(String defaultProperty : defaultProperties) {
                sb.append(defaultProperty).append('=').append( getPropertyValue(defaultProperty) ).append('\n');
            }
            sb.append(  "========================================================================\n");
            logger.info( sb.toString() );
        }
    }

    /**
     * Returns all the defined configuration properties.
     *
     * @return list of defined properties.
     */
    public synchronized String[] getProperties() {
        return properties.keySet().toArray( new String[properties.size()] );
    }

    /**
     * Checks whether a property is defined or not in configuration.
     *
     * @param propertyName name of property to check.
     * @return <code>true</code> if defined, </code>false</code> otherwise.
     */
    public synchronized boolean defineProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    /**
     * Returns the value of a specified property, of the default value if property is not defined.
     *
     * @param propertyName name of property
     * @param defaultValue default value if not found.
     * @return the value associated to <i>propertyName</i>.
     */
    public synchronized String getProperty(String propertyName, String defaultValue) {
        final String value = getPropertyValue(propertyName);
        if(value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Returns the value of the specified <code>propertyName</code> or raises an exception
     * if <code>propertyName</code> is not defined.
     *
     * @param propertyName name of property to be returned.
     * @return property value.
     * @throws IllegalArgumentException if the property name is not defined
     *                                  or the found property value is blank or empty.
     */
    public synchronized String getPropertyOrFail(String propertyName) {
        final String propertyValue = getPropertyValue(propertyName);
        if(propertyValue == null) {
            throw new IllegalArgumentException("The property '" + propertyName + "' is expected to be declared.");
        }
        if(  propertyValue.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "Invalid value '" + propertyValue + "' for property '" + propertyName + "'"
            );
        }
        return propertyValue;
    }

    /**
     * Returns the {@link Integer} value of the specified <code>propertyName</code> or raises an exception
     * if <code>propertyName</code> is not defined.
     *
     * @param propertyName name of property to be returned.
     * @return property value.
     * @throws NullPointerException if the property name is not defined.
     * @throws IllegalArgumentException if the found property value is blank or empty.
     * @throws NumberFormatException if the found property value is not a valid {@link Integer}.
     */
    public synchronized int getPropertyIntOrFail(String propertyName) {
        final String value = getPropertyOrFail(propertyName);
        final String trimValue = value.trim();
        try {
            return Integer.parseInt(trimValue);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("The retrieved property is not a valid Integer: '" + trimValue + "'");
        }
    }

    public synchronized boolean getFlagProperty(final String propertyName) {
        final String value = getProperty(propertyName, null);
        if(value == null) {
            return false;
        }
        if(FLAG_PROPERTY_ON.equals(value)) {
            return true;
        }
        if(FLAG_PROPERTY_OFF.equals(value)) {
            return false;
        }
        throw new IllegalArgumentException(
                String.format(
                    "Invalid value [%s] for flag property [%s]. Supported values are %s|%s",
                    value, propertyName, FLAG_PROPERTY_ON, FLAG_PROPERTY_OFF
                )
        );
    }

    private String getPropertyValue(String propertyName) {
        if( ! defineProperty(propertyName) ) {
            if(logger.isDebugEnabled()) {
                logger.debug(
                        String.format(
                                "Property '%s' is not declared in default configuration file [%s]",
                                propertyName,
                                DEFAULT_CONFIG_FILE
                        )
                );
            }
            return null;
        }
        final String systemValue = System.getProperties().getProperty(propertyName);
        if(systemValue == null) {
            return properties.getProperty(propertyName);
        }
        return systemValue;
    }
}
