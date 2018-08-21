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
import biweekly.io.ParseWarning;
import biweekly.io.SkipMeException;
import biweekly.io.StreamReader;
import biweekly.io.TimezoneInfo;
import biweekly.io.WriteContext;
import biweekly.io.scribe.ScribeIndex;
import biweekly.io.scribe.component.ICalComponentScribe;
import biweekly.io.scribe.property.ICalPropertyScribe;
import biweekly.property.ICalProperty;
import com.github.mangstadt.vinnie.io.VObjectPropertyValues;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.vocab.ICAL;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Objects;

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
                          ExtractionResult extractionResult) throws IOException, ExtractionException {
        ScribeIndex index = new ScribeIndex();
        try (StreamReader reader = reader(inputStream)) {
            ICalendar cal;
            while ((cal = reader.readNext()) != null) {
                for (ParseWarning warning : reader.getWarnings()) {
                    String message = warning.getMessage();
                    Integer lineNumber = warning.getLineNumber();
                    if (lineNumber == null) {
                        extractionResult.notifyIssue(IssueReport.IssueLevel.WARNING, message, -1, -1);
                    } else {
                        extractionResult.notifyIssue(IssueReport.IssueLevel.WARNING, message, lineNumber, -1);
                    }
                }

                BNode calNode = f.createBNode();
                extractionResult.writeTriple(calNode, RDF.TYPE, vICAL.Vcalendar);
                extract(index, cal.getTimezoneInfo(), calNode, cal, extractionResult);
            }
        } catch (Exception e) {
            extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, toString(e), -1, -1);
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
        if (typeName.isEmpty()) {
            return "";
        }
        int ind = Character.charCount(typeName.codePointAt(0));
        return typeName.substring(0, ind).toUpperCase(Locale.ENGLISH) + typeName.substring(ind);
    }

    private static String localNameOfProperty(String propertyName) {
        String[] nameComponents = propertyName.split("-");
        StringBuilder sb = new StringBuilder(propertyName.length());
        sb.append(nameComponents[0]);
        for (int i = 1, len = nameComponents.length; i < len; i++) {
            sb.append(localNameOfType(nameComponents[i]));
        }
        return sb.toString();
    }

    private static IRI type(ICalComponentScribe<?> scribe, ExtractionResult result) {
        if (scribe == null) {
            return null;
        }
        String originalName = scribe.getComponentName();
        String name = originalName.toLowerCase(Locale.ENGLISH);

        if (name.startsWith("x-")) {
            //non-standard class
            return f.createIRI(ICAL.NS, "X-" + localNameOfType(name.substring(2)));
        }

        name = localNameOfType(name);

        try {
            return Objects.requireNonNull(vICAL.getClass(name));
        } catch (RuntimeException e) {
            IRI iri = f.createIRI(ICAL.NS, name);
            result.notifyIssue(IssueReport.IssueLevel.ERROR,
                    "class " + iri + " (" + originalName + ") not defined in " + ICAL.class.getName(),
                    -1, -1);
            return iri;
        }
    }

    private static IRI predicate(ICalPropertyScribe<?> scribe, ExtractionResult result) {
        if (scribe == null) {
            return null;
        }
        String originalName = scribe.getPropertyName(ICalVersion.V2_0);
        String name = originalName.toLowerCase(Locale.ENGLISH);
        if (name.startsWith("x-")) {
            //non-standard property
            return f.createIRI(ICAL.NS, "x-" + localNameOfProperty(name.substring(2)));
        }

        name = localNameOfProperty(name);

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

    @SuppressWarnings("unchecked")
    private static <T extends ICalProperty> Value value(ICalPropertyScribe<T> scribe, ICalProperty property, TimezoneInfo info) {
        try {
            T prop = (T)property;
            String text = scribe.writeText(prop, new WriteContext(ICalVersion.V2_0, info, null));
            if (text == null) {
                return null;
            }
            text = VObjectPropertyValues.unescape(text);
            ICalDataType dataType = scribe.dataType(prop, ICalVersion.V2_0);
            if (ICalDataType.URI.equals(dataType) || ICalDataType.URL.equals(dataType)) {
                try {
                    return f.createIRI(text.trim());
                } catch (IllegalArgumentException e) {
                    //ignore
                }
            }
            return f.createLiteral(text);
        } catch (SkipMeException e) {
            return null;
        }
    }

    private static void extract(ScribeIndex index, TimezoneInfo info, BNode node, ICalComponent component, ExtractionResult extractionResult) {
        for (ICalProperty property : component.getProperties().values()) {
            ICalPropertyScribe<?> scribe = index.getPropertyScribe(property);
            IRI predicate = predicate(scribe, extractionResult);
            if (predicate != null) {
                Value value = value(scribe, property, info);
                if (value != null) {
                    extractionResult.writeTriple(node, predicate, value);
                }
            }
        }
        for (ICalComponent child : component.getComponents().values()) {
            BNode childNode = f.createBNode();
            extractionResult.writeTriple(node, vICAL.component, childNode);
            IRI childType = type(index.getComponentScribe(child), extractionResult);
            if (childType != null) {
                extractionResult.writeTriple(childNode, RDF.TYPE, childType);
            }
            extract(index, info, childNode, child, extractionResult);
        }
    }

}

