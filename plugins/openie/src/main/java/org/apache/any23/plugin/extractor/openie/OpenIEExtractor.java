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
package org.apache.any23.plugin.extractor.openie;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.plugin.Author;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.util.StreamUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import edu.knowitall.openie.Argument;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;
import scala.collection.JavaConversions;
import scala.collection.Seq;

/**
 * An <a href="https://github.com/allenai/openie-standalone">OpenIE</a> 
 * extractor able to generate <i>RDF</i> statements from 
 * sentences representing relations in the text.
 */
@Author(name="Lewis John McGibbney (lewismc@apache.org)")
public class OpenIEExtractor implements Extractor.TagSoupDOMExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(OpenIEExtractor.class);

    /**
     * default constructor
     */
    public OpenIEExtractor() {
        // default constructor
    }

    /**
     * @see org.apache.any23.extractor.Extractor#getDescription()
     */
    @Override
    public ExtractorDescription getDescription() {
        return OpenIEExtractorFactory.getDescriptionInstance();
    }

    @Override
    public void run(ExtractionParameters extractionParameters,
            ExtractionContext context, Document in, ExtractionResult out)
                    throws IOException, ExtractionException {

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        //free up as much memory as possible before performing this calculation
        runtime.gc();
        long usedMemory = Math.max(0L, runtime.totalMemory() - runtime.freeMemory());
        long availableMemory = maxMemory - usedMemory;
        if (availableMemory < 4294967296L) {
            out.notifyIssue(IssueReport.IssueLevel.FATAL,
                    "Not enough heap space available to perform OpenIE extraction: "
                            + (availableMemory/1048576L) + "/" + (maxMemory / 1048576L)
                            + " MB. Requires 4096 MB.", -1, -1);
            LOG.error("Increase JVM heap size when running OpenIE extractor. max=" + maxMemory + "; available=" + availableMemory);
            return;
        }

        IRI documentIRI = context.getDocumentIRI();
        RDFUtils.iri(documentIRI.toString() + "root");
        out.writeNamespace(RDF.PREFIX, RDF.NAMESPACE);
        out.writeNamespace(RDFS.PREFIX, RDFS.NAMESPACE);
        LOG.debug("Processing: {}", documentIRI.toString());

        OpenIE openIE = new OpenIE(
                new ClearParser(
                        new ClearPostagger(
                                new ClearTokenizer())), new ClearSrl(), false, false);

        Seq<Instance> extractions = null;
        Tika tika = new Tika();
        try {
            extractions = openIE.extract(tika.parseToString(StreamUtils.documentToInputStream(in)));
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            LOG.error("Encountered error during OpenIE extraction.", e);
        } catch (TikaException e) {
            LOG.error("Encountered error whilst parsing InputStream with Tika.", e);
        }

        List<Instance> listExtractions = JavaConversions.seqAsJavaList(extractions);
        // for each extraction instance we can obtain a number of extraction elements
        // instance.confidence() - a confidence value for the extraction itself
        // instance.extr().context() - an optional representation of the context for this extraction
        // instance.extr().arg1().text() - subject
        // instance.extr().rel().text() - predicate
        // instance.extr().arg2s().text() - object
        String thresholdString;
        try {
            thresholdString = extractionParameters.getProperty("any23.extraction.openie.confidence.threshold");
        } catch (RuntimeException e) {
            thresholdString = null;
        }
        double threshold = thresholdString == null ? 0.5 : Double.parseDouble(thresholdString);
        for(Instance instance : listExtractions) {
            if (instance.confidence() > threshold) {
                List<Argument> listArg2s = JavaConversions.seqAsJavaList(instance.extr().arg2s());
                for(Argument argument : listArg2s) {
                    Resource subject = RDFUtils.makeIRI(instance.extr().arg1().text(), documentIRI);
                    IRI predicate = (IRI) RDFUtils.makeIRI(instance.extr().rel().text(), documentIRI);
                    Value object = RDFUtils.toValue(argument.text());
                    out.writeTriple(subject, predicate, object);
                }
            }
        }
    }
}
