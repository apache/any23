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

package org.deri.any23;

import org.deri.any23.extractor.Extractor;
import org.deri.any23.validator.ValidationReport;

import java.util.Collections;
import java.util.List;


/**
 * This class contains some statistics and general information about
 * an extraction.
 *
 * @see org.deri.any23.Any23
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class ExtractionReport {

    private final List<Extractor> matchingExtractors;

    private final String encoding;

    private final String detectedMimeType;

    private final ValidationReport validationReport;

    public ExtractionReport(
            final List<Extractor> matchingExtractors,
            String encoding,
            String detectedMimeType,
            ValidationReport validationReport
    ) {
        this.matchingExtractors    = Collections.unmodifiableList(matchingExtractors);
        this.encoding              = encoding;
        this.detectedMimeType      = detectedMimeType;
        this.validationReport      = validationReport;
    }

    /**
     * @return <code>true</code> if the extraction has activated
     *         at least an extractor, <code>false</code> otherwise.
     */
    public boolean hasMatchingExtractors() {
        return matchingExtractors.size() > 0;
    }

    /**
     * @return the list of matching extractors.
     */
    public List<Extractor> getMatchingExtractors() {
        return matchingExtractors;
    }

    /**
     * @return the detected encoding for the source stream.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @return the tetected mimetype for the input stream.      
     */
    public String getDetectedMimeType() {
        return detectedMimeType;
    }

    /**
     * @return the validation report applied to the processed document.
     */
    public ValidationReport getValidationReport() {
        return validationReport;
    }

}
