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
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.Excel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Implementation of {@link org.apache.any23.extractor.Extractor.ContentExtractor} able to process
 * a <i>MS Excel 97-2007+</i> file format <i>.xls/.xlsx</i> and
 * convert the detected content to triples.
 * This extractor is based on
 * <a href="http://poi.apache.org/spreadsheet/index.html">Apache POI-HSSF and POI-XSSF Java API</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@MetaInfServices( value = Extractor.class )
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
            final URI documentURI = context.getDocumentURI();
            final Workbook workbook = createWorkbook(documentURI, in);
            processWorkbook(documentURI, workbook, er);
        } catch (Exception e) {
            throw new ExtractionException("An error occurred while extracting MS Excel content.", e);
        }
    }

    // TODO: this should be done by Tika, the extractors should be split.
    private Workbook createWorkbook(URI document, InputStream is) throws IOException {
        final String documentURI = document.toString();
        if(documentURI.endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        } else if(documentURI.endsWith("xls")) {
            return new HSSFWorkbook(is);
        } else {
            throw new IllegalArgumentException("Unsupported extension for resource [" + documentURI + "]");
        }
    }

    private void processWorkbook(URI documentURI, Workbook wb, ExtractionResult er) {
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            final Sheet sheet = wb.getSheetAt(sheetIndex);
            final URI sheetURI = getSheetURI(documentURI, sheet);
            er.writeTriple(documentURI, excel.containsSheet, sheetURI);
            er.writeTriple(sheetURI, RDF.TYPE, excel.sheet);
            writeSheetMetadata(sheetURI, sheet, er);
            for (Row row : sheet) {
                final URI rowURI = getRowURI(sheetURI, row);
                er.writeTriple(sheetURI, excel.containsRow, rowURI);
                er.writeTriple(rowURI, RDF.TYPE, excel.row);
                writeRowMetadata(rowURI, row, er);
                for (Cell cell : row) {
                    writeCell(rowURI, cell, er);
                }
            }
        }
    }

    private void writeSheetMetadata(URI sheetURI, Sheet sheet, ExtractionResult er) {
        final String sheetName   = sheet.getSheetName();
        final int    firstRowNum = sheet.getFirstRowNum();
        final int    lastRowNum  = sheet.getLastRowNum();
        er.writeTriple(sheetURI, excel.sheetName, RDFUtils.literal(sheetName));
        er.writeTriple(sheetURI, excel.firstRow, RDFUtils.literal(firstRowNum));
        er.writeTriple(sheetURI, excel.lastRow  , RDFUtils.literal(lastRowNum ));
    }

    private void writeRowMetadata(URI rowURI, Row row, ExtractionResult er) {
        final int    firstCellNum = row.getFirstCellNum();
        final int    lastCellNum  = row.getLastCellNum();
        er.writeTriple(rowURI, excel.firstCell , RDFUtils.literal(firstCellNum));
        er.writeTriple(rowURI, excel.lastCell  , RDFUtils.literal(lastCellNum ));
    }

    private void writeCell(URI rowURI, Cell cell, ExtractionResult er) {
        final URI cellType = cellTypeToType(cell.getCellType());
        if(cellType == null) return; // Skip unsupported cells.
        final URI cellURI = getCellURI(rowURI, cell);
        er.writeTriple(rowURI, excel.containsCell, cellURI);
        er.writeTriple(cellURI, RDF.TYPE, excel.cell);
        er.writeTriple(
                cellURI,
                excel.cellValue,
                RDFUtils.literal(cell.getStringCellValue(), cellType)
        );
    }

    private URI getSheetURI(URI documentURI, Sheet sheet) {
        return RDFUtils.uri( documentURI.toString() + "/sheet/" + sheet.getSheetName() );
    }

    private URI getRowURI(URI sheetURI, Row row) {
        return  RDFUtils.uri( sheetURI.toString() + "/" + row.getRowNum() );
    }

    private URI getCellURI(URI rowURI, Cell cell) {
        return RDFUtils.uri(
            rowURI +
            String.format("/%d/", cell.getColumnIndex())
        );
    }

    private URI cellTypeToType(int cellType) {
        final String postfix;
        switch (cellType) {
            case Cell.CELL_TYPE_STRING:
                postfix = "string";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                postfix = "boolean";
                break;
            case Cell.CELL_TYPE_NUMERIC:
                postfix = "numeric";
                break;
            default:
                postfix = null;
        }
        return postfix == null ? null : RDFUtils.uri(excel.getNamespace().toString() + postfix);
    }


}
