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

package org.deri.any23.validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * This interface models a document to be processed
 * by the {@link org.deri.any23.validator.Validator}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface DOMDocument {

    /**
     * Returns the original document.
     *
     * @return the original document.
     */
    Document getOriginalDocument();

    List<Node> getNodes(String xPath);

    Node getNode(String xPath);

    void addAttribute(String xPath, String attrName, String attrValue);
}
