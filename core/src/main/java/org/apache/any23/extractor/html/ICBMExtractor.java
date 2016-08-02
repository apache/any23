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

package org.apache.any23.extractor.html;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Extractor for "ICBM coordinates" provided as META headers in the head
 * of an HTML page.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ICBMExtractor implements TagSoupDOMExtractor {

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {

        // ICBM is the preferred method, if two values are available it is meaningless to read both
        String props = DomUtils.find(in, "//META[@name=\"ICBM\" or @name=\"geo.position\"]/@content");
        if ("".equals(props)) return;

        String[] coords = props.split("[;,]");
        float lat, lon;
        try {
            lat = Float.parseFloat(coords[0]);
            lon = Float.parseFloat(coords[1]);
        } catch (NumberFormatException nfe) {
            return;
        }

        final ValueFactory factory = new Any23ValueFactoryWrapper(SimpleValueFactory.getInstance(), out);
        BNode point = factory.createBNode();
        out.writeTriple(extractionContext.getDocumentIRI(), expand("dcterms:related"), point);
        out.writeTriple(point, expand("rdf:type"), expand("geo:Point"));
        out.writeTriple(point, expand("geo:lat"), factory.createLiteral(Float.toString(lat)));
        out.writeTriple(point, expand("geo:long"), factory.createLiteral(Float.toString(lon)));
    }

    private IRI expand(String curie) {
        return getDescription().getPrefixes().expand(curie);
    }

    @Override
    public ExtractorDescription getDescription() {
        return ICBMExtractorFactory.getDescriptionInstance();
    }

}