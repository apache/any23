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

package org.apache.any23.extractor.csv;

import static java.lang.Character.toUpperCase;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.CSV;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Locale;

/**
 * This extractor produces <i>RDF</i> from a <i>CSV file</i> .
 * It automatically detects fields <i>delimiter</i>. If not able uses
 * the one provided in the <i>Any23</i> configuration.
 *
 * @see CSVReaderBuilder
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class CSVExtractor implements Extractor.ContentExtractor {

    private CSVParser csvParser;

    private IRI[] headerIRIs;

    private CSV csv = CSV.getInstance();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStopAtFirstError(boolean f) {
      //not implemented
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            InputStream in
            , ExtractionResult out
    ) throws IOException, ExtractionException {
        final IRI documentIRI = extractionContext.getDocumentIRI();

        // build the parser
        csvParser = CSVReaderBuilder.build(in);
        Iterator<CSVRecord> rows = csvParser.iterator();

        // get the header and generate the IRIs for column names
        CSVRecord header = rows.hasNext() ? rows.next() : null;
        headerIRIs = processHeader(header, documentIRI);

        // write triples to describe properties
        writeHeaderPropertiesMetadata(header, out);

        int index = 0;
        while (rows.hasNext()) {
            CSVRecord nextLine = rows.next();
            IRI rowSubject = RDFUtils.iri(
                    documentIRI.toString(),
                    "row/" + index
            );
            // add a row type
            out.writeTriple(rowSubject, RDF.TYPE, csv.rowType);
            // for each row produce its statements
            produceRowStatements(rowSubject, nextLine, out);
            // link the row to the document
            out.writeTriple(documentIRI, csv.row, rowSubject);
            // the progressive row number
            out.writeTriple(
                    rowSubject,
                    csv.rowPosition,
                    SimpleValueFactory.getInstance().createLiteral(String.valueOf(index))
            );
            index++;
        }
        // add some CSV metadata such as the number of rows and columns
        addTableMetadataStatements(
                documentIRI,
                out,
                index,
                headerIRIs.length
        );
    }

    /**
     * Check whether a number is an integer.
     *
     * @param number
     * @return
     */
    private boolean isInteger(String number) {
        try {
            Integer.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check whether a number is a float.
     *
     * @param number
     * @return
     */
    private boolean isFloat(String number) {
        try {
            Float.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * It writes <i>RDF</i> statements representing properties of the header.
     *
     * @param header
     * @param out
     */
    private void writeHeaderPropertiesMetadata(CSVRecord header, ExtractionResult out) {
        int index = 0;
        for (IRI singleHeader : headerIRIs) {
            if (index > headerIRIs.length) {
                break;
            }
            String headerString = header.get(index);
            if (!RDFUtils.isAbsoluteIRI(headerString)) {
                out.writeTriple(
                        singleHeader,
                        RDFS.LABEL,
                        SimpleValueFactory.getInstance().createLiteral(headerString)
                );
            }
            out.writeTriple(
                    singleHeader,
                    csv.columnPosition,
                    SimpleValueFactory.getInstance().createLiteral(String.valueOf(index), XMLSchema.INTEGER)
            );
            index++;
        }
    }

    /**
     * It process the first row of the file, returning a list of {@link IRI}s representing
     * the properties for each column. If a value of the header is an absolute <i>IRI</i>
     * then it leave it as is. Otherwise the {@link org.apache.any23.vocab.CSV} vocabulary is used.
     *
     * @param header
     * @return an array of {@link IRI}s identifying the column names.
     */
    private IRI[] processHeader(CSVRecord header, IRI documentIRI) {
        if (header == null)
            return new IRI[0];

        IRI[] result = new IRI[header.size()];
        int index = 0;
        for (String h : header) {
            String candidate = h.trim();
            if (RDFUtils.isAbsoluteIRI(candidate)) {
                result[index] = SimpleValueFactory.getInstance().createIRI(candidate);
            } else {
                result[index] = normalize(candidate, documentIRI);
            }
            index++;
        }
        return result;
    }

    private IRI normalize(String toBeNormalized, IRI documentIRI) {
      String newToBeNormalized = toBeNormalized.trim().toLowerCase(Locale.ROOT).replace("?", "").replace("&", "");

        StringBuilder result = new StringBuilder(documentIRI.toString());

        StringTokenizer tokenizer = new StringTokenizer(newToBeNormalized, " ");
        while (tokenizer.hasMoreTokens()) {
            String current = tokenizer.nextToken();

            result.append(toUpperCase(current.charAt(0))).append(current.substring(1));
        }

        return SimpleValueFactory.getInstance().createIRI(result.toString());
    }

    /**
     * It writes on the provided {@link ExtractionResult}, the </>RDF statements</>
     * representing the row <i>cell</i>. If a  row <i>cell</i> is an absolute <i>IRI</i>
     * then an object property is written, literal otherwise.
     *
     * @param rowSubject
     * @param values
     * @param out
     */
    private void produceRowStatements(
            IRI rowSubject,
            CSVRecord values,
            ExtractionResult out
    ) {
        int index = 0;
        for (String cell : values) {
            if (index >= headerIRIs.length) {
                // there are some row cells that don't have an associated column name
                break;
            }
            if ("".equals(cell)) {
                index++;
                continue;
            }
            IRI predicate = headerIRIs[index];
            Value object = getObjectFromCell(cell);
            out.writeTriple(rowSubject, predicate, object);
            index++;
        }
    }

    private Value getObjectFromCell(String cell) {
        Value object;
        String newCell = cell.trim();
        if (RDFUtils.isAbsoluteIRI(newCell)) {
            object = SimpleValueFactory.getInstance().createIRI(newCell);
        } else {
            IRI datatype = XMLSchema.STRING;
            if (isInteger(newCell)) {
                datatype = XMLSchema.INTEGER;
            } else if(isFloat(newCell)) {
                datatype = XMLSchema.FLOAT;
            }
            object = SimpleValueFactory.getInstance().createLiteral(newCell, datatype);
        }
        return object;
    }

    /**
     * It writes on the provided {@link ExtractionResult} some <i>RDF Statements</i>
     * on generic properties of the <i>CSV</i> file, such as number of rows and columns.
     *
     * @param documentIRI
     * @param out
     * @param numberOfRows
     * @param numberOfColumns
     */
    private void addTableMetadataStatements(
            IRI documentIRI,
            ExtractionResult out,
            int numberOfRows,
            int numberOfColumns) {
        out.writeTriple(
                documentIRI,
                csv.numberOfRows,
                SimpleValueFactory.getInstance().createLiteral(String.valueOf(numberOfRows), XMLSchema.INTEGER)
        );
        out.writeTriple(
                documentIRI,
                csv.numberOfColumns,
                SimpleValueFactory.getInstance().createLiteral(String.valueOf(numberOfColumns), XMLSchema.INTEGER)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtractorDescription getDescription() {
        return CSVExtractorFactory.getDescriptionInstance();
    }
}
