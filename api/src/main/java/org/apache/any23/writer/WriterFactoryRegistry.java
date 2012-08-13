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

package org.apache.any23.writer;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry class for {@link WriterFactory}s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WriterFactoryRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(WriterFactoryRegistry.class);
    
    /**
     * Singleton instance.
     */
    private static WriterFactoryRegistry instance;

    /**
     * List of registered writers.
     */
    private final List<WriterFactory> writers =
            new ArrayList<WriterFactory>();

    /**
     * MIME Type to {@link FormatWriter} class.
     */
    private final Map<String,List<WriterFactory>> mimeToWriter =
            new HashMap<String, List<WriterFactory>>();

    /**
     * Identifier to {@link FormatWriter} class.
     */
    private final Map<String,WriterFactory> idToWriter =
            new HashMap<String, WriterFactory>();

    private List<String> identifiers = new ArrayList<String>();

    /**
     * Reads the identifier specified for the given {@link FormatWriter}.
     *
     * @param writerClass writer class.
     * @return identifier.
     */
    public static String getIdentifier(WriterFactory writerClass) {
        return writerClass.getIdentifier();
    }

    /**
     * Reads the <i>MIME Type</i> specified for the given {@link FormatWriter}.
     *
     * @param writerClass writer class.
     * @return MIME type.
     */
    public static String getMimeType(WriterFactory writerClass) {
        return writerClass.getMimeType();
    }

    /**
     * @return the {@link WriterFactoryRegistry} singleton instance.
     */
    public synchronized static WriterFactoryRegistry getInstance() {
        if(instance == null) {
            instance = new WriterFactoryRegistry();
        }
        return instance;
    }

    public WriterFactoryRegistry() {
        ServiceLoader<WriterFactory> serviceLoader = java.util.ServiceLoader.load(WriterFactory.class, this.getClass().getClassLoader());
        
        Iterator<WriterFactory> iterator = serviceLoader.iterator();
        
        // use while(true) loop so that we can isolate all service loader errors from .next and .hasNext to a single service
        while(true)
        {
            try
            {
                if(!iterator.hasNext())
                    break;
                
                WriterFactory factory = iterator.next();
                
                this.register(factory);
            }
            catch(ServiceConfigurationError error)
            {
                LOG.error("Found error loading a WriterFactory", error);
            }
        }
    }

    /**
     * Registers a new {@link WriterFactory} to the registry.
     *
     * @param writerClass the class of the writer to be registered.
     * @throws IllegalArgumentException if the id or the mimetype are null
     *                                  or empty strings or if the identifier has been already defined.
     */
    public synchronized void register(WriterFactory writerClass) {
        if(writerClass == null) throw new NullPointerException("writerClass cannot be null.");
        final String id       = writerClass.getIdentifier();
        final String mimeType = writerClass.getMimeType();
        if(id == null || id.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid identifier returned by writer " + writerClass);
        }
        if(mimeType == null || mimeType.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid MIME type returned by writer " + writerClass);
        }
        if(idToWriter.containsKey(id))
            throw new IllegalArgumentException("The writer identifier is already declared.");

        writers.add(writerClass);
        identifiers.add(writerClass.getIdentifier());
        List<WriterFactory> writerClasses = mimeToWriter.get(mimeType);
        if(writerClasses == null) {
            writerClasses = new ArrayList<WriterFactory>();
            mimeToWriter.put(mimeType, writerClasses);
        }
        writerClasses.add(writerClass);
        idToWriter.put(id, writerClass);
    }

    /**
     * Verifies if a {@link FormatWriter} with given <code>id</code> identifier has been registered.
     *
     * @param id identifier.
     * @return <code>true</code> if the identifier has been registered, <code>false</code> otherwise.
     */
    public synchronized boolean hasIdentifier(String id) {
        return idToWriter.containsKey(id);
    }

    /**
     * @return the list of all the specified identifiers.
     */
    public synchronized List<String> getIdentifiers() {
        return Collections.unmodifiableList(identifiers);
    }

    /**
     * @return the list of MIME types covered by the registered {@link FormatWriter}s.
     */
    public synchronized Collection<String> getMimeTypes() {
        return Collections.unmodifiableCollection(mimeToWriter.keySet());
    }

    /**
     * @return the list of all the registered {@link FormatWriter}s.
     */
    public synchronized List<WriterFactory> getWriters() {
        return Collections.unmodifiableList(writers);
    }

    /**
     * Returns the {@link FormatWriter} identified by <code>id</code>.
     *
     * @param id the writer identifier.
     * @return the class of the {@link FormatWriter} matching the <code>id</code>
     *         or <code>null</code> if not found.s
     */
    public synchronized WriterFactory getWriterByIdentifier(String id) {
        return idToWriter.get(id);
    }

    /**
     * Returns all the writers matching the specified <code>mimeType</code>.
     *
     * @param mimeType a MIMEType.
     * @return a list of matching writers or an empty list.
     */
    public synchronized Collection<WriterFactory> getWritersByMimeType(String mimeType) {
        final List<WriterFactory> writerClasses = mimeToWriter.get(mimeType);
        return writerClasses;
    }

    /**
     * Returns an instance of {@link FormatWriter} ready to write on the given <code>os</code>
     * {@link OutputStream}.
     *
     * @param id the identifier of the {@link FormatWriter} to crate an instance.
     * @param os the output stream.
     * @return the not <code>null</code> {@link FormatWriter} instance.
     * @throws NullPointerException if the <code>id</code> doesn't match any registered writer.
     */
    public synchronized FormatWriter getWriterInstanceByIdentifier(String id, OutputStream os) {
        final  WriterFactory writerClazz = getWriterByIdentifier(id);
        if(writerClazz == null)
            throw new NullPointerException(
                String.format("Cannot find writer with id '%s' .", id)
            );
        return createWriter(writerClazz, os);
    }

    /**
     * Crates a writer instance.
     *
     * @param clazz class to instantiate.
     * @param os output stream to pass as constructor argument.
     * @return created instance.
     * @throws IllegalArgumentException if an error occurs during instantiation.
     */
    private FormatWriter createWriter(WriterFactory clazz, OutputStream os) {
        try {
            return clazz.getRdfWriter(os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while initializing format writer " + clazz + " .", e);
        }
    }

}
