package org.apache.any23.extractor.html.microformats2;

import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.TagSoupExtractionResult;
import org.apache.any23.vocab.HItem;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;
import org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import org.apache.any23.extractor.html.HTMLDocument;

/**
 * Extractor for the <a href="http://microformats.org/wiki/h-item">h-item</a>
 * microformat.
 *
 * @author Nisala Nirmana
 */
public class HItemExtractor extends EntityBasedMicroformatExtractor {

    private static final HItem vHITEM = HItem.getInstance();

    private static final String[] itemFields = {
            "name",
            "url",
            "photo"
    };

    @Override
    public ExtractorDescription getDescription() {
        return HItemExtractorFactory.getDescriptionInstance();
    }

    protected String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX+"item";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException{
        if (null == node) return false;
        final HTMLDocument document = new HTMLDocument(node);
        BNode item = getBlankNodeFor(node);
        out.writeTriple(item, RDF.TYPE, vHITEM.Item);
        final String extractorName = getDescription().getExtractorName();
        addName(document,item);
        addPhotos(document,item);
        addUrls(document,item);
        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot(document.getPathToLocalRoot(), item, this.getClass());
        return true;
    }

    private void mapFieldWithProperty(HTMLDocument fragment, BNode item, String fieldClass, URI property) {
        HTMLDocument.TextField title = fragment.getSingularTextField(fieldClass);
        conditionallyAddStringProperty(
                title.source(),item, property, title.value()
        );
    }

    private void addName(HTMLDocument fragment, BNode item) {
        mapFieldWithProperty(fragment, item, Microformats2Prefixes.PROPERTY_PREFIX+itemFields[0], vHITEM.name);
    }

    private void addPhotos(HTMLDocument fragment, BNode item) throws ExtractionException {
        final HTMLDocument.TextField[] photos = fragment.getPluralUrlField
                (Microformats2Prefixes.URL_PROPERTY_PREFIX+itemFields[2]);
        for(HTMLDocument.TextField photo : photos) {
            addURIProperty(item, vHITEM.photo, fragment.resolveURI(photo.value()));
        }
    }

    private void addUrls(HTMLDocument fragment, BNode item) throws ExtractionException {
        HTMLDocument.TextField[] links = fragment.getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX+
                itemFields[1]);
        for (HTMLDocument.TextField link : links) {
            conditionallyAddResourceProperty(item, vHITEM.url, getHTMLDocument().resolveURI(link.value()));
        }
    }
}
