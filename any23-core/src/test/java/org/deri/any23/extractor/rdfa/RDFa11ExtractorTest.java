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

import org.deri.any23.extractor.ErrorReporter;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.vocab.FOAF;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import java.io.IOException;

/**
 * Reference test class for {@link RDFa11Extractor} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */

public class RDFa11ExtractorTest extends AbstractRDFaExtractorTestCase {

    /**
     * This test verifies the correct object resource conversion.
     *
     * @throws RepositoryException
     */
    @Test
    public void testObjectResourceConversion() throws RepositoryException {
        assertExtracts("html/rdfa/object-resource-test.html");
        logger.debug(dumpModelToTurtle());
         assertContains(
                null,
                FOAF.getInstance().page,
                RDFUtils.uri("http://en.wikipedia.org/New_York")
        );
    }

    /**
     * This test checks the behavior of the <i>RDFa</i> extraction where the datatype
     * of a property is explicitly set.
     * For details see the <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa in XHTML: Syntax and Processing</a>
     * recommendation.
     *
     * @throws RepositoryException
     */
    @Test
    public void testExplicitDatatypeDeclaration() throws RepositoryException {
        assertExtracts("html/rdfa/xmlliteral-datatype-test.html");
        logger.debug(dumpModelToTurtle());

        Literal literal = RDFUtils.literal(
                "<SPAN datatype=\"rdf:XMLLiteral\" property=\"foaf:name\">Albert <STRONG>Einstein</STRONG></SPAN>",
                RDF.XMLLITERAL
        );
        assertContains(
                RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                vFOAF.name,
                literal
        );
    }

    /**
     * Tests the correct behavior of <i>REL</i> and <i>HREF</i>.
     *
     * @throws RepositoryException
     */
    @Test
    public void testRelWithHref() throws RepositoryException {
         assertExtracts("html/rdfa/rel-href.html");
        logger.debug(dumpModelToTurtle());

        assertContains(
                RDFUtils.uri( baseURI.toString(),"#me"),
                FOAF.getInstance().name,
                "John Doe"
        );
        assertContains(
                RDFUtils.uri( baseURI.toString(),"#me"),
                FOAF.getInstance().homepage,
                RDFUtils.uri("http://example.org/blog/")
        );
    }

    /**
     * This test verifies the correct <em>REL/REV</em> attribute usage.
     *
     * @throws RepositoryException
     */
    @Test
    public void testRelRevSupport() throws RepositoryException {
        assertExtracts("html/rdfa/rel-rev.html");
        logger.debug(dumpModelToTurtle());

        assertContains(
                baseURI,
                RDFUtils.uri("http://bob.example.com/cite"),
                RDFUtils.uri("http://www.example.com/books/the_two_towers")
        );
        assertContains(
                RDFUtils.uri("http://path/to/chapter"),
                RDFUtils.uri("http://bob.example.com/isChapterOf"),
                baseURI
        );
    }

    /**
     * Tests the <em>@vocab</em> support.
     *
     * @throws RepositoryException
     */
    @Test
    public void testVocabSupport() throws RepositoryException {
        assertExtracts("html/rdfa/vocab.html");
        logger.debug(dumpModelToTurtle());

        assertContains(
                RDFUtils.uri(baseURI.toString(), "#me"),
                RDFUtils.uri("http://xmlns.com/foaf/0.1/name"),
                RDFUtils.literal("John Doe")
        );
        assertContains(
                RDFUtils.uri(baseURI.toString(), "#me"),
                RDFUtils.uri("http://xmlns.com/foaf/0.1/homepage"),
                RDFUtils.uri("http://example.org/blog/")
        );
    }

    /**
     * Tests that the default parser settings enable tolerance in data type parsing.
     */
    @Test
    public void testTolerantParsing() {
        assertExtracts("html/rdfa/oreilly-invalid-datatype.html");
        assertError(ErrorReporter.ErrorLevel.WARN, ".*Cannot map prefix \'mailto\'.*");
    }

    /**
     * Taken from the <a href="http://www.heppnetz.de/rdfa4google/testcases.html">GoodRelations test cases</a>.
     * It checks if the extraction is the same when the namespaces are defined in <i>RDFa1.0</i>.
     *
     * @throws RepositoryException
     * @throws java.io.IOException
     * @throws org.openrdf.rio.RDFHandlerException
     * @throws org.openrdf.rio.RDFParseException
     */
    @Test
    public void testRDFa10Extraction()
    throws RepositoryException, RDFHandlerException, IOException, RDFParseException {
        final int EXPECTED_STATEMENTS = 33;

        assertExtracts("html/rdfa/goodrelations-rdfa10.html");
        logger.debug(dumpModelToNQuads());

        Assert.assertEquals(EXPECTED_STATEMENTS, dumpAsListOfStatements().size());
        assertContainsModel("/html/rdfa/goodrelations-rdfa10-expected.nq");
    }

    /**
     * Taken from the <a href="http://www.heppnetz.de/rdfa4google/testcases.html">GoodRelations test cases</a>.
     * It checks if the extraction is the same when the namespaces are defined in <i>RDFa1.1</i>.
     *
     * @throws RepositoryException
     * @throws java.io.IOException
     * @throws org.openrdf.rio.RDFHandlerException
     * @throws org.openrdf.rio.RDFParseException
     */
    @Test
    public void testRDFa11Extraction()
    throws RepositoryException, RDFHandlerException, IOException, RDFParseException {
        final int EXPECTED_STATEMENTS = 33;

        assertExtracts("html/rdfa/goodrelations-rdfa11.html");
        logger.debug(dumpHumanReadableTriples());

        Assert.assertEquals(EXPECTED_STATEMENTS, dumpAsListOfStatements().size());
        assertContainsModel("/html/rdfa/goodrelations-rdfa10-expected.nq");
    }

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return RDFa11Extractor.factory;
    }

}
