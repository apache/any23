package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * The <i>MS Excel</i> extractor vocabulary.
 *
 * @see org.deri.any23.plugin.officescraper.ExcelExtractor
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Excel extends Vocabulary {

    public static final String SHEET = "sheet";
    public static final String ROW   = "row";
    public static final String CELL  = "cell";

    public static final String CONTAINS_SHEET = "containsSheet";
    public static final String CONTAINS_ROW   = "containsRow";
    public static final String CONTAINS_CELL  = "containsCell";
    public static final String CELL_VALUE     = "cellValue";


    public static final String SHEET_NAME = "sheetName";
    public static final String FIRST_ROW  = "firstRow";
    public static final String LAST_ROW   = "lastRow";
    public static final String FIRST_CELL = "firstCell";
    public static final String LAST_CELL  = "lastCell";

    /**
     * This property links the identifier of a <i>document</i> to the identifier of a <i>sheet</i>.
     */
    public final URI containsSheet = createProperty(CONTAINS_SHEET);

    /**
     * This property links the identifier of a <i>sheet</i> to the identifier of a <i>row</i>.
     */
    public final URI containsRow = createProperty(CONTAINS_ROW);

    /**
     * This property links the identifier of a <i>row</i> to the identifier of a <i>cell</i>.
     */
    public final URI containsCell = createProperty(CONTAINS_CELL);

    /**
     * This property links the identifier of a <i>Sheet</i> to the name of the sheet.
     */
    public final URI sheetName = createProperty(SHEET_NAME);

    /**
     * This property links the identifier of a <i>Sheet</i> to the index of the first declared row.
     */
    public final URI firstRow = createProperty(FIRST_ROW);

    /**
     * This property links the identifier of a <i>Sheet</i> to the index of the last declared row.
     */
    public final URI lastRow = createProperty(LAST_ROW);

    /**
     * This property links the identifier of a <i>Row</i> to the index of the first declared cell.
     */
    public final URI firstCell = createProperty(FIRST_CELL);

    /**
     * This property links the identifier of a <i>Row</i> to the index of the last declared cell.
     */
    public final URI lastCell = createProperty(LAST_CELL);

    /**
     * This property links the identifier of a <i>cell</i> to the content of the cell.
     */
    public final URI cellValue = createProperty(CELL_VALUE);


    /**
     * This resource identifies a <i>Sheet</i>.
     */
    public final URI sheet = createResource(SHEET);

    /**
     * This resource identifies a <i>row</i>.
     */
    public final URI row = createResource(ROW);

    /**
     * This resource identifies a <i>cell</i>.
     */
    public final URI cell = createResource(CELL);

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://vocab.sindice.net/excel/";

    private static Excel instance;

    public static Excel getInstance() {
        if (instance == null) {
            instance = new Excel();
        }
        return instance;
    }

    public URI createResource(String localName) {
        return createProperty(NS, localName);
    }

    /**
     *
     * @param localName
     * @return
     */
    public URI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    private Excel() {
        super(NS);
    }


}
