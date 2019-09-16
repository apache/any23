package org.apache.any23.extractor.rdfa;

import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.IssueReport;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;

final class SemarglSink implements org.semarglproject.sink.TripleSink, org.semarglproject.rdf.ProcessorGraphHandler {

    private static final String BNODE_PREFIX = org.semarglproject.vocab.RDF.BNODE_PREFIX;

    private final ExtractionResult handler;
    private final ValueFactory valueFactory;

    SemarglSink(ExtractionResult handler, ValueFactory valueFactory) {
        this.handler = handler;
        this.valueFactory = valueFactory;
    }

    private Resource createResource(String arg) {
        if (arg.startsWith(BNODE_PREFIX)) {
            return valueFactory.createBNode(arg.substring(BNODE_PREFIX.length()));
        }
        return valueFactory.createIRI(arg);
    }

    private void writeTriple(String s, String p, Value o) {
        handler.writeTriple(createResource(s), valueFactory.createIRI(p), o);
    }

    @Override
    public final void addNonLiteral(String s, String p, String o) {
        writeTriple(s, p, createResource(o));
    }

    @Override
    public final void addPlainLiteral(String s, String p, String o, String lang) {
        writeTriple(s, p, lang == null ? valueFactory.createLiteral(o) : valueFactory.createLiteral(o, lang));
    }

    @Override
    public final void addTypedLiteral(String s, String p, String o, String type) {
        writeTriple(s, p, valueFactory.createLiteral(o, valueFactory.createIRI(type)));
    }

    @Override
    public void startStream() {

    }

    @Override
    public void endStream() {
    }

    @Override
    public boolean setProperty(String key, Object value) {
        return false;
    }

    @Override
    public void setBaseUri(String baseUri) {
    }

    @Override
    public void info(String infoClass, String message) {

    }

    @Override
    public void warning(String warningClass, String message) {
        handler.notifyIssue(IssueReport.IssueLevel.WARNING, message, -1, -1);
    }

    @Override
    public void error(String errorClass, String message) {
        handler.notifyIssue(IssueReport.IssueLevel.ERROR, message, -1, -1);
    }
}
