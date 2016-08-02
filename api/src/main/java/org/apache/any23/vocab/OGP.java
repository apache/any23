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
 * The <a href="http://ogp.me/">Open Graph Protocol</a> vocabulary.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class OGP extends Vocabulary {

    public static final String NS = "http://ogp.me/ns#";
    
    /* BEGIN: Basic Metadata. */

    /** The title of your object as it should appear within the graph (Facebook Graph API). */
    public static final String TITLE = "title";
    /** The type of your object, e.g., <code>video.movie</code>.
     *  Depending on the type you specify, other properties may also be required. */
    public static final String TYPE  = "type";
    /** An image URL which should represent your object within the graph. */
    public static final String URL   = "url";
    /** The canonical URL of your object that will be used as its permanent ID in the graph (Facebook Graph API). */
    public static final String IMAGE = "image";

    /* END: Basic Metadata. */

    /* BEGIN: Optional Metadata. */

    /** A URL to an audio file to accompany this object. */
    public static final String AUDIO = "audio";
    /** A one to two sentence description of your object. */
    public static final String DESCRIPTION = "description";
    /** The word that appears before this object's title in a sentence. An enum of (a, an, the, "", auto).
     *  If auto is chosen, the consumer of your data should chose between "a" or "an". Default is "" (blank). */
    public static final String DETERMINER = "determiner";
    /** The locale these tags are marked up in. Of the format <code>language_TERRITORY</code>.
     *  Default is <code>en_US</code>. */
    public static final String LOCALE = "locale";
    /** An array of other locales this page is available in. */
    public static final String LOCALE__ALTERNATE = "locale:alternate";
    /** If your object is part of a larger web site, the name which should be
     *  displayed for the overall site. e.g., <b>IMDb</b>. */
    public static final String SITE_NAME = "site_name";
    /** A URL to a video file that complements this object. */
    public static final String VIDEO = "video";

    /* END: Optional Metadata. */

    /* BEGIN: Structured Properties. */

    /** Identical to <code>og:image</code>. */
    public static final String IMAGE__URL        = "image:url";
    /** An alternate url to use if the webpage requires <b>HTTPS</b>. */
    public static final String IMAGE__SECURE_URL = "image:secure_url";
    /** A <i>MIME type</i> for this image. */
    public static final String IMAGE__TYPE       = "image:type";
    /** The number of pixels wide. */
    public static final String IMAGE__WIDTH      = "image:width";
    /** The number of pixels high. */
    public static final String IMAGE__HEIGHT     = "image:height";

    /** Video URL. */
    public static final String VIDEO__URL        = "video:url";
    /** An alternate url to use if the webpage requires <b>HTTPS</b>. */
    public static final String VIDEO__SECURE_URL = "video:secure_url";
    /** A <i>MIME type</i> for this video. */
    public static final String VIDEO__TYPE       = "video:type";
    /** The number of pixels wide. */
    public static final String VIDEO__WIDTH      = "video:width";
    /** The number of pixels height. */
    public static final String VIDEO__HEIGHT     = "video:height";

    /** An alternate url to use if the webpage requires <b>HTTPS</b>. */
    public static final String AUDIO__SECURE_URL = "audio:secure_url";
    /** A <i>MIME type</i> for this audio. */
    public static final String AUDIO__TYPE       = "audio:type";

    /* END: Structured Properties. */

    private static OGP instance;

    public static OGP getInstance() {
        if(instance == null) {
            instance = new OGP();
        }
        return instance;
    }

    public final IRI NAMESPACE = createIRI(NS);
    

    public final IRI title           = createProperty(TITLE);
    public final IRI type            = createProperty(TYPE);
    public final IRI url             = createProperty(URL);
    public final IRI image           = createProperty(IMAGE);
    public final IRI description     = createProperty(DESCRIPTION);
    public final IRI determiner      = createProperty(DETERMINER);
    public final IRI locale          = createProperty(LOCALE);
    public final IRI localeAlternate = createProperty(LOCALE__ALTERNATE);
    public final IRI siteName        = createProperty(SITE_NAME);
    public final IRI video           = createProperty(VIDEO);

    public final IRI imageURL       = createProperty(IMAGE__URL);
    public final IRI imageSecureURL = createProperty(IMAGE__SECURE_URL);
    public final IRI imageType      = createProperty(IMAGE__TYPE);
    public final IRI imageWidth     = createProperty(IMAGE__WIDTH);
    public final IRI imageHeight    = createProperty(IMAGE__HEIGHT);

    public final IRI videoURL       = createProperty(VIDEO__URL);
    public final IRI videoSecureURL = createProperty(VIDEO__SECURE_URL);
    public final IRI videoType      = createProperty(VIDEO__TYPE);
    public final IRI videoWidth     = createProperty(VIDEO__WIDTH);
    public final IRI videoHeight    = createProperty(VIDEO__HEIGHT);

    public final IRI audio          = createProperty(AUDIO);
    public final IRI audioSecureURL = createProperty(AUDIO__SECURE_URL);
    public final IRI audioType      = createProperty(AUDIO__TYPE);

    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    private OGP() {
        super(NS);
    }

}
