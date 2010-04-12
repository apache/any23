/**
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

package org.deri.any23.extractor;

/**
 * This class defines the context of a single document
 * and it is passed to every {@link org.deri.any23.extractor.Extractor}
 * candidate for being applied on the processed document.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DocumentContext {

    private String defaultLanguage;

    public DocumentContext(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Returns the default language specified for the document.
     *
     * @return the default language of the document.
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }
    
}
