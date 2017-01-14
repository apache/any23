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

import org.eclipse.rdf4j.model.IRI;

/**
 * Vocabulary to map the <a href="http://microformats.org/wiki/h-product">h-product</a> microformat.
 *
 * @author Nisala Nirmana
 */

public class HProduct extends Vocabulary {
    public static final String NS = SINDICE.NS + "hproduct/";

    private static HProduct instance;

    public static HProduct getInstance() {
        if(instance == null) {
            instance = new HProduct();
        }
        return instance;
    }

    public IRI product  = createClass(NS, "Product");


    public IRI name  = createProperty(NS, "name");
    public IRI photo  = createProperty(NS, "photo");
    public IRI brand = createProperty(NS, "brand");
    public IRI category  = createProperty(NS, "category");
    public IRI description  = createProperty(NS, "description");
    public IRI url = createProperty(NS, "url");
    public IRI identifier = createProperty(NS, "identifier");
    public IRI price = createProperty(NS, "price");
    public IRI review  = createProperty(NS, "review");


    private HProduct() {
        super(NS);
    }

}
