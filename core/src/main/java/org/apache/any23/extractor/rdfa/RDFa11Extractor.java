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

package org.apache.any23.extractor.rdfa;

import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.rdf.BaseRDFExtractor;
import org.apache.any23.extractor.rdf.RDFParserFactory;
import org.apache.any23.util.StreamUtils;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.w3c.dom.Document;

/**
 * {@link org.apache.any23.extractor.Extractor} implementation for
 * <a href="http://www.w3.org/TR/rdfa-core/">RDFa 1.1</a> specification.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFa11Extractor extends BaseRDFExtractor {

    public RDFa11Extractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public RDFa11Extractor() {
        this(false, false);
    }

    @Override
    public ExtractorDescription getDescription() {
        return RDFa11ExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getRDFa11Parser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }

    @Override
    public void run(ExtractionParameters extractionParameters,
        ExtractionContext context, Document in, ExtractionResult out)
            throws IOException, ExtractionException {
      RDFParser parser = RDFParserFactory.getInstance().getRDFa11Parser(
          isVerifyDataType(), isStopAtFirstError(), context, out);
      try { 
        parser.parse(StreamUtils.documentToInputStream(in), in.getDocumentURI());
      } catch (RDFParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (RDFHandlerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (TransformerConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (TransformerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (TransformerFactoryConfigurationError e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
}
