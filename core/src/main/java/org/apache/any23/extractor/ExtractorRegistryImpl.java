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

package org.apache.any23.extractor;

import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.extractor.html.HTMLMetaExtractorFactory;
import org.apache.any23.extractor.rdfa.RDFa11ExtractorFactory;
import org.apache.any23.extractor.rdfa.RDFaExtractorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Singleton class acting as a register for all the various
 *  {@link Extractor}.
 */
public class ExtractorRegistryImpl extends info.aduna.lang.service.ServiceRegistry<String, ExtractorFactory> implements ExtractorRegistry {

    /**
     * Public constructor for ExtractorRegistryImpl. Should normally call getInstance.
     */
    public ExtractorRegistryImpl() {
        super(ExtractorFactory.class);
    }

    /**
     * The instance.
     */
    private static ExtractorRegistry instance = null;

    /**
     * @return returns the {@link ExtractorRegistry} instance.
     */
    public static ExtractorRegistry getInstance() {
        // Thread-safe
        synchronized (ExtractorRegistry.class) {
            final DefaultConfiguration conf = DefaultConfiguration.singleton();
            if (instance == null) {
                instance = new ExtractorRegistryImpl();
                // FIXME: Remove these hardcoded links to the extractor factories by turning them into SPI interfaces
                //instance.register(RDFXMLExtractor.factory);
                //instance.register(TurtleExtractor.factory);
                //instance.register(NTriplesExtractor.factory);
                //instance.register(NQuadsExtractor.factory);
                //instance.register(TriXExtractor.factory);
                //instance.register(HeadLinkExtractor.factory);
                //instance.register(LicenseExtractor.factory);
                //instance.register(TitleExtractor.factory);
                //instance.register(XFNExtractor.factory);
                //instance.register(ICBMExtractor.factory);
                //instance.register(AdrExtractor.factory);
                //instance.register(GeoExtractor.factory);
                //instance.register(HCalendarExtractor.factory);
                //instance.register(HCardExtractor.factory);
                //instance.register(HListingExtractor.factory);
                //instance.register(HResumeExtractor.factory);
                //instance.register(HReviewExtractor.factory);
                //instance.register(HRecipeExtractor.factory);
                //instance.register(SpeciesExtractor.factory);
                //instance.register(TurtleHTMLExtractor.factory);
                //instance.register(MicrodataExtractor.factory);
                //instance.register(CSVExtractor.factory);
                
                if(conf.getFlagProperty("any23.extraction.rdfa.programmatic")) {
                    instance.unregister(RDFaExtractorFactory.NAME);
                    // FIXME: Unregister RDFaExtractor if flag is not set
                    //instance.register(RDFa11Extractor.factory);
                } else {
                    instance.unregister(RDFa11ExtractorFactory.NAME);
                    // FIXME: Unregister RDFaExtractor if flag is set
                    //instance.register(RDFaExtractor.factory);
                }
                if(!conf.getFlagProperty("any23.extraction.head.meta")) {
                    instance.unregister(HTMLMetaExtractorFactory.NAME);
                    // FIXME: Unregister HTMLMetaExtractor if this flag is not set
                    //instance.register(HTMLMetaExtractor.factory);
                }
            }
        }
        return instance;
    }

    /**
     * Registers an {@link ExtractorFactory}.
     *
     * @param factory
     * @throws IllegalArgumentException if trying to register a {@link ExtractorFactory}
     *         with a that already exists in the registry.
     */
    @Override
    public void register(ExtractorFactory<?> factory) {
        this.add(factory);
    }
    
    /**
     * Unregisters the {@link ExtractorFactory} with the given name.
     * 
     * @param name The name of the ExtractorFactory to unregister.
     */
    @Override
    public void unregister(String name) {
        if(this.has(name)) {
            this.remove(this.get(name));
        }
    }
    
    /**
     *
     * Retrieves a {@link ExtractorFactory} given its name
     *
     * @param name of the desired factory
     * @return the {@link ExtractorFactory} associated to the provided name
     * @throws IllegalArgumentException if there is not a
     * {@link ExtractorFactory} associated to the provided name.
     */
    @Override
    public ExtractorFactory<?> getFactory(String name) {
        ExtractorFactory<?> result = this.get(name);
        if (result == null) {
            throw new IllegalArgumentException("Unregistered extractor name: " + name);
        }
        return result;
    }

    /**
     * @return an {@link ExtractorGroup} with all the registered
     * {@link Extractor}.
     */
    @Override
    public ExtractorGroup getExtractorGroup() {
        return getExtractorGroup(getAllNames());
    }

    /**
     * Returns an {@link ExtractorGroup} containing the
     * {@link ExtractorFactory} mathing the names provided as input.
     * @param names a {@link java.util.List} containing the names of the desired {@link ExtractorFactory}.
     * @return the extraction group.
     */
    @Override
    public ExtractorGroup getExtractorGroup(List<String> names) {
        List<ExtractorFactory<?>> members = new ArrayList<ExtractorFactory<?>>(names.size());
        for (String name : names) {
            members.add(getFactory(name));
        }
        return new ExtractorGroup(members);
    }

    /**
     * 
     * @param name of the {@link ExtractorFactory}
     * @return <code>true</code> if is there a {@link ExtractorFactory}
     * associated to the provided name.
     */
    @Override
    public boolean isRegisteredName(String name) {
        return this.has(name);
    }

    /**
     * Returns the names of all registered extractors, sorted alphabetically.
     */
    @Override
    public List<String> getAllNames() {
        List<String> result = new ArrayList<String>(this.getKeys());
        Collections.sort(result);
        return result;
    }

    @Override
    protected String getKey(ExtractorFactory service) {
        return service.getExtractorName();
    }

}
