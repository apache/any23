package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.HRECIPE;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.Arrays;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hrecipe">hRecipe</a>
 * microformat.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRecipeExtractor extends EntityBasedMicroformatExtractor {

    public final static ExtractorFactory<HRecipeExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-hrecipe",
                    PopularPrefixes.createSubset("rdf", "hrecipe"),
                    Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                    null,
                    HRecipeExtractor.class
            );


    @Override
    public ExtractorDescription getDescription() {
        return factory;
    }

    @Override
    protected String getBaseClassName() {
        return "hrecipe";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        final BNode recipe = getBlankNodeFor(node);
        conditionallyAddResourceProperty(recipe, RDF.TYPE, HRECIPE.Recipe);
        final HTMLDocument fragment = new HTMLDocument(node);
        addFN(fragment, recipe);
        addIngredients(fragment, recipe);
        addYield(fragment, recipe);
        addInstructions(fragment, recipe);
        addDurations(fragment, recipe);
        addPhoto(fragment, recipe);
        addSummary(fragment, recipe);
        addAuthors(fragment, recipe);
        addPublished(fragment, recipe);
        addNutritions(fragment, recipe);
        addTags(fragment, recipe);
        return true;
    }

    /**
     * Maps a field text with a property.
     *
     * @param fragment
     * @param recipe
     * @param fieldClass
     * @param property
     */
    private void mapFieldWithProperty(HTMLDocument fragment, BNode recipe, String fieldClass, URI property) {
        HTMLDocument.TextField title = fragment.getSingularTextField(fieldClass);
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                title.source(), recipe, property, title.value()
        );
    }

    /**
     * Adds the <code>fn</code> triple.
     *
     * @param fragment
     * @param recipe
     */
    private void addFN(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "fn", HRECIPE.fn);
    }

    /**
     * Adds the <code>ingredient</code> triples.
     *
     * @param fragment
     * @param ingredient
     * @return
     */
    private BNode addIngredient(HTMLDocument fragment,  HTMLDocument.TextField ingredient) {
        final BNode ingredientBnode = getBlankNodeFor(ingredient.source());
        addURIProperty(ingredientBnode, RDF.TYPE, HRECIPE.Ingredient);
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                ingredient.source(),
                ingredientBnode,
                HRECIPE.ingredientName,
                HTMLDocument.readNodeContent(ingredient.source(), true)
        );
        mapFieldWithProperty(fragment, ingredientBnode, "value", HRECIPE.ingredientQuantity);
        mapFieldWithProperty(fragment, ingredientBnode, "type" , HRECIPE.ingredientQuantityType);
        return ingredientBnode;
    }

    /**
     * Adds the <code>ingredients</code>list triples.
     *
     * @param fragment
     * @param recipe
     * @return
     */
    private void addIngredients(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] ingredients = fragment.getPluralTextField("ingredient");
        for(HTMLDocument.TextField ingredient : ingredients) {
            addBNodeProperty(recipe, HRECIPE.ingredient, addIngredient(fragment, ingredient));
        }
    }

    /**
     * Adds the <code>instruction</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addInstructions(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "instructions", HRECIPE.instructions);

    }

    /**
     * Adds the <code>yield</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addYield(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "yield", HRECIPE.yield);
    }

    /**
     * Adds the <code>duration</code> triples.
     *
     * @param fragment
     * @param duration
     * @return
     */
    //TODO: USE http://microformats.org/wiki/value-class-pattern to read correct date format.
    private BNode addDuration(HTMLDocument fragment, HTMLDocument.TextField duration) {
        final BNode durationBnode = getBlankNodeFor(duration.source());
        addURIProperty(durationBnode, RDF.TYPE, HRECIPE.Duration);
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                duration.source(),
                durationBnode, HRECIPE.durationTime, duration.value()
        );
        mapFieldWithProperty(fragment, durationBnode, "value-title", HRECIPE.durationTitle);
        return durationBnode;
    }

    /**
     * Adds the <code>yield</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addDurations(HTMLDocument fragment, BNode recipe) {
      final HTMLDocument.TextField[] durations = fragment.getPluralTextField("duration");
        for(HTMLDocument.TextField duration : durations) {
            addBNodeProperty(recipe, HRECIPE.duration, addDuration(fragment, duration));
        }
    }

    /**
     * Adds the <code>photo</code> triples.
     *
     * @param fragment
     * @param recipe
     * @throws ExtractionException
     */
    private void addPhoto(HTMLDocument fragment, BNode recipe) throws ExtractionException {
        final HTMLDocument.TextField[] photos = fragment.getPluralUrlField("photo");
        for(HTMLDocument.TextField photo : photos) {
            addURIProperty(recipe, HRECIPE.photo, fragment.resolveURI(photo.value()));
        }
    }

    /**
     * Adds the <code>summary</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addSummary(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "summary", HRECIPE.summary);
    }

    /**
     * Adds the <code>authors</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addAuthors(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] authors = fragment.getPluralTextField("author");
         for(HTMLDocument.TextField author : authors) {
             conditionallyAddStringProperty(
                    getDescription().getExtractorName(),
                    author.source(),
                    recipe, HRECIPE.author, author.value()
              );
        }
    }

    /**
     * Adds the <code>published</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    //TODO: USE http://microformats.org/wiki/value-class-pattern to read correct date format.
    private void addPublished(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "published", HRECIPE.published);
    }

    /**
     * Adds the <code>nutrition</code> triples.
     *
     * @param fragment
     * @param nutrition
     * @return
     */
    private BNode addNutrition(HTMLDocument fragment, HTMLDocument.TextField nutrition) {
        final BNode nutritionBnode = getBlankNodeFor(nutrition.source());
        addURIProperty(nutritionBnode, RDF.TYPE, HRECIPE.Nutrition);
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                nutrition.source(),
                nutritionBnode, HRECIPE.nutritionValue, nutrition.value()
        );
        mapFieldWithProperty(fragment, nutritionBnode, "value", HRECIPE.nutritionValue);
        mapFieldWithProperty(fragment, nutritionBnode, "type", HRECIPE.nutritionValueType);
        return nutritionBnode;
    }

    /**
     * Adds the <code>nutritions</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addNutritions(HTMLDocument fragment, BNode recipe) {
        HTMLDocument.TextField[] nutritions = fragment.getPluralTextField("nutrition");
        for (HTMLDocument.TextField nutrition : nutritions) {
            addBNodeProperty(recipe, HRECIPE.nutrition, addNutrition(fragment, nutrition));
        }
    }

    /**
     * Adds the <code>tags</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addTags(HTMLDocument fragment, BNode recipe) {
        HTMLDocument.TextField[] tags = fragment.extractRelTagNodes();
        for(HTMLDocument.TextField tag : tags) {
            conditionallyAddStringProperty(
                    getDescription().getExtractorName(),
                    tag.source(),
                    recipe, HRECIPE.tag, tag.value()
              );
        }
    }

}
