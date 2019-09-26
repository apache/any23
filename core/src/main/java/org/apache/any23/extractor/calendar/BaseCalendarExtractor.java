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

package org.apache.any23.extractor.calendar;

import biweekly.ICalDataType;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.ICalComponent;
import biweekly.component.VTimezone;
import biweekly.io.ParseWarning;
import biweekly.io.SkipMeException;
import biweekly.io.StreamReader;
import biweekly.io.TimezoneAssignment;
import biweekly.io.TimezoneInfo;
import biweekly.io.WriteContext;
import biweekly.io.json.JCalValue;
import biweekly.io.json.JsonValue;
import biweekly.io.scribe.ScribeIndex;
import biweekly.io.scribe.property.ICalPropertyScribe;
import biweekly.parameter.Encoding;
import biweekly.parameter.ICalParameters;
import biweekly.property.Geo;
import biweekly.property.ICalProperty;
import biweekly.util.DateTimeComponents;
import biweekly.util.ICalDateFormat;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.vocab.ICAL;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.datatypes.XMLDatatypeUtil;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hans Brende (hansbrende@apache.org)
 */
abstract class BaseCalendarExtractor implements Extractor.ContentExtractor {

    @Override
    public void setStopAtFirstError(boolean b) {
        //unsupported
    }

    private static final ValueFactory f = SimpleValueFactory.getInstance();
    private static final ICAL vICAL = ICAL.getInstance();

    abstract StreamReader reader(InputStream inputStream);

