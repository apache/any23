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

import org.apache.any23.extractor.html.TagSoupParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Test case for {@link DefaultValidator}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultValidatorTest {

    private static final Logger logger = LoggerFactory.getLogger(DefaultValidatorTest.class);

    private DefaultValidator validator;

    @Before
    public void setUp() {
        validator = new DefaultValidator();
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void testRegisterRule() {
        validator.addRule(FakeRule.class, FakeFix.class);
        List<Class<? extends Fix>> fixes = validator.getFixes(FakeRule.class);
        Assert.assertEquals("Unexpected fixes size.", 1, fixes.size());
        Assert.assertEquals("Unexpected fix.", FakeFix.class,  fixes.get(0));
        validator.removeRule(FakeRule.class);
        Assert.assertEquals("Unexpected fixes size.", 0, validator.getFixes(FakeRule.class).size());
    }

    @Test
    public void testMissingOGNamespace() throws IOException, ValidatorException, URISyntaxException {
        DOMDocument document = loadDocument("missing-og-namespace.html");
        Assert.assertNull( document.getNode("/HTML").getAttributes().getNamedItem("xmlns:og") );
        ValidationReport validationReport = validator.validate(document, true);
        Assert.assertNotNull( document.getNode("/HTML").getAttributes().getNamedItem("xmlns:og") );
        if(logger.isDebugEnabled()) {
            logger.debug( validationReport.toString() );
        }
    }
    
    @Ignore("Itemscope parsing issue")
    @Test
    public void testMissingItemscopeAttributeValue() throws IOException, URISyntaxException, ValidatorException {
      DOMDocument document = loadDocument("microdata-basic.html");
      List<Node> brokenItemScopeNodes = document.getNodesWithAttribute("itemscope");
      for (Node node : brokenItemScopeNodes) {
        // all nodes with itemscope have an empty string value
        Assert.assertEquals("", node.getAttributes().getNamedItem("itemscope").getNodeValue() );
      }
      ValidationReport validationReport = validator.validate(document, true);
      List<Node> fixedItemScopeNodes = document.getNodesWithAttribute("itemscope");
      for (Node node : fixedItemScopeNodes) {
        // all nodes with itemscope now have a default value of "itemscope"
        Assert.assertNotNull(node.getAttributes().getNamedItem("itemscope").getNodeValue() );
        Assert.assertNotEquals("", node.getAttributes().getNamedItem("itemscope").getNodeValue() );
        Assert.assertEquals("itemscope", node.getAttributes().getNamedItem("itemscope").getNodeValue());
      }
      if(logger.isDebugEnabled()) {
          logger.debug( validationReport.toString() );
      }
  }

    @Test
    public void testMetaNameMisuse() throws Exception {
        DOMDocument document = loadDocument("meta-name-misuse.html");
        ValidationReport validationReport = validator.validate(document, true);
        logger.debug( validationReport.toString() );
        if(logger.isDebugEnabled()) {
            logger.debug( serialize(document) );
        }

        List<Node> metas = document.getNodes("/HTML/HEAD/META");
        for(Node meta : metas) {
            Node name = meta.getAttributes().getNamedItem("name");
            if(name != null) {
                Assert.assertFalse( name.getTextContent().contains(":") );
            }
        }
    }

    @Test
    public void testAboutNotIRIRule() throws Exception {
        DOMDocument document = loadDocument("invalid-rdfa-about.html");
        ValidationReport validationReport = validator.validate(document, true);
        logger.debug(validationReport.toString());
        Assert.assertEquals( "Unexpected number of issues.", 1, validationReport.getIssues().size() );
    }

    private DOMDocument loadDocument(String document) throws IOException, URISyntaxException {
        InputStream is = this.getClass().getResourceAsStream(document);
        final String documentIRI = "http://test.com";
        TagSoupParser tsp = new TagSoupParser(is, documentIRI);
        return new DefaultDOMDocument( new URI(documentIRI), tsp.getDOM() );
    }

    private String serialize(DOMDocument document) throws Exception {
        OutputFormat format = new OutputFormat(document.getOriginalDocument());
        format.setLineSeparator("\n");
        format.setIndenting(true);
        format.setLineWidth(0);
        format.setPreserveSpace(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer(
                baos,
                format
        );
        serializer.asDOMSerializer();
        serializer.serialize( document.getOriginalDocument() );
        return baos.toString();
    }

    class FakeRule implements Rule {
        public String getHRName() {
            return "fake-rule";
        }

        public boolean applyOn(
                DOMDocument document,
                @SuppressWarnings("rawtypes") RuleContext context,
                ValidationReportBuilder validationReportBuilder
        ) {
            throw new UnsupportedOperationException();
        }
    }

    class FakeFix implements Fix {
        public String getHRName() {
            return "fake-fix";
        }

        public void execute(Rule rule, @SuppressWarnings("rawtypes") RuleContext context, DOMDocument document) {
              throw new UnsupportedOperationException();
        }
    }

}
