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

package org.apache.any23.plugin.htmlscraper;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CanolaExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.vocab.SINDICE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of content extractor for performing <i>HTML</i> scraping.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HTMLScraperExtractor implements Extractor.ContentExtractor {

    public static final IRI PAGE_CONTENT_DE_PROPERTY  =
            SimpleValueFactory.getInstance().createIRI(SINDICE.NS + "pagecontent/de");
    public static final IRI PAGE_CONTENT_AE_PROPERTY  =
            SimpleValueFactory.getInstance().createIRI(SINDICE.NS + "pagecontent/ae");
    public static final IRI PAGE_CONTENT_LCE_PROPERTY =
            SimpleValueFactory.getInstance().createIRI(SINDICE.NS + "pagecontent/lce");
    public static final IRI PAGE_CONTENT_CE_PROPERTY  =
            SimpleValueFactory.getInstance().createIRI(SINDICE.NS + "pagecontent/ce");

    private final List<ExtractionRule> extractionRules = new ArrayList<>();

    public HTMLScraperExtractor() {
        loadDefaultRules();
    }

    public void addTextExtractor(String name, IRI property, BoilerpipeExtractor extractor) {
        extractionRules.add( new ExtractionRule(name, property, extractor) );
    }

    public String[] getTextExtractors() {
        final List<String> extractors = new ArrayList<>();
        for(ExtractionRule er : extractionRules) {
            extractors.add(er.name);
        }
        return extractors.toArray( new String[extractors.size()] );
    }

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            InputStream inputStream,
            ExtractionResult extractionResult
    ) throws IOException, ExtractionException {
        try {
            final IRI documentIRI = extractionContext.getDocumentIRI();
            for (ExtractionRule extractionRule : extractionRules) {
                final String content = extractionRule.boilerpipeExtractor.getText(new InputStreamReader(inputStream));
                extractionResult.writeTriple(
                        documentIRI,
                        extractionRule.property,
                        SimpleValueFactory.getInstance().createLiteral(content)
                );
            }
        } catch (BoilerpipeProcessingException bpe) {
            throw new ExtractionException("Error while applying text processor " + ArticleExtractor.class, bpe);
        }
    }

    @Override
    public ExtractorDescription getDescription() {
        return HTMLScraperExtractorFactory.getDescriptionInstance();
    }

    @Override
    public void setStopAtFirstError(boolean b) {
        // Ignored.
    }

    private void loadDefaultRules() {
        addTextExtractor("default-extractor"      , PAGE_CONTENT_DE_PROPERTY , DefaultExtractor.getInstance());
        addTextExtractor("article-extractor"      , PAGE_CONTENT_AE_PROPERTY , ArticleExtractor.getInstance());
        addTextExtractor("large-content-extractor", PAGE_CONTENT_LCE_PROPERTY, LargestContentExtractor.getInstance());
        addTextExtractor("canola-extractor"       , PAGE_CONTENT_CE_PROPERTY , CanolaExtractor.getInstance());
    }

    /**
     * This class associates a <i>BoilerPipe</i> extractor with the property going to host the extracted content.
     */
    class ExtractionRule {

        public final String name;
        public final IRI property;
        public final BoilerpipeExtractor boilerpipeExtractor;

        ExtractionRule(String name, IRI property, BoilerpipeExtractor boilerpipeExtractor) {
            if(name == null) {
                throw new NullPointerException("name cannot be null.");
            }
            if(property == null) {
                throw new NullPointerException("property cannot be null.");
            }
            if(boilerpipeExtractor == null) {
                throw new NullPointerException("extractor cannot be null.");
            }
            this.name = name;
            this.property = property;
            this.boilerpipeExtractor = boilerpipeExtractor;
        }

    }
}
