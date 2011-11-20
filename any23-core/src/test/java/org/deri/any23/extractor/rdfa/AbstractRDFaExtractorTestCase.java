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

import org.deri.any23.extractor.html.AbstractExtractorTestCase;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.FOAF;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a common set of tests for an <i>RDFa</i> extractor.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class AbstractRDFaExtractorTestCase extends AbstractExtractorTestCase {

    protected static final DCTERMS vDCTERMS = DCTERMS.getInstance();
    protected static final FOAF vFOAF       = FOAF.getInstance();

    Logger logger = LoggerFactory.getLogger(RDFaExtractorTest.class);

    /**
     * Verify the basic RDFa support.
     *
     * @throws org.openrdf.repository.RepositoryException
     */
    @Test
    public void testBasic() throws RepositoryException {
        assertExtracts("html/rdfa/basic.html");
        System.out.println( dumpModelToNQuads() );
        assertContains(null, vDCTERMS.creator, RDFUtils.literal("Alice", "en") );
        assertContains(null, vDCTERMS.title  , RDFUtils.literal("The trouble with Bob", "en") );
        assertContains(null, RDFUtils.uri("http://fake.org/prop"), RDFUtils.literal("Mary", "en") );
    }

    /**
     * This test check if the
     * <a href=""http://www.w3.org/TR/2010/WD-rdfa-core-20100422/#s_curieprocessing">RDFa1.1 CURIEs</a>
     * expansion is correct and backward compatible with
     * <a href="http://www.w3.org/TR/rdfa-syntax/#s_curieprocessing">RDFa 1.0</a>.
     *
     * @throws RepositoryException
     */
    @Test
    public void testRDFa11CURIEs() throws RepositoryException {
        assertExtracts("html/rdfa/rdfa-11-curies.html");
        assertModelNotEmpty();
        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                RDFUtils.uri("http://dbpedia.org/name"),
                RDFUtils.literal("Albert Einstein")
        );
        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                RDFUtils.uri("http://dbpedia.org/knows"),
                RDFUtils.uri("http://dbpedia.org/resource/Franklin_Roosevlet")
        );
         assertContains(
                RDFUtils.uri("http://database.org/table/Departments"),
                RDFUtils.uri("http://database.org/description"),
                RDFUtils.literal("Tables listing departments")
        );
        assertContains(
                RDFUtils.uri("http://database.org/table/Departments"),
                RDFUtils.uri("http://database.org/owner"),
                RDFUtils.uri("http://database.org/people/Davide_Palmisano")
        );
        assertContains(
                RDFUtils.uri("http://database.org/table/Departments"),
                RDFUtils.uri("http://xmlns.org/foaf/01/author"),
                RDFUtils.uri("http://database.org/people/Davide_Palmisano")
        );
        assertContains(
                RDFUtils.uri("http://database.org/table/Departments"),
                RDFUtils.uri("http://purl.org/dc/01/name"),
                RDFUtils.literal("Departments")
        );
        assertStatementsSize(null, null, null, 6);
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * This test checks if the subject of a property modeled as <i>RDFa</i> in a <i>XHTML</i> document
     * where the subject contains inner <i>XML</i> tags is represented as a plain <i>Literal</i> stripping all
     * the inner tags.
     * For details see the <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa in XHTML: Syntax and Processing</a>
     * recommendation.
     *
     * @throws RepositoryException
     */
    @Test
    public void testEmptyDatatypeDeclarationWithInnerXMLTags() throws RepositoryException {
        assertExtracts("html/rdfa/null-datatype-test.html");
        logger.debug(dumpModelToRDFXML());

        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                vFOAF.name,
                RDFUtils.literal("Albert Einstein", "en")
        );

    }

    /**
     * This test checks if the <i>RDF</i> extraction is compliant to the
     * <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa in XHTML: Syntax and Processing</a> specification against the
     * <a href="http://files.openspring.net/tmp/drupal-test-frontpage.html">Drupal test page</a>.
     *
     * @throws org.openrdf.repository.RepositoryException
     */
    @Test
    public void testDrupalTestPage() throws RepositoryException {
        assertExtracts("html/rdfa/drupal-test-frontpage.html");
        logger.debug(dumpModelToTurtle());
        assertContains(
                RDFUtils.uri("http://bob.example.com/node/3"),
                vDCTERMS.title,
                RDFUtils.literal("A blog post...", "en")
        );
    }

    /**
     * See RDFa 1.1 Specification section 6.2 .
     *
     * @throws RepositoryException
     */
    @Test
    public void testIncompleteTripleManagement() throws RepositoryException {
        assertExtracts("html/rdfa/incomplete-triples.html");
        logger.debug(dumpModelToTurtle());

        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                RDFUtils.uri("http://dbpedia.org/property/birthPlace"),
                RDFUtils.uri("http://dbpedia.org/resource/Germany")
        );
        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Germany"),
                RDFUtils.uri("http://dbpedia.org/property/conventionalLongName"),
                RDFUtils.literal("Federal Republic of Germany")
        );
        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                RDFUtils.uri("http://dbpedia.org/property/citizenship"),
                RDFUtils.uri("http://dbpedia.org/resource/Germany")
        );
        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                RDFUtils.uri("http://dbpedia.org/property/citizenship"),
                RDFUtils.uri("http://dbpedia.org/resource/United_States")
        );
    }

}
