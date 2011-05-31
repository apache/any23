package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * Vocabulary to map the <a href="http://microformats.org/wiki/hrecipe">hRecipe</a> microformat.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRECIPE extends Vocabulary {

    public static final String NS = SINDICE.NS + "hrecipe/";

    // Classes.
    public static URI Recipe     = createURI(NS, "Recipe");
    public static URI Duration   = createURI(NS, "Duration");
    public static URI Ingredient = createURI(NS, "Ingredient");
    public static URI Nutrition  = createURI(NS, "Nutrition");

    // Properties.
    public static URI fn                     = createURI(NS, "fn");
    public static URI duration               = createURI(NS, "duration");
    public static URI durationTitle          = createURI(NS, "durationTitle");
    public static URI durationTime           = createURI(NS, "durationTime");
    public static URI photo                  = createURI(NS, "photo");
    public static URI summary                = createURI(NS, "summary");
    public static URI author                 = createURI(NS, "author");
    public static URI published              = createURI(NS, "published");
    public static URI nutrition              = createURI(NS, "nutrition");
    public static URI nutritionValue         = createURI(NS, "nutritionValue");
    public static URI nutritionValueType     = createURI(NS, "nutritionValueType");
    public static URI tag                    = createURI(NS, "tag");
    public static URI ingredient             = createURI(NS, "ingredient");
    public static URI ingredientName         = createURI(NS, "ingredientName");
    public static URI ingredientQuantity     = createURI(NS, "ingredientQuantity");
    public static URI ingredientQuantityType = createURI(NS, "ingredientQuantityType");
    public static URI instructions           = createURI(NS, "instructions");
    public static URI yield                  = createURI(NS, "yield");

}
