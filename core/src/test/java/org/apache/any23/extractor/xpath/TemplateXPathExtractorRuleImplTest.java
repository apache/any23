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

package org.apache.any23.extractor.xpath;

import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.html.TagSoupParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test case for {@link TemplateXPathExtractionRuleImpl}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateXPathExtractorRuleImplTest {

    private TemplateXPathExtractionRule xPathExtractionRule;

    @Before
    public void setUp() {
        xPathExtractionRule = new TemplateXPathExtractionRuleImpl("test-name", "http://test/pattern/*");
    }

    @After
    public void tearDown() {
        xPathExtractionRule = null;
    }

    @Test
    public void testAddRemoveVariables() {
        final Variable v1 = new Variable("v1", "/a/b/c1");
        final Variable v2 = new Variable("v2", "/a/b/c2");
        final Variable v3 = new Variable("v3", "/a/b/c3");

        xPathExtractionRule.add(v1);
        xPathExtractionRule.add(v2);
        xPathExtractionRule.add(v3);

        Assert.assertTrue ( xPathExtractionRule.remove(v1) );
        Assert.assertTrue ( xPathExtractionRule.remove(v2) );
        Assert.assertTrue ( xPathExtractionRule.remove(v3) );
        Assert.assertFalse( xPathExtractionRule.remove(v3) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddVariableSameNameCheck() {
        xPathExtractionRule.add( new Variable("v1", "/a") );
        xPathExtractionRule.add( new Variable("v1", "/b") );
    }

    @Test
    public void testAddRemoveTemplates() {
        final QuadTemplate template1 = new QuadTemplate(
                new TemplateSubject(TemplateSubject.Type.URI, "http://sub1", false),
                new TemplatePredicate("http://pred1", false),
                new TemplateObject(TemplateObject.Type.URI, "http://obj1", false),
                new TemplateGraph("http://graph1", false)
        );
        final QuadTemplate template2 = new QuadTemplate(
                new TemplateSubject(TemplateSubject.Type.URI, "http://sub2", false),
                new TemplatePredicate("http://pred2", false),
                new TemplateObject(TemplateObject.Type.URI, "http://obj2", false),
                new TemplateGraph("http://graph2", false)
        );

        xPathExtractionRule.add(template1);
        xPathExtractionRule.add(template2);
        Assert.assertTrue(xPathExtractionRule.remove(template1));
        Assert.assertTrue(xPathExtractionRule.remove(template2));

        xPathExtractionRule.add(new Variable("v1", "//"));
        final QuadTemplate template3 = new QuadTemplate(
                new TemplateSubject(TemplateSubject.Type.URI, "http://sub2", false),
                new TemplatePredicate("http://pred2", false),
                new TemplateObject(TemplateObject.Type.URI, "v1", true),
                new TemplateGraph("http://graph2", false)
        );
        xPathExtractionRule.add(template3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddTemplateWithNoDeclaredVarCheck() {
        xPathExtractionRule.add(
                new QuadTemplate(
                        new TemplateSubject(TemplateSubject.Type.URI, "http://sub2", false),
                        new TemplatePredicate("http://pred2", false),
                        new TemplateObject(TemplateObject.Type.URI, "v1", true),
                        new TemplateGraph("http://graph2", false)
                )
        );
    }

    @Test
    public void testAcceptIRI() {
        Assert.assertTrue( xPathExtractionRule.acceptIRI(
                SimpleValueFactory.getInstance().createIRI("http://test/pattern/page"))
        );
        Assert.assertFalse( xPathExtractionRule.acceptIRI(
                SimpleValueFactory.getInstance().createIRI("http://test/wrong/page"))
        );
    }

    @Test
    public void testProcess() throws IOException {
        final QuadTemplate template1 = new QuadTemplate(
                new TemplateSubject(TemplateSubject.Type.URI, "http://sub1", false),
                new TemplatePredicate("http://pred1", false),
                new TemplateObject(TemplateObject.Type.LITERAL, "v1", true),
                new TemplateGraph("http://graph1", false)
        );
        final QuadTemplate template2 = new QuadTemplate(
                new TemplateSubject(TemplateSubject.Type.URI, "http://sub2", false),
                new TemplatePredicate("v2", true),
                new TemplateObject(TemplateObject.Type.URI, "http://obj2", false),
                new TemplateGraph("http://graph2", false)
        );

        xPathExtractionRule.add( new Variable("v1", "/html/body/div[1]") );
        xPathExtractionRule.add( new Variable("v2", "/html/body/div[2]") );
        xPathExtractionRule.add(template1);
        xPathExtractionRule.add(template2);

        final String documentIRI = "http://www.page.com/test-uri";
        final InputStream testData = this.getClass().getResourceAsStream("xpathextractor-test.html");
        final TagSoupParser tagSoupParser = new TagSoupParser(testData, documentIRI);
        final ExtractionResult extractionResult = mock(ExtractionResult.class);
        xPathExtractionRule.process(tagSoupParser.getDOM(), extractionResult);

        verify(extractionResult).writeTriple(
                SimpleValueFactory.getInstance().createIRI("http://sub1"),
                SimpleValueFactory.getInstance().createIRI("http://pred1"),
                SimpleValueFactory.getInstance().createLiteral("value1"),
                SimpleValueFactory.getInstance().createIRI("http://graph1")
        );

        verify(extractionResult).writeTriple(
                SimpleValueFactory.getInstance().createIRI("http://sub2"),
                SimpleValueFactory.getInstance().createIRI("http://test.dom/uri"),
                SimpleValueFactory.getInstance().createIRI("http://obj2"),
                SimpleValueFactory.getInstance().createIRI("http://graph2")
        );
    }

}
