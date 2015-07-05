package org.apache.any23.vocab;

import org.openrdf.model.URI;

/**
 * Vocabulary to map the <a href="http://microformats.org/wiki/hitem">h-item</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HItem extends Vocabulary {

    public static final String NS = SINDICE.NS + "hitem/";

    private static HItem instance;

    public static HItem getInstance() {
        if(instance == null) {
            instance = new HItem();
        }
        return instance;
    }

    public URI Item  = createClass(NS, "Item");
    public URI name  = createProperty(NS, "name");
    public URI url   = createProperty(NS, "url");
    public URI photo = createProperty(NS, "photo");
    private HItem() {
        super(NS);
    }
}
