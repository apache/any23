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

package org.apache.any23.servlet.conneg;

/**
 * Defines a {@link ContentTypeNegotiator} for <i>Any23</i>.
 */
public class Any23Negotiator {

    private final static ContentTypeNegotiator any23negotiator;

    static {
        any23negotiator = new ContentTypeNegotiator();
        any23negotiator.setDefaultAccept("text/turtle");

        any23negotiator.addVariant("application/rdf+xml;q=0.95"     )
                .addAliasMediaType("application/xml;q=0.4"          )
                .addAliasMediaType("text/xml;q=0.4"                 );

        any23negotiator.addVariant("text/rdf+n3;charset=utf-8;q=0.9")
                .addAliasMediaType("text/n3;q=0.9"                  )
                .addAliasMediaType("application/n3;q=0.9"           );

        any23negotiator.addVariant("text/rdf+nq;charset=utf-8;q=0.9")
                .addAliasMediaType("text/nq;q=0.9"                  )
                .addAliasMediaType("application/nq;q=0.9"           );

        any23negotiator.addVariant("text/turtle"                    )
                .addAliasMediaType("application/x-turtle"           )
                .addAliasMediaType("application/turtle"             );

        any23negotiator.addVariant("text/plain;q=0.5");
    }

    public static ContentTypeNegotiator getNegotiator() {
        return any23negotiator;
    }
}
