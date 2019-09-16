package org.apache.any23.extractor.rdfa;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.rdf.BaseRDFExtractor;
import org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.helpers.RDFaParserSettings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.rdf4j.rdf.rdfa.SemarglParserSettings;
import org.semarglproject.sink.XmlSink;
import org.semarglproject.source.StreamProcessor;

import java.io.IOException;
import java.io.InputStream;

abstract class BaseRDFaExtractor extends BaseRDFExtractor {

    private final short version;

    BaseRDFaExtractor(short version) {
        super(false, false);
        this.version = version;
    }

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, InputStream in, ExtractionResult extractionResult) throws IOException, ExtractionException {

        SemarglSink rdfaSink = new SemarglSink(extractionResult, new Any23ValueFactoryWrapper(
                SimpleValueFactory.getInstance(),
                extractionResult,
                extractionContext.getDefaultLanguage()
        ));

        XmlSink xmlSink = RdfaParser.connect(rdfaSink);
        xmlSink.setProperty(StreamProcessor.PROCESSOR_GRAPH_HANDLER_PROPERTY, rdfaSink);
        xmlSink.setProperty(RdfaParser.RDFA_VERSION_PROPERTY, version);
        xmlSink.setProperty(RdfaParser.ENABLE_VOCAB_EXPANSION, RDFaParserSettings.VOCAB_EXPANSION_ENABLED.getDefaultValue());
        xmlSink.setProperty(RdfaParser.ENABLE_PROCESSOR_GRAPH, SemarglParserSettings.PROCESSOR_GRAPH_ENABLED.getDefaultValue());

        String baseUri = extractionContext.getDocumentIRI().stringValue();
        xmlSink.setBaseUri(baseUri);
        Document doc = Jsoup.parse(in, null, baseUri, Parser.htmlParser().settings(ParseSettings.preserveCase));
        try {
            xmlSink.startDocument();
            doc.traverse(new JsoupScanner(xmlSink));
            xmlSink.endDocument();
        } catch (Exception e) {
            extractionResult.notifyIssue(IssueReport.IssueLevel.FATAL, toString(e), -1, -1);
        }
    }
}
