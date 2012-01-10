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

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.TagSoupExtractionResult;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.VCARD;
import org.openrdf.model.BNode;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.Arrays;

import static org.deri.any23.extractor.html.HTMLDocument.TextField;


/**
 * Extractor for the <a href="http://microformats.org/wiki/geo">Geo</a>
 * microformat.
 *
 * @author Gabriele Renzi
 */
public class GeoExtractor extends EntityBasedMicroformatExtractor {

    private static final VCARD vVCARD = VCARD.getInstance();

    public static final ExtractorFactory<GeoExtractor> factory =
            SimpleExtractorFactory.create(
                "html-mf-geo",
                PopularPrefixes.createSubset("rdf", "vcard"),
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf-geo.html",
                GeoExtractor.class
            );

    public ExtractorDescription getDescription() {
        return factory;
    }

    protected String getBaseClassName() {
        return "geo";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) {
        if (null == node) return false;
        //try lat & lon
        final HTMLDocument document = new HTMLDocument(node);
        HTMLDocument.TextField latNode = document.getSingularTextField("latitude" );
        TextField lonNode = document.getSingularTextField("longitude");
        String lat = latNode.value();
        String lon = lonNode.value();
        if ("".equals(lat) || "".equals(lon)) {
            String[] both = document.getSingularUrlField("geo").value().split(";");
            if (both.length != 2) return false;
            lat = both[0];
            lon = both[1];
        }
        BNode geo = getBlankNodeFor(node);
        out.writeTriple(geo, RDF.TYPE, vVCARD.Location);
        final String extractorName = getDescription().getExtractorName();
        conditionallyAddStringProperty(
                latNode.source(),
                geo, vVCARD.latitude , lat
        );
        conditionallyAddStringProperty(
                lonNode.source(),
                geo, vVCARD.longitude, lon
        );

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot( document.getPathToLocalRoot(), geo, this.getClass() );

        return true;
    }
    
}
