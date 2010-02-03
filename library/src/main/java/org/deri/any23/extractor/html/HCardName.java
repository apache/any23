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

package org.deri.any23.extractor.html;

import java.util.HashMap;
import java.util.Map;

/**
 * An HCard name, consisting of various parts. Handles computation
 * of full names from first and last names, and similar computations.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class HCardName {

    public static final String GIVEN_NAME = "given-name";
    public static final String FAMILY_NAME = "family-name";
    public static final String ADDITIONAL_NAME = "additional-name";
    public static final String NICKNAME = "nickname";
    public static final String HONORIFIC_PREFIX = "honorific-prefix";
    public static final String HONORIFIC_SUFFIX = "honorific-suffix";

    public static final String[] FIELDS = {
            GIVEN_NAME,
            FAMILY_NAME,
            ADDITIONAL_NAME,
            NICKNAME,
            HONORIFIC_PREFIX,
            HONORIFIC_SUFFIX
    };

    private static final String[] NAME_COMPONENTS = {
            HONORIFIC_PREFIX,
            GIVEN_NAME,
            ADDITIONAL_NAME,
            FAMILY_NAME,
            HONORIFIC_SUFFIX
    };

    private Map<String, String> fields = new HashMap<String, String>();
    private String fullName     = null;
    private String organization = null;
    private String unit         = null;

    public void setField(String fieldName, String value) {
        value = fixWhiteSpace(value);
        if (value == null) return;
        fields.put(fieldName, value);
    }

    public void setFullName(String value) {
        value = fixWhiteSpace(value);
        if (value == null) return;
        this.fullName = value;
    }

    public void setOrganization(String value) {
        value = fixWhiteSpace(value);
        if (value == null) return;
        this.organization = value;
    }

    public String getField(String fieldName) {
        if (GIVEN_NAME.equals(fieldName)) {
            return getFullNamePart(GIVEN_NAME, 0);
        }
        if (FAMILY_NAME.equals(fieldName)) {
            return getFullNamePart(FAMILY_NAME, 1);
        }
        return fields.get(fieldName);
    }

    private String getFullNamePart(String fieldName, int index) {
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        if (fullName == null) return null;
        // If org and fn are the same, the hCard is for an organization, and we do not split the fn
        if (fullName.equals(organization)) {
            return null;
        }
        String[] split = fullName.split("\\s+");
        if (split.length <= index) return null;
        return split[index];
    }

    public boolean hasField(String fieldName) {
        return getField(fieldName) != null;
    }

    public boolean hasAnyField() {
        for (String fieldName : FIELDS) {
            if (hasField(fieldName)) return true;
        }
        return false;
    }

    public String getFullName() {
        if (fullName != null) return fullName;
        StringBuffer s = new StringBuffer();
        boolean empty = true;
        for (String fieldName : NAME_COMPONENTS) {
            if (!hasField(fieldName)) continue;
            if (!empty) {
                s.append(' ');
            }
            s.append(getField(fieldName));
            empty = false;
        }
        if (empty) return null;
        return s.toString();
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganizationUnit(String value) {
        value = fixWhiteSpace(value);
        if (value == null) return;
        this.unit = value;
    }

    public String getOrganizationUnit() {
        return unit;
    }

    private String fixWhiteSpace(String s) {
        if (s == null) return null;
        s = s.trim().replaceAll("\\s+", " ");
        if ("".equals(s)) return null;
        return s;
    }
    
}