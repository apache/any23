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

package org.apache.any23.validator;

import org.apache.any23.validator.rule.MetaNameMisuseFix;
import org.apache.any23.validator.rule.MetaNameMisuseRule;
import org.apache.xerces.dom.DocumentImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;

/**
 * Test case for {@link XMLValidationReportSerializer}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class XMLValidationReportSerializerTest {

    private static final Logger logger = LoggerFactory.getLogger(XMLValidationReportSerializerTest.class);

    private XMLValidationReportSerializer serializer;

    @Before
    public void setUp() {
        serializer = new XMLValidationReportSerializer();
    }

    @After
    public void tearDown() {
        serializer = null;
    }

    @Test
    public void testSerializeEmptyReport() throws SerializationException {
        ValidationReport emptyReport = EmptyValidationReport.getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(emptyReport, baos);

        Assert.assertTrue(baos.size() > 0);
    }

    @Test
    public void testSerialize()
    throws SerializationException, IllegalAccessException, InstantiationException {
        ValidationReportBuilder validationReportBuilder = new DefaultValidationReportBuilder();

        Document document = new DocumentImpl();
        Element element = document.createElement("html");
        validationReportBuilder.reportIssue(ValidationReport.IssueLevel.INFO, "Test message", element);

        validationReportBuilder.traceRuleActivation( new MetaNameMisuseRule() );

        validationReportBuilder.reportRuleError(
                new MetaNameMisuseRule(),
                new RuntimeException("Fake exc message"),
                "Fake message"
        );

        validationReportBuilder.reportFixError(
                new MetaNameMisuseFix(),
                new RuntimeException("Fake exc message"),
                "Fake message"
        );

        ValidationReport vr = validationReportBuilder.getReport();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(vr, baos);
        logger.debug( baos.toString() );

        final String bufferContent = baos.toString();
        Assert.assertTrue(bufferContent.contains("<validationReport>"));
        Assert.assertTrue(bufferContent.contains("</validationReport>"));
        Assert.assertTrue(bufferContent.contains("<issue>"));
        Assert.assertTrue(bufferContent.contains("</issue>"));
        Assert.assertTrue(bufferContent.contains("<ruleActivation>"));
        Assert.assertTrue(bufferContent.contains("</ruleActivation>"));
        Assert.assertTrue(bufferContent.contains("<ruleError>"));
        Assert.assertTrue(bufferContent.contains("</ruleError>"));
        Assert.assertTrue(bufferContent.contains("<fixError>"));
        Assert.assertTrue(bufferContent.contains("</fixError>"));
    }

}
