/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.deri.any23.util;

/**
 * This class provides a set of string utility methods.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StringUtils {

    /**
     * Counts how many times <code>content</code> appears within <code>container</code>
     * without string overlapping.
     *
     * @param container container string.
     * @param content content string.
     * @return occurrences count.
     */
    public static int countOccurrences(String container, String content){
        int lastIndex, currIndex = 0, occurrences = 0;
        while(true) {
            lastIndex = container.indexOf(content, currIndex);
            if(lastIndex == -1) {
                break;
            }
            currIndex = lastIndex + content.length();
            occurrences++;
        }
        return occurrences;
    }

    /**
     * Check whether string <code>candidatePrefix</code> is prefix of string <code>container</code>.
     *
     * @param candidatePrefix
     * @param container
     * @return
     */
    public static boolean isPrefix(String candidatePrefix, String container) {
        if(candidatePrefix == null || container == null) {
            throw new NullPointerException("Arguments must be not null.");
        }
        if(candidatePrefix.length() > container.length()) {
            return false;
        }
        for(int i = 0; i < candidatePrefix.length(); i++) {
            if(candidatePrefix.charAt(i) != container.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether string <code>candidateSuffix</code> is suffix of string <code>container</code>.
     *
     * @param candidateSuffix
     * @param container
     * @return
     */
    public static boolean isSuffix(String candidateSuffix, String container) {
        if(candidateSuffix == null || container == null) {
            throw new NullPointerException("Arguments must be not null.");
        }
        if(candidateSuffix.length() > container.length()) {
            return false;
        }
        final int lenDiff = container.length() - candidateSuffix.length();
        for(int i = candidateSuffix.length() - 1; i >= 0; i--) {
            if(candidateSuffix.charAt(i) != container.charAt(i + lenDiff)) {
                return false;
            }
        }
        return true;
    }

    private StringUtils() {}
}
