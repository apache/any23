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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * General test to verify usability of the {@link XSSFWorkbook} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class XSSFWorkbookTest {

    private static final Logger logger = LoggerFactory.getLogger(XSSFWorkbookTest.class);

    @Test
    public void testXLSXFormatAccess() throws IOException {
        verifyResource("test1-workbook.xlsx");
    }

    @Test
    public void testXLSFormatAccess() throws IOException {
        verifyResource("test2-workbook.xls");
    }

    private void verifyResource(String resource) throws IOException {
        final InputStream document = this.getClass().getResourceAsStream(resource);
        final Workbook wb;
        if(resource.endsWith(".xlsx")) {
            wb = new XSSFWorkbook(document);
        } else if(resource.endsWith("xls")) {
            wb = new HSSFWorkbook(document);
        } else {
            throw new IllegalArgumentException("Unsupported extension for resource " + resource);
        }
        Assert.assertEquals(2, wb.getNumberOfSheets());
        Sheet sheet;
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            sheet = wb.getSheetAt(sheetIndex);
            int rowcount = 0;
            for (Row row : sheet) {
                rowcount++;
                int cellcount = 0;
                for (Cell cell : row) {
                    cellcount++;
                    logger.debug(
                            String.format(
                                    "cell [%d, %d]: %s",
                                    cell.getRowIndex(),
                                    cell.getColumnIndex(),
                                    cell.getStringCellValue()
                            )
                    );
                    verifyContent(sheetIndex, cell.getRowIndex(), cell.getColumnIndex(), cell.getStringCellValue());
                }
                Assert.assertEquals(3, cellcount);
            }
            Assert.assertEquals(3, rowcount);
        }
    }

    private void verifyContent(int sheet, int row, int col, String content) {
        Assert.assertEquals(
                String.format("%s %d.%d", sheet == 0 ? "a" : "b", row + 1, col + 1),
                content
        );
    }

}
