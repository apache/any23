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
 * Vocabulary to map the <a href="http://microformats.org/wiki/h-event">h-event</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HEvent extends Vocabulary {
    public static final String NS = SINDICE.NS + "hevent/";

    private static HEvent instance;

    public static HEvent getInstance() {
        if(instance == null) {
            instance = new HEvent();
        }
        return instance;
    }

    public URI event  = createClass(NS, "Event");


    public URI name  = createProperty(NS, "name");
    public URI summary   = createProperty(NS, "summary");
    public URI start = createProperty(NS, "start");
    public URI end   = createProperty(NS, "end");
    public URI duration = createProperty(NS, "duration");
    public URI description  = createProperty(NS, "description");
    public URI url = createProperty(NS, "url");
    public URI category  = createProperty(NS, "category");
    public URI location = createProperty(NS, "location");
    public URI attendee  = createProperty(NS, "attendee");


    private HEvent() {
        super(NS);
    }
}
