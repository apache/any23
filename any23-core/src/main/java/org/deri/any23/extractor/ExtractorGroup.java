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
 *
 */

package org.deri.any23.extractor;

import org.deri.any23.mime.MIMEType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * It simple models a group of {@link org.deri.any23.extractor.ExtractorFactory} providing
 * simple accessing methods.
 */
public class ExtractorGroup implements Iterable<ExtractorFactory<?>> {

    private final Collection<ExtractorFactory<?>> factories;

    public ExtractorGroup(Collection<ExtractorFactory<?>> factories) {
        this.factories = factories;
    }

    public boolean isEmpty() {
        return factories.isEmpty();
    }

    /**
     * Returns a {@link ExtractorGroup} with a set of {@link org.deri.any23.extractor.Extractor} able to
     * process the provided mime type.
     * 
     * @param mimeType to perform the selection.
     * @return an {@link org.deri.any23.extractor.ExtractorGroup} able to process the provided mime type.
     */
    public ExtractorGroup filterByMIMEType(MIMEType mimeType) {
        // @@@ wildcards, q values
        Collection<ExtractorFactory<?>> matching = new ArrayList<ExtractorFactory<?>>();
        for (ExtractorFactory<?> factory : factories) {
            if (supportsAllContentTypes(factory) || supports(factory, mimeType)) {
                matching.add(factory);
            }
        }
        return new ExtractorGroup(matching);
    }

    public Iterator<ExtractorFactory<?>> iterator() {
        return factories.iterator();
    }

    /**
     * @return <code>true</code> if all the {@link org.deri.any23.extractor.Extractor} contained in the group
     * supports all the content types.
     */
    public boolean allExtractorsSupportAllContentTypes() {
        for (ExtractorFactory<?> factory : factories) {
            if (!supportsAllContentTypes(factory)) return false;
        }
        return true;
    }

    private boolean supportsAllContentTypes(ExtractorFactory<?> factory) {
        return factory.getSupportedMIMETypes().contains("*/*");
    }

    private boolean supports(ExtractorFactory<?> factory, MIMEType mimeType) {
        for (MIMEType supported : factory.getSupportedMIMETypes()) {
            if (supported.isAnyMajorType()) return true;
            if (supported.isAnySubtype() && supported.getMajorType().equals(mimeType.getMajorType())) return true;
            if (supported.getFullType().equals(mimeType.getFullType())) return true;
        }
        return false;
    }

}
