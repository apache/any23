package org.apache.any23.extractor.rdf;

import java.util.List;
import java.util.Map.Entry;

import org.apache.any23.extractor.ExtractionResult;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

import com.github.jsonldjava.core.JsonLdTripleCallback;
import com.github.jsonldjava.core.RDFDataset;


class JSONLDJavaSink implements JsonLdTripleCallback {

    private static final String BNODE_PREFIX = "_:";

    private final ExtractionResult handler;
    private final ValueFactory valueFactory;

    JSONLDJavaSink(ExtractionResult handler, ValueFactory valueFactory) {
        this.handler = handler;
        this.valueFactory = valueFactory;
    }

    private Resource createResource(String arg) {
        if (arg.startsWith(BNODE_PREFIX)) {
            String bNodeId = arg.substring(BNODE_PREFIX.length());
            return bNodeId.isEmpty() ? valueFactory.createBNode() : valueFactory.createBNode(bNodeId);
        }
        return valueFactory.createIRI(arg);
    }

    private void writeQuad(String s, String p, Value o, String graphName) {
        if (s == null || p == null || o == null) {
            return;
        }

        if (graphName == null) {
            handler.writeTriple(createResource(s), valueFactory.createIRI(p), o);
        } else {
            Resource g = createResource(graphName);
            if (g instanceof IRI) {
                handler.writeTriple(createResource(s), valueFactory.createIRI(p), o, (IRI)g);
            }
            // TODO support resource graph names in Any23
        }
    }


    @Override
    public Object call(final RDFDataset dataset) {
        for (final Entry<String, String> nextNamespace : dataset.getNamespaces().entrySet()) {
            handler.writeNamespace(nextNamespace.getKey(), nextNamespace.getValue());
        }
        for (String graphName : dataset.keySet()) {
            final List<RDFDataset.Quad> quads = dataset.getQuads(graphName);
            if ("@default".equals(graphName)) {
                graphName = null;
            }
            for (RDFDataset.Quad quad : quads) {
                RDFDataset.Node object = quad.getObject();
                String s = quad.getSubject().getValue();
                String p = quad.getPredicate().getValue();
                String o = object.getValue();
                if (object.isLiteral()) {
                    String lang = object.getLanguage();
                    String datatype = object.getDatatype();
                    if (lang != null && !lang.isEmpty() &&
                            (datatype == null || datatype.indexOf(':') < 0
                                    || RDF.LANGSTRING.stringValue().equalsIgnoreCase(datatype)
                                    || XMLSchema.STRING.stringValue().equalsIgnoreCase(datatype))) {
                        writeQuad(s, p, valueFactory.createLiteral(o, lang), graphName);
                    } else if (datatype != null && !datatype.isEmpty()) {
                        writeQuad(s, p, valueFactory.createLiteral(o, valueFactory.createIRI(datatype)), graphName);
                    } else {
                        writeQuad(s, p, valueFactory.createLiteral(o), graphName);
                    }
                } else {
                    writeQuad(s, p, createResource(o), graphName);
                }
            }
        }
        return null;
    }

}
