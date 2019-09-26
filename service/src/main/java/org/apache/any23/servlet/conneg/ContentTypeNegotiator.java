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

package org.apache.any23.servlet.conneg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class defines a negotiator for content types based on scoring.
 */
public class ContentTypeNegotiator {

    private List<VariantSpec> variantSpecs = new ArrayList<>();

    private List<MediaRangeSpec> defaultAcceptRanges = Collections.singletonList(MediaRangeSpec.parseRange("*/*"));
    
    private Collection<AcceptHeaderOverride> userAgentOverrides = new ArrayList<>();

    protected ContentTypeNegotiator(){}

    /**
     * Returns the {@link MediaRangeSpec}
     * associated to the given <i>accept</i> type.
     * 
     * @param accept a provided <i>accept</i> type
     * @return a {@link MediaRangeSpec} associated to the accept parameter
     */
    public MediaRangeSpec getBestMatch(String accept) {
        return getBestMatch(accept, null);
    }

    /**
     * Returns the {@link MediaRangeSpec}
     * associated to the given <i>accept</i> type and <i>userAgent</i>.
     *
     * @param accept a provided <i>accept</i> type
     * @param userAgent use agent associated with the request
     * @return the {@link MediaRangeSpec}
     * associated to the given <i>accept</i> type and <i>userAgent</i>.
     */
    public MediaRangeSpec getBestMatch(String accept, String userAgent) {
        if (userAgent == null) {
            userAgent = "";
        }
        Iterator<AcceptHeaderOverride> it = userAgentOverrides.iterator();
        String overriddenAccept = accept;
        while (it.hasNext()) {
            AcceptHeaderOverride override = it.next();
            if (override.matches(accept, userAgent)) {
                overriddenAccept = override.getReplacement();
            }
        }
        return new Negotiation(toAcceptRanges(overriddenAccept)).negotiate();
    }

    protected VariantSpec addVariant(String mediaType) {
        VariantSpec result = new VariantSpec(mediaType);
        variantSpecs.add(result);
        return result;
    }

    /**
     * Sets an Accept header to be used as the default if a client does
     * not send an Accept header, or if the Accept header cannot be parsed.
     * Defaults to "* / *".
     * @param accept a default <i>accept</i> type
     */
    protected void setDefaultAccept(String accept) {
        this.defaultAcceptRanges = MediaRangeSpec.parseAccept(accept);
    }

    /**
     * Overrides the Accept header for certain user agents. This can be
     * used to implement special-case handling for user agents that send
     * faulty Accept headers.
     *
     * @param userAgentString      A pattern to be matched against the User-Agent header;
     *                             <code>null</code> means regardless of User-Agent
     * @param originalAcceptHeader Only override the Accept header if the user agent
     *                             sends this header; <code>null</code> means always override
     * @param newAcceptHeader      The Accept header to be used instead
     */
    protected void addUserAgentOverride(
         Pattern userAgentString,
         String originalAcceptHeader,
         String newAcceptHeader
    ) {
        this.userAgentOverrides.add(
            new AcceptHeaderOverride(userAgentString, originalAcceptHeader, newAcceptHeader)
        );
    }

    private List<MediaRangeSpec> toAcceptRanges(String accept) {
        if (accept == null) {
            return defaultAcceptRanges;
        }
        List<MediaRangeSpec> result = MediaRangeSpec.parseAccept(accept);
        if (result.isEmpty()) {
            return defaultAcceptRanges;
        }
        return result;
    }

    protected class VariantSpec {

        private MediaRangeSpec type;
        private List<MediaRangeSpec> aliases = new ArrayList<>();
        private boolean isDefault = false;

        public VariantSpec(String mediaType) {
            type = MediaRangeSpec.parseType(mediaType);
        }

        public VariantSpec addAliasMediaType(String mediaType) {
            aliases.add(MediaRangeSpec.parseType(mediaType));
            return this;
        }

        public void makeDefault() {
            isDefault = true;
        }

        public MediaRangeSpec getMediaType() {
            return type;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public List<MediaRangeSpec> getAliases() {
            return aliases;
        }
    }

    private class Negotiation {

        private final List<MediaRangeSpec> ranges;
        private MediaRangeSpec bestMatchingVariant = null;
        private MediaRangeSpec bestDefaultVariant = null;
        private double bestMatchingQuality = 0;
        private double bestDefaultQuality = 0;

        Negotiation(List<MediaRangeSpec> ranges) {
            this.ranges = ranges;
        }

        MediaRangeSpec negotiate() {
            Iterator<VariantSpec> it = variantSpecs.iterator();
            while (it.hasNext()) {
                VariantSpec variant = it.next();
                if (variant.isDefault) {
                    evaluateDefaultVariant(variant.getMediaType());
                }
                evaluateVariant(variant.getMediaType());
                Iterator<MediaRangeSpec> aliasIt = variant.getAliases().iterator();
                while (aliasIt.hasNext()) {
                    MediaRangeSpec alias = aliasIt.next();
                    evaluateVariantAlias(alias, variant.getMediaType());
                }
            }
            return (bestMatchingVariant == null) ? bestDefaultVariant : bestMatchingVariant;
        }

        private void evaluateVariantAlias(MediaRangeSpec variant, MediaRangeSpec isAliasFor) {
            if (variant.getBestMatch(ranges) == null)
              return;
            double q = variant.getBestMatch(ranges).getQuality();
            if (q * variant.getQuality() > bestMatchingQuality) {
                bestMatchingVariant = isAliasFor;
                bestMatchingQuality = q * variant.getQuality();
            }
        }

        private void evaluateVariant(MediaRangeSpec variant) {
            evaluateVariantAlias(variant, variant);
        }

        private void evaluateDefaultVariant(MediaRangeSpec variant) {
            if (variant.getQuality() > bestDefaultQuality) {
                bestDefaultVariant = variant;
                bestDefaultQuality = 0.00001 * variant.getQuality();
            }
        }
        
    }

    private class AcceptHeaderOverride {

        private Pattern userAgentPattern;
        private String original;
        private String replacement;

        AcceptHeaderOverride(Pattern userAgentPattern, String original, String replacement) {
            this.userAgentPattern = userAgentPattern;
            this.original = original;
            this.replacement = replacement;
        }

        boolean matches(String acceptHeader, String userAgentHeader) {
            return (userAgentPattern == null
                    || userAgentPattern.matcher(userAgentHeader).find())
                    && (original == null || original.equals(acceptHeader));
        }

        String getReplacement() {
            return replacement;
        }
    }
    
}