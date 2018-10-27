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

package org.apache.any23.plugin.officescraper;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.Excel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link org.apache.any23.extractor.Extractor.ContentExtractor} able to process
 * a <i>MS Excel 97-2007+</i> file format <i>.xls/.xlsx</i> and
 * convert the detected content to triples.
 * This extractor is based on
 * <a href="http://poi.apache.org/spreadsheet/index.html">Apache POI-HSSF and POI-XSSF Java API</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExcelExtractor implements Extractor.ContentExtractor {

    private static final Excel excel = Excel.getInstance();

    private boolean stopAtFirstError = false;

    public ExcelExtractor() {}

    public boolean isStopAtFirstError() {
        return stopAtFirstError;
    }

    @Override
    public void setStopAtFirstError(boolean f) {
        stopAtFirstError = f;
    }

    @Override
    public ExtractorDescription getDescription() {
        return ExcelExtractorFactory.getDescriptionInstance();
    }

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext context,
            InputStream in,
            ExtractionResult er
    ) throws IOException, ExtractionException {
        try {
            final IRI documentIRI = context.getDocumentIRI();
            final Workbook workbook = createWorkbook(documentIRI, in);
            processWorkbook(documentIRI, workbook, er);
        } catch (Exception e) {
            throw new ExtractionException("An error occurred while extracting MS Excel content.", e);
        }
    }

    // TODO: this should be done by Tika, the extractors should be split.
    private Workbook createWorkbook(IRI document, InputStream is) throws IOException {
        final String documentIRI = document.toString();
        if (documentIRI.endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        } else if (documentIRI.endsWith("xls")) {
            return new HSSFWorkbook(is);
        } else {
            throw new IllegalArgumentException("Unsupported extension for resource [" + documentIRI + "]");
        }
    }

    private void processWorkbook(IRI documentIRI, Workbook wb, ExtractionResult er) {
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            final Sheet sheet = wb.getSheetAt(sheetIndex);
            final IRI sheetIRI = getSheetIRI(documentIRI, sheet);
            er.writeTriple(documentIRI, excel.containsSheet, sheetIRI);
            er.writeTriple(sheetIRI, RDF.TYPE, excel.sheet);
            writeSheetMetadata(sheetIRI, sheet, er);
            for (Row row : sheet) {
                final IRI rowIRI = getRowIRI(sheetIRI, row);
                er.writeTriple(sheetIRI, excel.containsRow, rowIRI);
                er.writeTriple(rowIRI, RDF.TYPE, excel.row);
                writeRowMetadata(rowIRI, row, er);
                for (Cell cell : row) {
                    writeCell(rowIRI, cell, er);
                }
            }
        }
    }

    private void writeSheetMetadata(IRI sheetIRI, Sheet sheet, ExtractionResult er) {
        final String sheetName   = sheet.getSheetName();
        final int    firstRowNum = sheet.getFirstRowNum();
        final int    lastRowNum  = sheet.getLastRowNum();
        er.writeTriple(sheetIRI, excel.sheetName, RDFUtils.literal(sheetName));
        er.writeTriple(sheetIRI, excel.firstRow, RDFUtils.literal(firstRowNum));
        er.writeTriple(sheetIRI, excel.lastRow, RDFUtils.literal(lastRowNum));
    }

    private void writeRowMetadata(IRI rowIRI, Row row, ExtractionResult er) {
        final int    firstCellNum = row.getFirstCellNum();
        final int    lastCellNum  = row.getLastCellNum();
        er.writeTriple(rowIRI, excel.firstCell , RDFUtils.literal(firstCellNum));
        er.writeTriple(rowIRI, excel.lastCell  , RDFUtils.literal(lastCellNum ));
    }

    private void writeCell(IRI rowIRI, Cell cell, ExtractionResult er) {
        final IRI cellType = cellTypeToType(cell.getCellType());
        if (cellType == null)
            return; // Skip unsupported cells.
        final IRI cellIRI = getCellIRI(rowIRI, cell);
        er.writeTriple(rowIRI, excel.containsCell, cellIRI);
        er.writeTriple(cellIRI, RDF.TYPE, excel.cell);
        er.writeTriple(
                cellIRI,
                excel.cellValue,
                RDFUtils.literal(cell.getStringCellValue(), cellType)
        );
    }

    private IRI getSheetIRI(IRI documentIRI, Sheet sheet) {
        return RDFUtils.iri(documentIRI.toString() + "/sheet/" + sheet.getSheetName());
    }

    private IRI getRowIRI(IRI sheetIRI, Row row) {
        return RDFUtils.iri(sheetIRI.toString() + "/" + row.getRowNum());
    }

    private IRI getCellIRI(IRI rowIRI, Cell cell) {
        return RDFUtils.iri(rowIRI +
		String.format("/%d/", cell.getColumnIndex()));
    }

    private IRI cellTypeToType(CellType cellType) {
        final String postfix;
        if (cellType == null) {
            postfix = null;
        } else {
            switch (cellType) {
                case STRING:
                    postfix = "string";
                    break;
                case BOOLEAN:
                    postfix = "boolean";
                    break;
                case NUMERIC:
                    postfix = "numeric";
                    break;
                default:
                    postfix = null;
            }
        }
        return postfix == null ? null : RDFUtils.iri(excel.getNamespace().toString() + postfix);
    }


}
