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
import org.apache.any23.extractor.html.microformats2.annotations.Includes;
import org.apache.any23.vocab.VCard;
import org.openrdf.model.BNode;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;
import org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import org.apache.any23.extractor.html.HTMLDocument;

/**
 * Extractor for the <a href="http://microformats.org/wiki/h-adr">h-adr</a>
 * microformat.
 *
 * @author Nisala Nirmana
 */
@Includes( extractors = HGeoExtractor.class )
public class HAdrExtractor extends EntityBasedMicroformatExtractor {

    private static final VCard vVCARD = VCard.getInstance();

    private static final String[] addressFields = {
            "p-street-address",
            "p-extended-address",
            "p-locality",
            "p-region",
            "p-postal-code",
            "p-country-name",
            "p-geo"
    };

    protected String getBaseClassName() {
        return "h-adr";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) {
        if (null == node) return false;
        final HTMLDocument document = new HTMLDocument(node);
        BNode adr = getBlankNodeFor(node);
        out.writeTriple(adr, RDF.TYPE, vVCARD.Address);
        final String extractorName = getDescription().getExtractorName();
        for (String field : addressFields) {
            HTMLDocument.TextField[] values = document.getPluralTextField(field);
            for (HTMLDocument.TextField val : values) {
               if(!field.equals("p-geo")) {
                        conditionallyAddStringProperty(
                                val.source(),
                                adr, vVCARD.getProperty(field.replaceFirst("p-", "")), val.value()
                        );
               }else {
                   String[] composed = val.value().split(";");
                   if (composed.length == 3){
                       conditionallyAddStringProperty(
                               val.source(),
                               adr, vVCARD.latitude, composed[0]
                       );
                       conditionallyAddStringProperty(
                               val.source(),
                               adr, vVCARD.longitude, composed[1]
                       );
                       conditionallyAddStringProperty(
                               val.source(),
                               adr, vVCARD.altitude, composed[2]
                       );

                   }else if (composed.length == 2){
                       conditionallyAddStringProperty(
                               val.source(),
                               adr, vVCARD.latitude, composed[0]
                       );
                       conditionallyAddStringProperty(
                               val.source(),
                               adr, vVCARD.longitude, composed[1]
                       );
                   }else {
                       //we discard if only length is 1
                   }

               }

            }
        }

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot( document.getPathToLocalRoot(), adr, this.getClass() );

        return true;
    }

    @Override
    public ExtractorDescription getDescription() {
        return HAdrExtractorFactory.getDescriptionInstance();
    }

}
