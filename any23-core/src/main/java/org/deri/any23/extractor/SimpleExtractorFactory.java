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

package org.deri.any23.extractor;

import org.deri.any23.mime.MIMEType;
import org.deri.any23.rdf.Prefixes;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is a simple and default-like implementation of {@link org.deri.any23.extractor.ExtractorFactory}.
 *
 * @param <T> the type of the {@link org.deri.any23.extractor.Extractor} served by this factory.
 */
public class SimpleExtractorFactory<T extends Extractor<?>> implements ExtractorFactory<T> {

    private final String name;

    private final Prefixes prefixes;

    private final Collection<MIMEType> supportedMIMETypes = new ArrayList<MIMEType>();

    private final String exampleInput;
    
    private final Class<T> extractorClass;

    /**
     * Creates an instance of a {@link org.deri.any23.extractor.ExtractorFactory} serving concrete implementation
     * instances of {@link org.deri.any23.extractor.Extractor}.
     *
     * @param name of the {@link org.deri.any23.extractor.Extractor}.
     * @param prefixes handled {@link org.deri.any23.rdf.Prefixes}.
     * @param supportedMIMETypes collection of supported MIME Types.
     * @param exampleInput a string acting as a input example.
     * @param extractorClass concrete implementation class of the {@link org.deri.any23.extractor.Extractor}.
     * @param <S> the concrete type of the {@link org.deri.any23.extractor.Extractor}.
     * @return an {@link org.deri.any23.extractor.ExtractorFactory}.
     */
    public static <S extends Extractor<?>> ExtractorFactory<S> create(
            String name,
            Prefixes prefixes,
            Collection<String> supportedMIMETypes,
            String exampleInput,
            Class<S> extractorClass
    ) {
        return new SimpleExtractorFactory<S>(name, prefixes, supportedMIMETypes, exampleInput, extractorClass);
    }

    /**
     * @return the name of the {@link org.deri.any23.extractor.Extractor}
     */
    public String getExtractorName() {
        return name;
    }

    /**
     * @return the handled {@link org.deri.any23.rdf.Prefixes}
     */
    public Prefixes getPrefixes() {
        return prefixes;
    }

    /**
     * @return the supported {@link org.deri.any23.mime.MIMEType}
     */
    public Collection<MIMEType> getSupportedMIMETypes() {
        return supportedMIMETypes;
    }

    @Override
    public Class<T> getExtractorType() {
        return extractorClass;
    }

    /**
     * @return an instance of type T concrete implementation of {@link org.deri.any23.extractor.Extractor}
     */
    @Override
    public T createExtractor() {
        try {
            return extractorClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Zero-argument constructor not public?", ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException("Non-instantiable type?", ex);
        }
    }

    /**
     * @return an input example
     */
    @Override
    public String getExampleInput() {
        return exampleInput;
    }

    private SimpleExtractorFactory(
            String name,
            Prefixes prefixes,
            Collection<String> supportedMIMETypes,
            String exampleInput,
            Class<T> extractorClass
    ) {
        this.name = name;
        this.prefixes = (prefixes == null) ? Prefixes.EMPTY : prefixes;
        for (String type : supportedMIMETypes) {
            this.supportedMIMETypes.add(MIMEType.parse(type));
        }
        this.exampleInput = exampleInput;
        this.extractorClass = extractorClass;
    }

}
