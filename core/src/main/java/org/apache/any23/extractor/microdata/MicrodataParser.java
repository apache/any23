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
package org.apache.any23.extractor.microdata;

import org.apache.any23.extractor.html.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides utility methods for handling <b>Microdata</b>
 * nodes contained within a <i>DOM</i> document.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParser {

    enum ErrorMode {
        /** This mode raises an exception at first encountered error. */
        StopAtFirstError,
        /**  This mode produces a full error report. */
        FullReport
    }

    public static final String ITEMSCOPE_ATTRIBUTE = "itemscope";
    public static final String ITEMPROP_ATTRIBUTE  = "itemprop";

    /**
     * List of tags providing the <code>src</code> property.
     */
    public static final Set<String> SRC_TAGS =  Collections.unmodifiableSet(
            new HashSet<String>( Arrays.asList("audio", "embed", "frame", "iframe", "img", 
              "source", "track", "video", "input", "layer", "script", "textarea") )
    );

    /**
     * List of tags providing the <code>href</code> property.
     */
    public static final Set<String> HREF_TAGS =  Collections.unmodifiableSet(
            new HashSet<String>( Arrays.asList("a", "area", "link") )
    );

    private final Document document;

    /**
     * This set holds the name of properties being dereferenced.
     * The {@link #deferProperties(String...)} checks first if the
     * required dereference has been already asked, if so raises
     * a loop detection error. This map works in coordination
     * with {@link #dereferenceRecursionCounter}, so that at the end of
     * {@link #deferProperties(String...)} call recursion the
     * {@link #loopDetectorSet} can be cleaned up.
     */
    private final Set<String> loopDetectorSet = new HashSet<String>();

    /**
     * {@link ItemScope} cache.
     */
    private final Map<Node,ItemScope> itemScopes = new HashMap<Node,ItemScope>();

    /**
     * {@link ItemPropValue} cache.
     */
    private final Map<Node, ItemPropValue> itemPropValues = new HashMap<Node, ItemPropValue>();

   /**
     * Counts the recursive call of {@link #deferProperties(String...)}.
     * It helps to cleanup the {@link #loopDetectorSet} when recursion ends.
     */
    private int dereferenceRecursionCounter = 0;

    /**
     * Current error mode.
     */
    private ErrorMode errorMode = ErrorMode.FullReport;

    /**
     * List of collected errors. Used when {@link #errorMode} <code>==</code> {@link ErrorMode#FullReport}.
     */
    private List<MicrodataParserException> errors = new ArrayList<MicrodataParserException>();

    /**
     * Returns all the <i>itemScope</i>s detected within the given root node.
     *
     * @param node root node to search in.
     * @return list of detected items.
     */
    public static List<Node> getItemScopeNodes(Node node) {
        return DomUtils.findAllByAttributeName(node, ITEMSCOPE_ATTRIBUTE);
    }

    /**
     * Check whether a node is an <i>itemScope</i>.
     *
     * @param node node to check.
     * @return <code>true</code> if the node is an <i>itemScope</i>., <code>false</code> otherwise.
     */
    public static boolean isItemScope(Node node) {
        return DomUtils.readAttribute(node, ITEMSCOPE_ATTRIBUTE, null) != null;
    }

    /**
     * Returns all the <i>itemProp</i>s detected within the given root node.
     *
     * @param node root node to search in.
     * @return list of detected items.
     */
    public static List<Node> getItemPropNodes(Node node) {
        return DomUtils.findAllByAttributeName(node, ITEMPROP_ATTRIBUTE);
    }

    /**
     * Check whether a node is an <i>itemProp</i>.
     *
     * @param node node to check.
     * @return <code>true</code> if the node is an <i>itemProp</i>., <code>false</code> otherwise.
     */
    public static boolean isItemProp(Node node) {
        return DomUtils.readAttribute(node, ITEMPROP_ATTRIBUTE, null) != null;
    }

    /**
     * Returns only the <i>itemScope</i>s that are top level items.
     *
     * @param node root node to search in.
     * @return list of detected top item scopes.
     */
    public static List<Node> getTopLevelItemScopeNodes(Node node)  {
        final List<Node> itemScopes = getItemScopeNodes(node);
        final List<Node> topLevelItemScopes = new ArrayList<Node>();
        for(Node itemScope : itemScopes) {
            if( ! isItemProp(itemScope) ) {
                topLevelItemScopes.add(itemScope);
            }
        }
        // ANY23-131 Nested Microdata are not extracted
        //return getUnnestedNodes( topLevelItemScopes );
        return topLevelItemScopes;
    }

    /**
     * Returns all the <b>Microdata items</b> detected within the given <code>document</code>.
     *
     * @param document document to be processed.
     * @param errorMode error management policy.
     * @return list of <b>itemscope</b> items.
     * @throws MicrodataParserException if
     *         <code>errorMode == {@link org.apache.any23.extractor.microdata.MicrodataParser.ErrorMode#StopAtFirstError}</code>
     *         and an error occurs.
     */
    public static MicrodataParserReport getMicrodata(Document document, ErrorMode errorMode)
    throws MicrodataParserException {
        final List<Node> itemNodes = getTopLevelItemScopeNodes(document);
        final List<ItemScope> items = new ArrayList<ItemScope>();
        final MicrodataParser microdataParser = new MicrodataParser(document);
        microdataParser.setErrorMode(errorMode);
        for(Node itemNode : itemNodes) {
            items.add( microdataParser.getItemScope(itemNode) );
        }
        return new MicrodataParserReport(
                items.toArray( new ItemScope[items.size()] ),
                microdataParser.getErrors()
        );
    }

    /**
     * Returns all the <b>Microdata items</b> detected within the given <code>document</code>,
     * works in full report mode.
     *
     * @param document document to be processed.
     * @return list of <b>itemscope</b> items.
     */
    public static MicrodataParserReport getMicrodata(Document document) {
        try {
            return getMicrodata(document, ErrorMode.FullReport);
        } catch (MicrodataParserException mpe) {
             throw new IllegalStateException("Unexpected exception.", mpe);
        }
    }

    /**
     * Returns a <i>JSON</i> containing the list of all extracted Microdata,
     * as described at <a href="http://www.w3.org/TR/microdata/#json">Microdata JSON Specification</a>.
     *
     * @param document document to be processed.
     * @param ps the {@link java.io.PrintStream} to write JSON to
     */
    public static void getMicrodataAsJSON(Document document, PrintStream ps) {
        final MicrodataParserReport report = getMicrodata(document);
        final ItemScope[] itemScopes = report.getDetectedItemScopes();
        final MicrodataParserException[] errors = report.getErrors();

        ps.append("{ ");

        // Results.
        ps.append("\"result\" : [");
        for(int i = 0; i < itemScopes.length; i++) {
            if (i > 0) {
                ps.print(", ");
            }
            ps.print( itemScopes[i].toJSON() );
        }
        ps.append("] ");

        // Errors.
        if(errors != null && errors.length > 0) {
            ps.append(", ");
            ps.append("\"errors\" : [");
            for (int i = 0; i < errors.length; i++) {
                if (i > 0) {
                    ps.print(", ");
                }
                ps.print( errors[i].toJSON() );
            }
            ps.append("] ");
        }

        ps.append("}");
    }

    /**
     * Returns only nodes that are <b>not</b> nested one each other.
     *
     * @param candidates list of candidate nodes.
     * @return list of unnested nodes.
     */
    private static List<Node> getUnnestedNodes(List<Node> candidates) {
        final List<Node> unnesteds  = new ArrayList<Node>();
        for(int i = 0; i < candidates.size(); i++) {
            boolean skip = false;
            for(int j = 0; j < candidates.size(); j++) {
                if(i == j) continue;
                if( DomUtils.isAncestorOf(candidates.get(j), candidates.get(i), true) ) {
                    skip = true;
                    break;
                }
            }
            if(!skip) {
                unnesteds.add( candidates.get(i) );
            }
        }
        return unnesteds;
    }

    public MicrodataParser(Document document) {
        if(document == null) {
            throw new NullPointerException("Document cannot be null.");
        }
        this.document = document;
    }

    public void setErrorMode(ErrorMode errorMode) {
        if(errorMode == null) throw new IllegalArgumentException("errorMode must be not null.");
        this.errorMode = errorMode;
    }

    public ErrorMode getErrorMode() {
        return this.errorMode;
    }

    public MicrodataParserException[] getErrors() {
        return errors == null
                ?
                new MicrodataParserException[0]
                :
                errors.toArray( new MicrodataParserException[errors.size()] );
    }

    /**
     * Reads the value of a <b>itemprop</b> node.
     *
     * @param node itemprop node.
     * @return value detected within the given <code>node</code>.
     * @throws MicrodataParserException if an error occurs while extracting a nested item scope.
     */
    public ItemPropValue getPropertyValue(Node node) throws MicrodataParserException {
        final ItemPropValue itemPropValue = itemPropValues.get(node);
        if(itemPropValue != null) return itemPropValue;

        final String nodeName = node.getNodeName().toLowerCase();
        if (DomUtils.hasAttribute(node, "content")) {
            return new ItemPropValue(DomUtils.readAttribute(node, "content"), ItemPropValue.Type.Plain);
        }

        if( SRC_TAGS.contains(nodeName) ) {
            return new ItemPropValue( DomUtils.readAttribute(node, "src"), ItemPropValue.Type.Link);
        }
        if( HREF_TAGS.contains(nodeName) ) {
            return new ItemPropValue( DomUtils.readAttribute(node, "href"), ItemPropValue.Type.Link);
        }

        if( "object".equals(nodeName) ) {
            return new ItemPropValue( DomUtils.readAttribute(node, "data"), ItemPropValue.Type.Link);
        }
        if( "time".equals(nodeName) ) {
            final String dateTimeStr = DomUtils.readAttribute(node, "datetime");
            final Date dateTime;
            try {
                dateTime = ItemPropValue.parseDateTime(dateTimeStr);
            } catch (ParseException pe) {
                throw new MicrodataParserException(
                        String.format("Invalid format for datetime '%s'", dateTimeStr),
                        node
                );
            }
            return new ItemPropValue(dateTime, ItemPropValue.Type.Date);
        }

        if( isItemScope(node) ) {
            return new ItemPropValue( getItemScope(node), ItemPropValue.Type.Nested );
        }

        final ItemPropValue newItemPropValue = new ItemPropValue( node.getTextContent(), ItemPropValue.Type.Plain);
        itemPropValues.put(node, newItemPropValue);
        return newItemPropValue;
    }

    /**
     * Returns all the <b>itemprop</b>s for the given <b>itemscope</b> node.
     *
     * @param scopeNode node representing the <b>itemscope</b>
     * @param skipRoot if <code>true</code> the given root <code>node</code>
     *        will be not read as a property, even if it contains the <b>itemprop</b> attribute.
     * @return the list of <b>itemprop</b>s detected within the given <b>itemscope</b>.
     * @throws MicrodataParserException if an error occurs while retrieving an property value.
     */
    public List<ItemProp> getItemProps(final Node scopeNode, boolean skipRoot) throws MicrodataParserException {
        final Set<Node> accepted = new LinkedHashSet<Node>();

        if (!skipRoot) {
            NamedNodeMap attributes = scopeNode.getAttributes();
            if (attributes.getNamedItem(ITEMPROP_ATTRIBUTE) != null) {
                accepted.add(scopeNode);
            }
        }

        // TreeWalker to walk DOM tree starting with the scopeNode. Nodes maybe visited multiple times.
        TreeWalker treeWalker = ((DocumentTraversal) scopeNode.getOwnerDocument())
            .createTreeWalker(scopeNode, NodeFilter.SHOW_ELEMENT, new NodeFilter() {
            @Override
            public short acceptNode(Node node) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap attributes = node.getAttributes();
                    if (attributes.getNamedItem(ITEMPROP_ATTRIBUTE) != null && !scopeNode.equals(node)) {
                        accepted.add(node);
                    }
                    if (attributes.getNamedItem(ITEMSCOPE_ATTRIBUTE) != null) {
                        // Don't visit descendants of nodes that define a new scope
                        return FILTER_REJECT;
                    }
                }
                return FILTER_ACCEPT;
            }
        }, false);

        // To populate accepted we only need to walk the tree.
    	while (treeWalker.nextNode() != null);

        final List<ItemProp> result = new ArrayList<ItemProp>();
        for(Node itemPropNode :  accepted) {
            final String itemProp = DomUtils.readAttribute(itemPropNode, ITEMPROP_ATTRIBUTE, null);
            final String[] propertyNames = itemProp.split(" ");
            ItemPropValue itemPropValue;
            for (String propertyName : propertyNames) {
                try {
                    itemPropValue = getPropertyValue(itemPropNode);
                } catch (MicrodataParserException mpe) {
                    manageError(mpe);
                    continue;
                }
                result.add(
                        new ItemProp(
                                DomUtils.getXPathForNode(itemPropNode),
                                propertyName,
                                itemPropValue
                        )
                );
            }
        }
        return result;
    }

    /**
     * Given a document and a list of <b>itemprop</b> names this method will return
     * such <b>itemprops</b>.
     *
     * @param refs list of references.
     * @return list of retrieved <b>itemprop</b>s.
     * @throws MicrodataParserException if a loop is detected or a property name is missing.
     */
    public ItemProp[] deferProperties(String... refs) throws MicrodataParserException {
        dereferenceRecursionCounter++;
        final List<ItemProp> result = new ArrayList<ItemProp>();
        try {
            for (String ref : refs) {
                if (loopDetectorSet.contains(ref)) {
                        throw new MicrodataParserException(
                                String.format(
                                        "Loop detected with depth %d while dereferencing itemProp '%s' .",
                                        dereferenceRecursionCounter - 1, ref
                                ),
                                null
                        );
                }
                loopDetectorSet.add(ref);
                final Element element = document.getElementById(ref);
                if (element == null) {
                    manageError(
                            new MicrodataParserException( String.format("Unknown itemProp id '%s'", ref ), null )
                    );
                    continue;
                }
                result.addAll(getItemProps(element, false));
            }
        } catch (MicrodataParserException mpe) {
            if(dereferenceRecursionCounter == 1)
                manageError(mpe); else throw mpe;  // Recursion end, this the the top call.
        } finally {
            dereferenceRecursionCounter--;
            if(dereferenceRecursionCounter == 0) { // Recursion end, this the the top call.
                loopDetectorSet.clear();
            }
        }
        return result.toArray( new ItemProp[result.size()] );
    }

    /**
     * Returns the {@link ItemScope} instance described within the specified <code>node</code>.
     *
     * @param node node describing an <i>itemscope</i>.
     * @return instance of ItemScope object.
     * @throws MicrodataParserException if an error occurs while dereferencing properties.
     */
    public ItemScope getItemScope(Node node) throws MicrodataParserException {
        final ItemScope itemScope = itemScopes.get(node);
        if(itemScope != null) return itemScope;

        final String id       = DomUtils.readAttribute(node, "id"      , null);
        final String itemref  = DomUtils.readAttribute(node, "itemref" , null);
        final String itemType = DomUtils.readAttribute(node, "itemtype", null);
        final String itemId   = DomUtils.readAttribute(node, "itemid"  , null);

        final List<ItemProp> itemProps = getItemProps(node, true);
        final String[] itemrefIDs = itemref == null ? new String[0] : itemref.split(" ");
        final ItemProp[] deferredProperties;
        try {
            deferredProperties = deferProperties(itemrefIDs);
        } catch (MicrodataParserException mpe) {
            mpe.setErrorNode(node);
            throw mpe;
        }
        for(ItemProp deferredProperty : deferredProperties) {
            if( itemProps.contains(deferredProperty) ) {
                manageError(
                        new MicrodataParserException(
                            String.format("Duplicated deferred itemProp '%s'.", deferredProperty.getName() ),
                            node
                        )
                );
                continue;
            }
            itemProps.add(deferredProperty);
        }

        final ItemScope newItemScope = new ItemScope(
                DomUtils.getXPathForNode(node),
                itemProps.toArray(new ItemProp[itemProps.size()]),
                id,
                itemrefIDs,
                itemType,
                itemId
        );
        itemScopes.put(node, newItemScope);
        return newItemScope;
    }

    private void manageError(MicrodataParserException mpe) throws MicrodataParserException {
        if(errorMode == ErrorMode.StopAtFirstError) {
            throw mpe;
        }
        if(errorMode != ErrorMode.FullReport) throw new IllegalStateException("Unsupported mode " + errorMode);
        if(errors == null) {
            errors = new ArrayList<MicrodataParserException>();
        }
        errors.add(mpe);
    }

}
