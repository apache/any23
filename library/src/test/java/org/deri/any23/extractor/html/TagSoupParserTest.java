package org.deri.any23.extractor.html;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.writer.RepositoryWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;

import java.io.*;
import java.net.URISyntaxException;

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