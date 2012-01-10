package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * Vocabulary to map the <a href="http://microformats.org/wiki/hrecipe">hRecipe</a> microformat.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRECIPE extends Vocabulary {

    public static final String NS = SINDICE.NS + "hrecipe/";

    private static HRECIPE instance;

    public static HRECIPE getInstance() {
        if(instance == null) {
            instance = new HRECIPE();
        }
        return instance;
    }

    // Resources.
    public URI Recipe     = createClass(NS, "Recipe");
    public URI Duration   = createClass(NS, "Duration");
    public URI Ingredient = createClass(NS, "Ingredient");
    public URI Nutrition  = createClass(NS, "Nutrition");

    // Properties.
    public URI fn                     = createProperty(NS, "fn");
    public URI duration               = createProperty(NS, "duration");
    public URI durationTitle          = createProperty(NS, "durationTitle");
    public URI durationTime           = createProperty(NS, "durationTime");
    public URI photo                  = createProperty(NS, "photo");
    public URI summary                = createProperty(NS, "summary");
    public URI author                 = createProperty(NS, "author");
    public URI published              = createProperty(NS, "published");
    public URI nutrition              = createProperty(NS, "nutrition");
    public URI nutritionValue         = createProperty(NS, "nutritionValue");
    public URI nutritionValueType     = createProperty(NS, "nutritionValueType");
    public URI tag                    = createProperty(NS, "tag");
    public URI ingredient             = createProperty(NS, "ingredient");
    public URI ingredientName         = createProperty(NS, "ingredientName");
    public URI ingredientQuantity     = createProperty(NS, "ingredientQuantity");
    public URI ingredientQuantityType = createProperty(NS, "ingredientQuantityType");
    public URI instructions           = createProperty(NS, "instructions");
    public URI yield                  = createProperty(NS, "yield");

    private HRECIPE() {
        super(NS);
    }
}
