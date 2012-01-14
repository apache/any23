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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry class for {@link FormatWriter}s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WriterRegistry {

    /**
     * Singleton instance.
     */
    private static WriterRegistry instance;

    /**
     * List of registered writers.
     */
    private final List<Class<? extends FormatWriter>> writers =
            new ArrayList<Class<? extends FormatWriter>>();

    /**
     * MIME Type to {@link FormatWriter} class.
     */
    private final Map<String,List<Class<? extends FormatWriter>>> mimeToWriter =
            new HashMap<String, List<Class<? extends FormatWriter>>>();

    /**
     * Identifier to {@link FormatWriter} class.
     */
    private final Map<String,Class<? extends FormatWriter>> idToWriter =
            new HashMap<String, Class<? extends FormatWriter>>();

    /**
     * Reads the identifier specified for the given {@link FormatWriter}.
     *
     * @param writerClass writer class.
     * @return identifier.
     */
    public static String getIdentifier(Class<? extends FormatWriter> writerClass) {
        return getWriterAnnotation(writerClass).identifier();
    }

    /**
     * Reads the <i>MIME Type</i> specified for the given {@link FormatWriter}.
     *
     * @param writerClass writer class.
     * @return MIME type.
     */
    public static String getMimeType(Class<? extends FormatWriter> writerClass) {
        return getWriterAnnotation(writerClass).mimeType();
    }

    /**
     * @return the {@link WriterRegistry} singleton instance.
     */
    public synchronized static WriterRegistry getInstance() {
        if(instance == null) {
            instance = new WriterRegistry();
        }
        return instance;
    }

    /**
     * Reads the annotation associated to the given {@link FormatWriter}.
     *
     * @param writerClass input class.
     * @return associated annotation.
     * @throws IllegalArgumentException if the annotation is not declared.
     */
    private static Writer getWriterAnnotation(Class<? extends FormatWriter> writerClass) {
        final Writer writer = writerClass.getAnnotation(Writer.class);
        if(writer == null)
            throw new IllegalArgumentException(
                    String.format("Class %s must be annotated with %s .",writerClass, Writer.class)
            );
        return writer;
    }

    private WriterRegistry() {
        register(TurtleWriter.class);
        register(RDFXMLWriter.class);
        register(NTriplesWriter.class);
        register(NQuadsWriter.class);
        register(TriXWriter.class);
        register(JSONWriter.class);
        register(URIListWriter.class);
    }

    /**
     * Registers a new {@link FormatWriter} to the registry.
     *
     * @param writerClass the class of the writer to be registered.
     * @throws IllegalArgumentException if the id or the mimetype are null
     *                                  or empty strings or if the identifier has been already defined.
     */
    public synchronized void register(Class<? extends FormatWriter> writerClass) {
        if(writerClass == null) throw new NullPointerException("writerClass cannot be null.");
        final Writer writer = getWriterAnnotation(writerClass);
        final String id       = writer.identifier();
        final String mimeType = writer.mimeType();
        if(id == null || id.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid identifier returned by writer " + writer);
        }
        if(mimeType == null || mimeType.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid MIME type returned by writer " + writer);
        }
        if(idToWriter.containsKey(id))
            throw new IllegalArgumentException("The writer identifier is already declared.");

        writers.add(writerClass);
        List<Class<? extends FormatWriter>> writerClasses = mimeToWriter.get(mimeType);
        if(writerClasses == null) {
            writerClasses = new ArrayList<Class<? extends FormatWriter>>();
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
    public synchronized String[] getIdentifiers() {
        final String[] ids = new String[writers.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = getIdentifier( writers.get(i) );
        }
        return ids;
    }

    /**
     * @return the list of MIME types covered by the registered {@link FormatWriter}s.
     */
    public synchronized String[] getMimeTypes() {
        return mimeToWriter.keySet().toArray( new String[mimeToWriter.keySet().size()] );
    }

    /**
     * @return the list of all the registered {@link FormatWriter}s.
     */
    @SuppressWarnings("unchecked")
    public synchronized Class<? extends FormatWriter>[] getWriters() {
        return writers.toArray( new Class[ writers.size() ] );
    }

    /**
     * Returns the {@link FormatWriter} identified by <code>id</code>.
     *
     * @param id the writer identifier.
     * @return the class of the {@link FormatWriter} matching the <code>id</code>
     *         or <code>null</code> if not found.s
     */
    public synchronized Class<? extends FormatWriter> getWriterByIdentifier(String id) {
        return idToWriter.get(id);
    }

    /**
     * Returns all the writers matching the specified <code>mimeType</code>.
     *
     * @param mimeType a MIMEType.
     * @return a list of matching writers or an empty list.
     */
    @SuppressWarnings("unchecked")
    public synchronized Class<? extends FormatWriter>[] getWritersByMimeType(String mimeType) {
        final List<Class<? extends FormatWriter>> writerClasses = mimeToWriter.get(mimeType);
        return writerClasses.toArray( new Class[writerClasses.size()] );
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
        final  Class<? extends FormatWriter> writerClazz = getWriterByIdentifier(id);
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
    private FormatWriter createWriter(Class<? extends FormatWriter> clazz, OutputStream os) {
        try {
            return clazz.getConstructor(OutputStream.class).newInstance(os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while initializing format writer " + clazz + " .");
        }
    }

}
