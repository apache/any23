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

package org.apache.any23.extractor.html.microformats2;

import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.TagSoupExtractionResult;
import org.apache.any23.vocab.VCard;
import org.openrdf.model.BNode;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;
import org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import org.apache.any23.extractor.html.HTMLDocument;
/**
 * Extractor for the <a href="http://microformats.org/wiki/h-geo">h-geo</a>
 * microformat.
 *
 * @author Nisala Nirmana
 */
public class HGeoExtractor extends EntityBasedMicroformatExtractor {

    private static final VCard vVCARD = VCard.getInstance();

    @Override
    public ExtractorDescription getDescription() {
        return HGeoExtractorFactory.getDescriptionInstance();
    }

    protected String getBaseClassName() {
        return "h-geo";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) {
        if (null == node) return false;
        final HTMLDocument document = new HTMLDocument(node);
        HTMLDocument.TextField latNode = document.getSingularTextField("p-latitude");
        HTMLDocument.TextField lonNode = document.getSingularTextField("p-longitude");
        HTMLDocument.TextField altNode = document.getSingularTextField("p-altitude");
        String lat = latNode.value();
        String lon = lonNode.value();
        String alt = altNode.value();
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
        conditionallyAddStringProperty(
                altNode.source(),
                geo, vVCARD.altitude, alt
        );

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot( document.getPathToLocalRoot(), geo, this.getClass() );

        return true;
    }
    
}
