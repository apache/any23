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

package org.deri.any23.extractor.rdfa;

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.html.AbstractExtractorTestCase;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.FOAF;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference Test Class for {@link org.deri.any23.extractor.rdfa.RDFaExtractor}.
 */
public class RDFaExtractorTest extends AbstractExtractorTestCase {

    Logger logger = LoggerFactory.getLogger(RDFaExtractorTest.class);

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return RDFaExtractor.factory;
    }

    @Test
    public void testSimple() throws RepositoryException {
        assertExtracts("html/rdfa/dummy.html");
        assertContains(null, DCTERMS.creator, RDFHelper.literal("Alice", "en") );
        assertContains(null, DCTERMS.title  , RDFHelper.literal("The trouble with Bob", "en") );
    }

    /**
     * Tests that the default parser settings enable tolerance in data type parsing.
     */
    @Test
    public void testTolerantParsing() {
        assertExtracts("html/rdfa/oreilly-invalid-datatype.html");
    }

    /**
     * This test checks if the subject of a property modeled as <i>RDFa</i> in a <i>XHTML</i> document
     * where the subject contains inner <i>XML</i> tags is represented as a plain <i>Literal</i> stripping all
     * the inner tags.
     *
     * For details see the <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa in XHTML: Syntax and Processing</a>
     * recommendation. 
     *  
     * @throws RepositoryException
     */
    @Test
    public void testEmptyDatatypeDeclarationWithInnerXMLTags() throws RepositoryException {
        assertExtracts("html/rdfa/null-datatype-test.html");
        logger.info(dumpModelToRDFXML());

        assertContains(
                RDFHelper.uri("http://dbpedia.org/resource/Albert_Einstein"),
                FOAF.name,
                RDFHelper.literal("Albert Einstein", "en")
        );

    }

    /**
     * This test checks the behavior of the <i>RDFa</i> extraction where the datatype of a property is
     * explicitly set.
     *
     * For details see the <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa in XHTML: Syntax and Processing</a>
     * recommendation.
     *  
     * @throws RepositoryException
     */
    @Test
    public void testExplicitDatatypeDeclaration() throws RepositoryException {
        assertExtracts("html/rdfa/xmlliteral-datatype-test.html");
        logger.info(dumpModelToTurtle());

        Literal literal = RDFHelper.literal("Albert <STRONG xmlns=\"http://www.w3.org/1999/xhtml\" " +
                "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">Einstein</STRONG>\n", RDF.XMLLITERAL);

        assertContains(
                RDFHelper.uri("http://dbpedia.org/resource/Albert_Einstein"),
                FOAF.name,
                literal
        );

    }

    /**
     * This test checks if the <i>RDF</i> extraction is compliant to the
     * <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa in XHTML: Syntax and Processing</a> specification against the
     * <a href="http://files.openspring.net/tmp/drupal-test-frontpage.html">Drupal test page</a>.
     *
     * @throws RuntimeException
     *
     */
    @Test
    public void testDrupalTestPage() throws RepositoryException {
        assertExtracts("html/rdfa/drupal-test-frontpage.html");
        logger.info(dumpModelToTurtle());
        assertContains(
                RDFHelper.uri("http://bob.example.com/node/3"),
                DCTERMS.title,
                RDFHelper.literal("A blog post...", "en")
        );

    }

}
