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
import org.deri.any23.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class provides utility methods for handling <b>Microdata</b>
 * nodes contained within a <i>DOM</i> document.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
// TODO: add error and warning management.
public class MicrodataUtils {

    public static final String ITEMSCOPE_ATTRIBUTE = "itemscope";
    public static final String ITEMPROP_ATTRIBUTE  = "itemprop";

    /**
     * List of tags providing the <code>src</code> property.
     */
    public static final Set<String> SRC_TAGS =  Collections.unmodifiableSet(
            new HashSet<String>( Arrays.asList("audio", "embed", "iframe", "img", "source", "track", "video") )
    );

    /**
     * List of tags providing the <code>href</code> property.
     */
    public static final Set<String> HREF_TAGS =  Collections.unmodifiableSet(
            new HashSet<String>( Arrays.asList("a", "area", "link") )
    );

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
     * Returns all the <i>itemProp</i>s detected within the given root node.
     *
     * @param node root node to search in.
     * @return list of detected items.
     */
    public static List<Node> getItemPropNodes(Node node) {
        return DomUtils.findAllByAttributeName(node, ITEMPROP_ATTRIBUTE);
    }

    /**
     * Returns only nodes that are not nested one each other.
     *
     * @param candidates list of candidate nodes.
     * @return list of unnested nodes.
     */
    public static List<Node> getUnnestedNodes(List<Node> candidates) {
        final List<Node> unnesteds  = new ArrayList<Node>();
        for(int i = 0; i < candidates.size(); i++) {
            boolean skip = false;
            for(int j = 0; j < candidates.size(); j++) {
                if(i == j) continue;
                if(
                        StringUtils.isPrefix(
                                DomUtils.getXPathForNode(candidates.get(j)),
                                DomUtils.getXPathForNode(candidates.get(i))
                        )
                ) {
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

    /**
     * Check whether a node is an item.
     *
     * @param node node to check.
     * @return <code>true</code> if the node is an item, <code>false</code> otherwise.
     */
    public static boolean isItemScope(Node node) {
        return DomUtils.readAttribute(node, ITEMSCOPE_ATTRIBUTE, null) != null;
    }

    /**
     * Reads the value of a <b>itemprop</code> node.
     *
     * @param document container document.
     * @param node itemprop node.
     * @return value detected within the given <code>node</code>.
     */
    public static ItemPropValue getPropertyValue(Document document, Node node) {
        final String nodeName = node.getNodeName().toLowerCase();
        if ("meta".equals(nodeName)) {
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
            return new ItemPropValue( DomUtils.readAttribute(node, "datetime"), ItemPropValue.Type.DateTime);
        }

        if( isItemScope(node) ) {
            return new ItemPropValue( getItemScope(document, node), ItemPropValue.Type.Nested );
        }

        return new ItemPropValue( node.getTextContent(), ItemPropValue.Type.Plain);
    }

    /**
     * Returns all the <b>itemprop</b>s for the given <b>itemscope</b> node.
     *
     * @param document container document.
     * @param node node representing the <b>itemscope</>
     * @param skipCurrent if <code>true</code> the current <code>node</node>
     *        will be not read as a property, even if it contains the <b>itemprop</b> attribute.
     * @return the list of <b>itemprop<b>s detected within the given <b>itemscope</b>.
     */
    public static List<ItemProp> getItemProps(Document document, Node node, boolean skipCurrent) {
        final List<Node> itemPropNodes = getItemPropNodes(node);

        // Skipping itemScopes nested to this item prop.
        final List<Node> subItemScopes = getItemScopeNodes(node);
        subItemScopes.remove(node);
        final List<Node> accepted = new ArrayList<Node>();
        String subItemScopeXpath;
        String subItemPropXPath;
        for(Node itemPropNode : itemPropNodes) {
            boolean skip = false;
            for(Node subItemScope : subItemScopes) {
                subItemScopeXpath = DomUtils.getXPathForNode(subItemScope);
                subItemPropXPath  = DomUtils.getXPathForNode(itemPropNode);
                if(
                    StringUtils.isPrefix(subItemScopeXpath, subItemPropXPath)
                            &&
                    // This prevent removal of itemprop that is also itemscope
                    subItemScopeXpath.length() < subItemPropXPath.length()
                ) {
                    skip = true;
                    break;
                }
            }
            if(!skip) accepted.add(itemPropNode);
        }

        final List<ItemProp> result = new ArrayList<ItemProp>();
        for(Node itemPropNode :  accepted) {
            if(itemPropNode.equals(node) && skipCurrent) {
                continue;
            }
            final String itemProp = DomUtils.readAttribute(itemPropNode, ITEMPROP_ATTRIBUTE, null);
            final String[] propertyNames = itemProp.split(" ");
            for (String propertyName : propertyNames) {
                result.add(
                        new ItemProp(
                                DomUtils.getXPathForNode(itemPropNode),
                                propertyName,
                                getPropertyValue(document, itemPropNode)
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
     * @param document container document.
     * @param refs list of references.
     * @return list of retrieved <b>itemprop</b>s.
     */
    public static ItemProp[] deferProperties(Document document, String[] refs) {
        final List<ItemProp> result = new ArrayList<ItemProp>();
        for(String ref : refs) {
            final Element element = document.getElementById(ref);
                result.addAll(getItemProps(document, element, false));
            }
        return result.toArray( new ItemProp[result.size()] );
    }

    /**
     * Returns the {@link ItemScope} instance described within the specified <code>node</code>.
     *
     * @param document container document.
     * @param node node describing an <i>itemscope</i>.
     * @return instance of ItemScope object.
     */
    public static ItemScope getItemScope(Document document, Node node) {
        final String id       = DomUtils.readAttribute(node, "id"      , null);
        final String itemref  = DomUtils.readAttribute(node, "itemref" , null);
        final String itemType = DomUtils.readAttribute(node, "itemtype", null);
        final String itemId   = DomUtils.readAttribute(node, "itemid"  , null);

        final List<ItemProp> itemProps = getItemProps(document, node, true);
        final String[] itemrefIDs = itemref == null ? new String[0] : itemref.split(" ");
        itemProps.addAll( Arrays.asList(deferProperties(document, itemrefIDs) ) );

        return new ItemScope(
                DomUtils.getXPathForNode(node),
                itemProps.toArray(new ItemProp[itemProps.size()]),
                id,
                itemrefIDs,
                itemType,
                itemId
        );
    }

    /**
     * Returns all the <b>Microdata items</b> detected within the given <code>document</code>.
     *
     * @param document document to be processed.
     * @return list of <b>itemscope</b> items.
     */
    public static ItemScope[] getMicrodata(Document document) {
        final List<Node> itemNodes = getUnnestedNodes(getItemScopeNodes(document));
        final List<ItemScope> items = new ArrayList<ItemScope>();
        for(Node itemNode : itemNodes) {
            items.add( getItemScope(document, itemNode) );
        }
        return items.toArray( new ItemScope[items.size()] );
    }

    /**
     * Returns a <i>JSON</i> containing the list of all extracted microdata.
     *
     * @param document
     * @param ps
     */
    public static void getMicrodataAsJSON(Document document, PrintStream ps) {
        final ItemScope[] itemScopes = getMicrodata(document);
        ps.append("{ \"result\" : [");
        for(int i = 0; i < itemScopes.length; i++) {
            ps.print( itemScopes[i].toJSON() );
            if( i < itemScopes.length - 1 ) {
                ps.print(", ");
            }
        }
        ps.append("] }");
    }

}
