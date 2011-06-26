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

package org.deri.any23.extractor.microdata;

import org.deri.any23.extractor.html.DomUtils;
import org.w3c.dom.Node;

/**
 * Defines an exception occurring while parsing
 * <i>Microdata</i>.
 *
 * @see MicrodataParser
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParserException extends Exception {

    private String location;

    public MicrodataParserException(String message, Node location) {
        super(message);
        setLocation(location);
    }

    public MicrodataParserException(String message, Throwable cause, Node location) {
        super(message, cause);
        setLocation(location);
    }

    public String getLocation() {
        return location;
    }

    public String toJSON() {
        return String.format(
                "{ \"message\" : \"%s\", \"location\" : \"%s\" }",
                getMessage().replaceAll("\"", ""),
                getLocation()
        );
    }

    protected void setLocation(Node location) {
        this.location = location == null ? null : DomUtils.getXPathForNode(location);
    }

}
