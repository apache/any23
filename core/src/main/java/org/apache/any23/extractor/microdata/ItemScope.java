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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class describes a <b>Microdata <i>itemscope</i></b>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ItemScope extends Item {

    /**
     * Map of properties and multi values.
     */
    private final Map<String, List<ItemProp>> properties;

    /**
     * <i>itemscope</i> DOM identifier in container document.
     */
    private final String id;

    /**
     * <i>itemscope</i> references.
     */
    private final String[] refs;

    /**
     * <i>itemscope</i> type.
     */
    private final URL type;

    /**
     * <i>itemscope</i> external identifier.
     */
    private final String itemId;

    /**
     * Constructor.
     *
     * @param xpath     location of this <i>itemscope</i> within the container document.
     * @param itemProps list of properties bound to this <i>itemscope</i>.
     * @param id        DOM identifier for this <i>itemscope</i>. Can be <code>null</code>.
     * @param refs      list of item prop references connected to this <i>itemscope</i>. Can be <code>null</code>.
     * @param type      <i>itemscope</i> type. Can be <code>null</code>.
     * @param itemId    <i>itemscope</i> id. Can be <code>null</code>.
     */
    public ItemScope(String xpath, ItemProp[] itemProps, String id, String[] refs, String type, String itemId) {
        super(xpath);

        if (itemProps == null) {
            throw new NullPointerException("itemProps list cannot be null.");
        }
        if (type != null) {
            try {
                this.type = new URL(type);
            } catch (MalformedURLException murle) {
                throw new IllegalArgumentException("Invalid type '" + type + "', must be a valid URL.");
            }
        } else {
            this.type = null;
        }
        this.id = id;
        this.refs = refs;
        this.itemId = itemId;

        final Map<String, List<ItemProp>> tmpProperties = new HashMap<>();
        for (ItemProp itemProp : itemProps) {
            final String propName = itemProp.getName();
            List<ItemProp> propList = tmpProperties.get(propName);
            if (propList == null) {
                propList = new ArrayList<>();
                tmpProperties.put(propName, propList);
            }
            propList.add(itemProp);
        }
        final Map<String, List<ItemProp>> properties = new HashMap<>();
        for (Map.Entry<String, List<ItemProp>> propertiesEntry : tmpProperties.entrySet()) {
            properties.put(
                    propertiesEntry.getKey(),
                    //Collections.unmodifiableList( propertiesEntry.getValue() )
                    propertiesEntry.getValue()
            );
        }
        // this.properties = Collections.unmodifiableMap(properties);
        this.properties = properties;
    }

    /**
     * @return map of declared properties, every property can have more than a value.
     */
    public Map<String, List<ItemProp>> getProperties() {
        return properties;
    }

    /**
     * @return the <i>itemscope</i>
     */
    public String getId() {
        return id;
    }

    /**
     * @return <i>itemscope</i> list of references to <i>itemprop</i>s.
     */
    public String[] getRefs() {
        return refs;
    }

    /**
     * @return <i>itemscope</i> type.
     */
    public URL getType() {
        return type;
    }

    /**
     * @return <i>itemscope</i> public identifier.
     */
    public String getItemId() {
        return itemId;
    }

    @Override
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        int i;
        int j;
        final Collection<List<ItemProp>> itemPropsList = properties.values();
        j = 0;
        for (List<ItemProp> itemProps : itemPropsList) {
            i = 0;
            for (ItemProp itemProp : itemProps) {
                sb.append(itemProp);
                if (i < itemProps.size() - 1) {
                    sb.append(", ");
                }
                i++;
            }
            if (j < itemPropsList.size() - 1) {
                sb.append(", ");
            }
            j++;
        }
        return String.format(
                "{ " +
                        "\"xpath\" : \"%s\", \"id\" : %s, \"refs\" : %s, \"type\" : %s, \"itemid\" : %s, \"properties\" : [ %s ]" +
                        " }",
                getXpath(),
                id == null ? null : "\"" + id + "\"",
                refs == null ? null : toJSON(refs),
                type == null ? null : "\"" + type + "\"",
                itemId == null ? null : "\"" + itemId + "\"",
                sb.toString()
        );
    }

    @Override
    public String toString() {
        return toJSON();
    }

    @Override
    public int hashCode() {
        int i = properties == null ? 0 : properties.hashCode();
        i += id == null         ? 0 : id.hashCode();
        i += refs == null       ? 0 : Arrays.hashCode(refs);
        i += type == null       ? 0 : type.hashCode();
        i += itemId == null     ? 0 : itemId.hashCode();
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof ItemScope) {
            final ItemScope other = (ItemScope) obj;
                return
                        super.getXpath().equals(other.getXpath())
                            &&
                        (properties == null ? other.properties == null : properties.equals(other.properties))
                            &&
                        (id == null ? other.id == null : id.equals(other.id))
                            &&
                        (refs == null ? other.refs == null : Arrays.equals(refs, other.refs))
                            &&
                        (type == null ? other.type == null : type.equals(other.type))
                            &&
                        (itemId == null ? other.itemId == null : itemId.equals(other.itemId));
        }
        return false;
    }

    protected void acquireProperty(ItemProp itemProp) {
        List<ItemProp> itemProps = properties.get(itemProp.getName());
        if (itemProps == null) {
            itemProps = new ArrayList<>();
            properties.put(itemProp.getName(), itemProps);
        }
        if (!itemProps.contains(itemProp))
            itemProps.add(itemProp);
    }

    protected void disownProperty(ItemProp itemProp) {
        List<ItemProp> propList = properties.get(itemProp.getName());
        if (propList != null)
            propList.remove(itemProp);
    }

    private String toJSON(String[] in) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < in.length; i++) {
            sb.append("\"");
            sb.append(in[i]);
            sb.append("\"");
            if (i < in.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }

}
