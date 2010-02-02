package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Arrays;

/**
 * Extracts the value of the &lt;title&gt; element of an
 * HTML or XHTML page.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TitleExtractor implements TagSoupDOMExtractor {
    public final static String NAME = "html-head-title";

    protected final ValueFactory valueFactory = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());

    public void run(Document in, URI documentURI, ExtractionResult out) throws IOException,
            ExtractionException {
        String title = DomUtils.find(in, "/HTML/HEAD/TITLE/text()").trim();
        if (title != null && (title.length() != 0)) {
            out.writeTriple(documentURI, DCTERMS.title, valueFactory.createLiteral(title));
        }
    }


    public ExtractorDescription getDescription() {
        return factory;
    }

    public final static ExtractorFactory<TitleExtractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    PopularPrefixes.createSubset("dcterms"),
                    Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                    "example-title.html",
                    TitleExtractor.class);
}