    @Override
    public final void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, InputStream inputStream,
                          ExtractionResult result) throws IOException, ExtractionException {
        result.writeNamespace(RDF.PREFIX, RDF.NAMESPACE);
        result.writeNamespace(ICAL.PREFIX, ICAL.NS);
        result.writeNamespace(XMLSchema.PREFIX, XMLSchema.NAMESPACE);

        ScribeIndex index = new ScribeIndex();
        try (StreamReader reader = reader(inputStream)) {
            ICalendar cal;
            while ((cal = reader.readNext()) != null) {
                for (ParseWarning warning : reader.getWarnings()) {
                    String message = warning.getMessage();
                    Integer lineNumber = warning.getLineNumber();
                    if (lineNumber == null) {
                        result.notifyIssue(IssueReport.IssueLevel.WARNING, message, -1, -1);
                    } else {
                        result.notifyIssue(IssueReport.IssueLevel.WARNING, message, lineNumber, -1);
                    }
                }

                BNode calNode = f.createBNode();
                result.writeTriple(calNode, RDF.TYPE, vICAL.Vcalendar);
                WriteContext ctx = new WriteContext(ICalVersion.V2_0, cal.getTimezoneInfo(), null);
                extract(index, ctx, calNode, cal, result, true);
            }
        } catch (Exception e) {
            result.notifyIssue(IssueReport.IssueLevel.FATAL, toString(e), -1, -1);
        }
    }

    private static String toString(Throwable th) {
        StringWriter writer = new StringWriter();
        try (PrintWriter pw = new PrintWriter(writer)) {
            th.printStackTrace(pw);
        }
        String string = writer.toString();
        if (string.length() > 200) {
            return string.substring(0, 197) + "...";
        }
        return string;
    }


    private static String localNameOfType(String typeName) {
        return camelCase(typeName, false);
    }

    private static String localNameOfProperty(String propertyName) {
        return camelCase(propertyName, true);
    }

    private static String camelCase(String name, boolean forProperty) {
        String[] nameComponents = name.toLowerCase(Locale.ENGLISH).split("-");
        StringBuilder sb = new StringBuilder(name.length());
        int i = 0;
        if (forProperty) {
            sb.append(nameComponents[i++]);
        }
        for (int len = nameComponents.length; i < len; i++) {
            String n = nameComponents[i];
            if (!n.isEmpty()) {
                int ind = Character.charCount(n.codePointAt(0));
                sb.append(n.substring(0, ind).toUpperCase(Locale.ENGLISH)).append(n.substring(ind));
            }
        }
        return sb.toString();
    }

    private static IRI type(String originalName) {
        if (originalName.regionMatches(true, 0, "X-", 0, 2)) {
            //non-standard class
            return f.createIRI(ICAL.NS, "X-" + localNameOfType(originalName.substring(2)));
        }

        String name = localNameOfType(originalName);

        try {
            return Objects.requireNonNull(vICAL.getClass(name));
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static IRI predicate(String originalName, ExtractionResult result) {
        if (originalName.regionMatches(true, 0, "X-", 0, 2)) {
            //non-standard property
            return f.createIRI(ICAL.NS, "x-" + localNameOfProperty(originalName.substring(2)));
        }

        String name = localNameOfProperty(originalName);

        try {
            return Objects.requireNonNull(vICAL.getProperty(name));
        } catch (RuntimeException e) {
            IRI iri = f.createIRI(ICAL.NS, name);
            result.notifyIssue(IssueReport.IssueLevel.ERROR,
                    "property " + iri + " (" + originalName + ") not defined in " + ICAL.class.getName(),
                    -1, -1);
            return iri;
        }
    }

    private static final String NaN = Double.toString(Double.NaN);
    private static String str(Double d) {
        return d == null ? NaN : d.toString();
    }

    private static BNode writeParams(BNode subject, IRI predicate, ICalParameters params, ExtractionResult result) {
        BNode bNode = f.createBNode();
        result.writeTriple(subject, predicate, bNode);
        writeParams(bNode, params, result);
        return bNode;
    }

    private static void writeParams(BNode subject, ICalParameters params, ExtractionResult result) {
        for (Map.Entry<String, List<String>> entry : params.getMap().entrySet()) {
            List<String> strings = entry.getValue();
            if (strings != null && !strings.isEmpty()) {
                IRI predicate = predicate(entry.getKey(), result);
                for (String v : strings) {
                    result.writeTriple(subject, predicate, f.createLiteral(v));
                }
            }
        }
    }


    private static IRI dataType(ICalDataType dataType, Boolean isFloating) {
        if (dataType == null || ICalDataType.TEXT.equals(dataType)) {
            return XMLSchema.STRING;
        } else if (ICalDataType.BOOLEAN.equals(dataType)) {
            return XMLSchema.BOOLEAN;
        } else if (ICalDataType.INTEGER.equals(dataType)) {
            return XMLSchema.INTEGER;
        } else if (ICalDataType.FLOAT.equals(dataType)) {
            return XMLSchema.FLOAT;
        } else if (ICalDataType.BINARY.equals(dataType)) {
            return XMLSchema.BASE64BINARY;
        } else if (ICalDataType.URI.equals(dataType)
                || ICalDataType.URL.equals(dataType)
                || ICalDataType.CONTENT_ID.equals(dataType)
                || ICalDataType.CAL_ADDRESS.equals(dataType)) {
            return XMLSchema.ANYURI;
        } else if (ICalDataType.DATE_TIME.equals(dataType)) {
            if (isFloating == null) {
                return null;
            }
            return isFloating ? vICAL.DATE_TIME : XMLSchema.DATETIME;
        } else if (ICalDataType.DATE.equals(dataType)) {
            return XMLSchema.DATE;
        } else if (ICalDataType.TIME.equals(dataType)) {
            return XMLSchema.TIME;
        } else if (ICalDataType.DURATION.equals(dataType)) {
            return XMLSchema.DURATION;
        } else if (ICalDataType.PERIOD.equals(dataType)) {
            return vICAL.Value_PERIOD;
        } else if (ICalDataType.RECUR.equals(dataType)) {
            return vICAL.Value_RECUR;
        } else {
            return XMLSchema.STRING;
        }
    }


    private static final Pattern durationWeeksPattern = Pattern.compile("(-?P)(\\d+)W");

    private static String normalizeAndReportIfInvalid(String s, IRI dataType, TimeZone zone, ExtractionResult result) {
        if (dataType == null) {
            return s;
        }
        try {
            if (XMLSchema.DURATION.equals(dataType)) {
                Matcher m = durationWeeksPattern.matcher(s);
                if (m.matches()) {
                    long days = Long.parseLong(m.group(2)) * 7;
                    return m.group(1) + days + "D";
                }
            } else if (vICAL.Value_PERIOD.equals(dataType)) {
                if (s.indexOf('/') == -1) {
                    throw new IllegalArgumentException();
                }
            } else if (zone != null && XMLSchema.DATETIME.equals(dataType)) {
                try {
                    DateTimeComponents dt = DateTimeComponents.parse(s);
                    if (!dt.isUtc()) {
                        s = ICalDateFormat.DATE_TIME_EXTENDED.format(dt.toDate(zone), zone);
                    }
                } catch (IllegalArgumentException e) {
                    //ignore
                }
            } else {
                s = XMLDatatypeUtil.normalize(s, dataType);
            }

            if (!XMLDatatypeUtil.isValidValue(s, dataType)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            String m = e.getMessage();
            if (StringUtils.isBlank(m)) {
                m = "Not a valid " + dataType + " value: " + s;
            }
            result.notifyIssue(IssueReport.IssueLevel.ERROR, m, -1, -1);
        }
        return s;
    }

    private static boolean writeValue(BNode subject, IRI predicate, JsonValue jsonValue, String lang, IRI dataType, TimeZone zone, ExtractionResult result) {
        if (jsonValue == null || jsonValue.isNull()) {
            return false;
        }
        Object val = jsonValue.getValue();
        if (val != null) {
            Value v;
            if (val instanceof Byte) {
                v = f.createLiteral((byte)val);
            } else if (val instanceof Short) {
                v = f.createLiteral((short)val);
            } else if (val instanceof Integer) {
                v = f.createLiteral((int)val);
            } else if (val instanceof Long) {
                v = f.createLiteral((long)val);
            } else if (val instanceof Float) {
                v = f.createLiteral((float)val);
            } else if (val instanceof Double) {
                v = f.createLiteral((double)val);
            } else if (val instanceof Boolean) {
                v = f.createLiteral((boolean)val);
            } else if (val instanceof BigInteger) {
                v = f.createLiteral((BigInteger)val);
            } else if (val instanceof BigDecimal) {
                v = f.createLiteral((BigDecimal)val);
            } else {
                String str = normalizeAndReportIfInvalid(val.toString(), dataType, zone, result);

                if (XMLSchema.STRING.equals(dataType)) {
                    if (lang == null) {
                        v = f.createLiteral(str);
                    } else {
                        v = f.createLiteral(str, lang);
                    }
                } else if (XMLSchema.ANYURI.equals(dataType)) {
                    try {
                        v = f.createIRI(str);
                    } catch (IllegalArgumentException e) {
                        v = f.createLiteral(str, dataType);
                    }
                } else if (vICAL.Value_PERIOD.equals(dataType)) {
                    String[] strs = str.split("/");
                    if (strs.length == 2) {
                        String firstPart = normalizeAndReportIfInvalid(strs[0], XMLSchema.DATETIME, zone, result);
                        String secondPart = strs[1];
                        if (secondPart.indexOf('P') != -1) { //duration
                            secondPart = normalizeAndReportIfInvalid(secondPart, XMLSchema.DURATION, zone, result);
                        } else {
                            secondPart = normalizeAndReportIfInvalid(secondPart, XMLSchema.DATETIME, zone, result);
                        }
                        str = firstPart + "/" + secondPart;
                    }
                    v = f.createLiteral(str);
                } else if (dataType != null) {
                    v = f.createLiteral(str, dataType);
                } else {
                    v = f.createLiteral(str);
                }

            }
            result.writeTriple(subject, predicate, v);
            return true;
        }

        List<JsonValue> array = jsonValue.getArray();
        if (array != null && !array.isEmpty()) {
            if (array.size() == 1) {
                return writeValue(subject, predicate, array.get(0), lang, dataType, zone, result);
            } else {
                BNode bNode = f.createBNode();
                result.writeTriple(subject, predicate, bNode);
                for (JsonValue value : array) {
                    writeValue(bNode, RDF.VALUE, value, lang, dataType, zone, result);
                }
                return true;
            }
        }

        Map<String, JsonValue> object = jsonValue.getObject();
        if (object != null) {
            BNode bNode = f.createBNode();
            result.writeTriple(subject, predicate, bNode);
            for (Map.Entry<String, JsonValue> entry : object.entrySet()) {
                writeValue(bNode, predicate(entry.getKey(), result), entry.getValue(), lang, XMLSchema.STRING, zone, result);
            }
            return true;
        }

        return false;
    }

    private static TimeZone parseTimeZoneId(String tzId) {
        for (;;) {
            TimeZone zone = ICalDateFormat.parseTimeZoneId(tzId);
            if (zone != null) {
                return zone;
            }
            int ind = tzId.indexOf('/');
            if (ind == -1) {
                return null;
            }
            tzId = tzId.substring(ind + 1);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends ICalProperty> void writeProperty(BNode subject, ICalPropertyScribe<T> scribe, ICalProperty property, WriteContext ctx, ExtractionResult result) {
        try {
            T prop = (T)property;

            ICalVersion version = ctx.getVersion();

            ICalDataType dataType = scribe.dataType(prop, version);

            ICalParameters params = scribe.prepareParameters(prop, ctx);

            String lang = params.getLanguage();
            params.removeAll(ICalParameters.LANGUAGE);

            Encoding encoding = params.getEncoding();

            if (dataType == null) {
                dataType = params.getValue();
                if (dataType == null && Encoding.BASE64.equals(encoding)) {
                    dataType = ICalDataType.BINARY;
                }
            }
            params.removeAll(ICalParameters.VALUE);

            if (ICalDataType.BINARY.equals(dataType)) {
                // RFC 5545 s. 3.2.7.
                // If the value type parameter is ";VALUE=BINARY", then the inline
                // encoding parameter MUST be specified with the value
                // ";ENCODING=BASE64"
                if (encoding != null && !Encoding.BASE64.equals(encoding)) {
                    result.notifyIssue(IssueReport.IssueLevel.ERROR,
                            "Invalid encoding " + encoding + " specified for BINARY value", -1, -1);
                    dataType = null;
                }
                params.removeAll(ICalParameters.ENCODING);
            }

            if (Encoding._8BIT.equals(encoding)) {
                // RFC 5545 s. 3.2.7.
                // The default encoding is "8BIT",
                // corresponding to a property value consisting of text.
                params.removeAll(ICalParameters.ENCODING);
            }

            // RFC 5545 s. 3.1.4.
            // There is not a property parameter to declare the charset used in a
            //   property value.  The default charset for an iCalendar stream is UTF-8
            //   as defined in [RFC3629].
            params.removeAll(ICalParameters.CHARSET);

            IRI predicate = predicate(scribe.getPropertyName(version), result);

            if (ICalDataType.CAL_ADDRESS.equals(dataType)) {
                subject = writeParams(subject, predicate, params, result);
                predicate = vICAL.calAddress;
            } else if (!params.isEmpty()) {
                subject = writeParams(subject, predicate, params, result);
                predicate = RDF.VALUE;
            }

            if (prop instanceof Geo) {
                // RFC 5870
                Geo g = (Geo)prop;
                IRI value = f.createIRI("geo:" + str(g.getLatitude()) + "," + str(g.getLongitude()));
                result.writeTriple(subject, predicate, value);
            } else {

                String tzId = params.getTimezoneId();
                TimezoneInfo tzInfo = ctx.getTimezoneInfo();
                TimeZone timeZone = null;
                Boolean floating;
                if (tzId != null) {
                    TimezoneAssignment assign = tzInfo.getTimezone(prop);
                    if (assign != null) {
                        timeZone = assign.getTimeZone();
                    } else {
                        timeZone = parseTimeZoneId(tzId);
                        tzInfo.setFloating(prop, true);
                    }
                    floating = timeZone == null ? null : Boolean.FALSE;
                } else {
                    floating = tzInfo.isFloating(prop);
                }

                IRI dataTypeIRI = dataType(dataType, floating);

                JCalValue jsonVal = scribe.writeJson(prop, ctx);
                List<JsonValue> jsonVals = jsonVal.getValues();

                boolean mod = false;
                for (JsonValue value : jsonVals) {
                    mod |= writeValue(subject, predicate, value, lang, dataTypeIRI, timeZone, result);
                }
                if (!mod) {
                    result.writeTriple(subject, predicate, f.createLiteral(jsonVal.asSingle()));
                }
            }
        } catch (SkipMeException e) {
            //ignore
        }
    }

    private static void extract(ScribeIndex index, WriteContext ctx, BNode node, ICalComponent component, ExtractionResult result, boolean writeTimezones) {
        for (ICalProperty property : component.getProperties().values()) {
            ctx.setParent(component);
            writeProperty(node, index.getPropertyScribe(property), property, ctx, result);
        }

        Stream<ICalComponent> components = component.getComponents().values().stream();

        if (writeTimezones) {
            Collection<VTimezone> tzs = ctx.getTimezoneInfo().getComponents();
            Set<String> tzIds = tzs.stream()
                    .map(tz -> tz.getTimezoneId().getValue())
                    .collect(Collectors.toSet());
            components = Stream.concat(tzs.stream(), components.filter(c ->
                    !(c instanceof VTimezone && tzIds.contains(((VTimezone) c).getTimezoneId().getValue())))
            );
        }

        components.forEachOrdered(child -> {
            BNode childNode = f.createBNode();
            String componentName = index.getComponentScribe(child).getComponentName();
            IRI childType = type(componentName);

            if (childType == null) {
                result.writeTriple(node, predicate(componentName, result), childNode);
            } else {
                result.writeTriple(node, vICAL.component, childNode);
                result.writeTriple(childNode, RDF.TYPE, childType);
            }
            extract(index, ctx, childNode, child, result, false);
        });
    }

}

