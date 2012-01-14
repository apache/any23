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

package org.apache.any23.vocab;

import org.openrdf.model.URI;

/**
 * Vocabulary to map the <a href="http://microformats.org/wiki/hrecipe">hRecipe</a> microformat.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRECIPE extends Vocabulary {

    public static final String NS = SINDICE.NS + "hrecipe/";

    private static HRECIPE instance;

    public static HRECIPE getInstance() {
        if(instance == null) {
            instance = new HRECIPE();
        }
        return instance;
    }

    // Resources.
    public URI Recipe     = createClass(NS, "Recipe");
    public URI Duration   = createClass(NS, "Duration");
    public URI Ingredient = createClass(NS, "Ingredient");
    public URI Nutrition  = createClass(NS, "Nutrition");

    // Properties.
    public URI fn                     = createProperty(NS, "fn");
    public URI duration               = createProperty(NS, "duration");
    public URI durationTitle          = createProperty(NS, "durationTitle");
    public URI durationTime           = createProperty(NS, "durationTime");
    public URI photo                  = createProperty(NS, "photo");
    public URI summary                = createProperty(NS, "summary");
    public URI author                 = createProperty(NS, "author");
    public URI published              = createProperty(NS, "published");
    public URI nutrition              = createProperty(NS, "nutrition");
    public URI nutritionValue         = createProperty(NS, "nutritionValue");
    public URI nutritionValueType     = createProperty(NS, "nutritionValueType");
    public URI tag                    = createProperty(NS, "tag");
    public URI ingredient             = createProperty(NS, "ingredient");
    public URI ingredientName         = createProperty(NS, "ingredientName");
    public URI ingredientQuantity     = createProperty(NS, "ingredientQuantity");
    public URI ingredientQuantityType = createProperty(NS, "ingredientQuantityType");
    public URI instructions           = createProperty(NS, "instructions");
    public URI yield                  = createProperty(NS, "yield");

    private HRECIPE() {
        super(NS);
    }
}
