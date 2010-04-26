package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Reference Test class for {@link org.deri.any23.extractor.html.TagSoupParser} parser.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 *
 */
public class TagSoupParserTest {

    private static final String page = "http://semanticweb.org/wiki/Knud_M%C3%B6ller";

    private TagSoupParser tagSoupParser;

    @After
    public void tearDown() throws RepositoryException {
        this.tagSoupParser = null;
        
    }

    @Test
    public void testExplicitEncodingBehavior() throws IOException, ExtractionException, RepositoryException {

        this.tagSoupParser = new TagSoupParser(
                new FileInputStream(
                    new File("src/test/resources/html/encoding-test.html")
                ),
                page,
                "UTF-8"
        );

        Assert.assertEquals(this.tagSoupParser.getDOM().getElementsByTagName("title").item(0).getTextContent(), 
                "Knud M\u00F6ller - semanticweb.org");
    }

    /**
     * This tests the Neko HTML parser without forcing it on using a specific encoding charset.
     * We expect that this test may fail if something changes in the Neko library, as an auto-detection of
     * the encoding.
     *
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testImplicitEncodingBehavior() throws IOException, ExtractionException, RepositoryException {

        this.tagSoupParser = new TagSoupParser(
                new FileInputStream(
                    new File("src/test/resources/html/encoding-test.html")
                ),
                page
        );

        Assert.assertNotSame(this.tagSoupParser.getDOM().getElementsByTagName("title").item(0).getTextContent(),
                "Knud M\u00F6ller - semanticweb.org");
    }

}