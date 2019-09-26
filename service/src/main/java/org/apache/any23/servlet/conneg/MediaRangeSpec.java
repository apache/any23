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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the <i>HTTP header media-range specification</i>.
 * See <a href="http://www.ietf.org/rfc/rfc2616.txt">RFC 2616 section 14.1</a>. 
 */
public class MediaRangeSpec {

    private static final Pattern tokenPattern;

    private static final Pattern parameterPattern;


    private static final Pattern mediaRangePattern;

    private static final Pattern qValuePattern;

    private final String type;

    private final String subtype;

    private final List<String> parameterNames;

    private final List<String> parameterValues;

    private final String mediaType;
    
    private final double quality;
    
    static {

        // See RFC 2616, section 2.2
        String token = "[\\x20-\\x7E&&[^()<>@,;:\\\"/\\[\\]?={} ]]+";
        String quotedString = "\"((?:[\\x20-\\x7E\\n\\r\\t&&[^\"\\\\]]|\\\\[\\x00-\\x7F])*)\"";

        // See RFC 2616, section 3.6
        String parameter = ";\\s*(?!q\\s*=)(" + token + ")=(?:(" + token + ")|" + quotedString + ")";

        // See RFC 2616, section 3.9
        String qualityValue = "(?:0(?:\\.\\d{0,3})?|1(?:\\.0{0,3})?)";

        // See RFC 2616, sections 14.1
        String quality = ";\\s*q\\s*=\\s*([^;,]*)";

        // See RFC 2616, section 3.7
        String regex = "(" + token     + ")/(" + token + ")" +
                "((?:\\s*" + parameter + ")*)" +
                "(?:\\s*"  + quality   + ")?" +
                "((?:\\s*" + parameter + ")*)";

        tokenPattern      = Pattern.compile(token);
        parameterPattern  = Pattern.compile(parameter);
        mediaRangePattern = Pattern.compile(regex);
        qValuePattern     = Pattern.compile(qualityValue);
    }

    /**
     * Parses a media type from a string such as <code>text/html;charset=utf-8;q=0.9</code>.
     * @param mediaType input string from which to extract mediaType
     * @return {@link org.apache.any23.servlet.conneg.MediaRangeSpec}
     */
    public static MediaRangeSpec parseType(String mediaType) {
        MediaRangeSpec m = parseRange(mediaType);
        if (m == null || m.isWildcardType() || m.isWildcardSubtype()) {
            return null;
        }
        return m;
    }

    /**
     * Parses a media range from a string such as <code>text/*;charset=utf-8;q=0.9</code>.
     * Unlike simple media types, media ranges may include wildcards.
     * @param mediaRange input string from which to extract media range
     * @return {@link org.apache.any23.servlet.conneg.MediaRangeSpec}
     */
    public static MediaRangeSpec parseRange(String mediaRange) {
        Matcher m = mediaRangePattern.matcher(mediaRange);
        if (!m.matches()) {
            return null;
        }
        String type = m.group(1).toLowerCase();
        String subtype = m.group(2).toLowerCase();
        String unparsedParameters = m.group(3);
        String qValue = m.group(7);
        m = parameterPattern.matcher(unparsedParameters);
        if ("*".equals(type) && !"*".equals(subtype)) {
            return null;
        }
        List<String> parameterNames = new ArrayList<>();
        List<String> parameterValues = new ArrayList<>();
        while (m.find()) {
            String name = m.group(1).toLowerCase();
            String value = (m.group(3) == null) ? m.group(2) : unescape(m.group(3));
            parameterNames.add(name);
            parameterValues.add(value);
        }
        double quality = 1.0;
        if (qValue != null && qValuePattern.matcher(qValue).matches()) {
            try {
                quality = Double.parseDouble(qValue);
            } catch (NumberFormatException ex) {
                // quality stays at default value
            }
        }
        return new MediaRangeSpec(type, subtype, parameterNames, parameterValues, quality);
    }

    /**
     * Parses an HTTP Accept header into a List of MediaRangeSpecs
     *
     * @param s an HTTP accept header.
     * @return A List of MediaRangeSpecs
     */
    public static List<MediaRangeSpec> parseAccept(String s) {
        List<MediaRangeSpec> result = new ArrayList<>();
        Matcher m = mediaRangePattern.matcher(s);
        while (m.find()) {
            result.add(parseRange(m.group()));
        }
        return result;
    }

    private static String unescape(String s) {
        return s.replaceAll("\\\\(.)", "$1");
    }

    private static String escape(String s) {
        return s.replaceAll("[\\\\\"]", "\\\\$0");
    }

    private MediaRangeSpec(
            String type,
            String subtype,
            List<String> parameterNames, List<String> parameterValues,
            double quality
    ) {
        this.type = type;
        this.subtype = subtype;
        this.parameterNames = Collections.unmodifiableList(parameterNames);
        this.parameterValues = parameterValues;
        this.mediaType = buildMediaType();
        this.quality = quality;
    }

    private String buildMediaType() {
        StringBuffer result = new StringBuffer();
        result.append(type);
        result.append("/");
        result.append(subtype);
        for (int i = 0; i < parameterNames.size(); i++) {
            result.append(";");
            result.append(parameterNames.get(i));
            result.append("=");
            String value = parameterValues.get(i);
            if (tokenPattern.matcher(value).matches()) {
                result.append(value);
            } else {
                result.append("\"");
                result.append(escape(value));
                result.append("\"");
            }
        }
        return result.toString();
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getMediaType() {
        return mediaType;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public String getParameter(String parameterName) {
        for (int i = 0; i < parameterNames.size(); i++) {
            if (parameterNames.get(i).equalsIgnoreCase(parameterName)) {
                return parameterValues.get(i);
            }
        }
        return null;
    }

    public boolean isWildcardType() {
        return "*".equals(type);
    }

    public boolean isWildcardSubtype() {
        return !isWildcardType() && "*".equals(subtype);
    }

    public double getQuality() {
        return quality;
    }

    public int getPrecedence(MediaRangeSpec range) {
        if (range.isWildcardType())
          return 1;
        if (!range.type.equals(type))
          return 0;
        if (range.isWildcardSubtype())
          return 2;
        if (!range.subtype.equals(subtype))
          return 0;
        if (range.getParameterNames().isEmpty())
          return 3;
        int result = 3;
        for (int i = 0; i < range.getParameterNames().size(); i++) {
            String name  = range.getParameterNames().get(i);
            String value = range.getParameter(name);
            if (!value.equals(getParameter(name)))
              return 0;
            result++;
        }
        return result;
    }

    public MediaRangeSpec getBestMatch(List<MediaRangeSpec> mediaRanges) {
        MediaRangeSpec result = null;
        int bestPrecedence = 0;
        Iterator<MediaRangeSpec> it = mediaRanges.iterator();
        while (it.hasNext()) {
            MediaRangeSpec range = it.next();
            if (getPrecedence(range) > bestPrecedence) {
                bestPrecedence = getPrecedence(range);
                result = range;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return mediaType + ";q=" + quality;
    }
}