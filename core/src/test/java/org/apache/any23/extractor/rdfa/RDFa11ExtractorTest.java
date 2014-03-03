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

package org.apache.any23.extractor.rdfa;

import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.FOAF;
import org.apache.any23.vocab.OGP;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
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
        assertExtract("/html/rdfa/object-resource-test.html");
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
        assertExtract("/html/rdfa/xmlliteral-datatype-test.html");
        logger.debug(dumpModelToTurtle());

        RepositoryResult<Statement> stmts =
                conn.getStatements(RDFUtils.uri("http://dbpedia.org/resource/Albert_Einstein"),
                        vFOAF.name, null, false);
        Assert.assertTrue(stmts.hasNext());
        Value obj = stmts.next().getObject();
        Assert.assertTrue(obj instanceof Literal);
        Literal lit = (Literal) obj;
        Assert.assertEquals(lit.getDatatype(), RDF.XMLLITERAL);
        Assert.assertEquals(lit.getLabel(), "Albert <strong xmlns=\"http://www.w3.org/1999/xhtml\" " +
                        "xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" " +
                        "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
                        "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" " +
                        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">Einstein</strong>");
    }

    /**
     * Tests the correct behavior of <i>REL</i> and <i>HREF</i>.
     *
     * @throws RepositoryException
     */
    @Test
    public void testRelWithHref() throws RepositoryException {
        assertExtract("/html/rdfa/rel-href.html");
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
        assertExtract("/html/rdfa/rel-rev.html");
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
        assertExtract("/html/rdfa/vocab.html");
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
        assertExtract("/html/rdfa/oreilly-invalid-datatype.html", false);
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
        final int EXPECTED_STATEMENTS = 31;

        assertExtract("/html/rdfa/goodrelations-rdfa10.html");
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
        final int EXPECTED_STATEMENTS = 31;

        assertExtract("/html/rdfa/goodrelations-rdfa11.html");
        logger.debug(dumpHumanReadableTriples());

        Assert.assertEquals(EXPECTED_STATEMENTS, dumpAsListOfStatements().size());
        assertContainsModel("/html/rdfa/goodrelations-rdfa10-expected.nq");
    }

    /**
     * Tests the correct support of the new <em>Open Graph Protocol</em>
     * <a href="http://ogp.me/#structured">Structured Properties</a>.
     *
     * @throws IOException
     * @throws org.apache.any23.extractor.ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testOpenGraphStructuredProperties() throws IOException, ExtractionException, RepositoryException {
        assertExtract("/html/rdfa/opengraph-structured-properties.html");
        logger.info( dumpHumanReadableTriples() );

        Assert.assertEquals(8, getStatementsSize(null, null, null) );
        final OGP vOGP = OGP.getInstance();
        assertContains(baseURI, vOGP.audio, RDFUtils.literal("http://example.com/bond/theme.mp3") );
        assertContains(
                baseURI,
                vOGP.description,
                RDFUtils.literal(
                        "Sean Connery found fame and fortune as the suave, sophisticated British agent, James Bond."
                )
        );
        assertContains(baseURI, vOGP.determiner, RDFUtils.literal("the") );
        assertContains(baseURI, vOGP.locale, RDFUtils.literal("en_UK") );
        assertContains(baseURI, vOGP.localeAlternate, RDFUtils.literal("fr_FR") );
        assertContains(baseURI, vOGP.localeAlternate, RDFUtils.literal("es_ES") );
        assertContains(baseURI, vOGP.siteName, RDFUtils.literal("IMDb") );
        assertContains(baseURI, vOGP.video, RDFUtils.literal("http://example.com/bond/trailer.swf") );
    }
    
    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new RDFa11ExtractorFactory();
    }

}
