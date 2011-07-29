/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.cli;

import org.deri.any23.vocab.RDFSchemaUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Prints out the vocabulary <i>RDFSchema</i> as <i>NQuads</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@ToolRunner.Description("Prints out the RDF Schema of the vocabularies used by Any23.")
public class VocabPrinter implements Tool {

    public static void main(String[] args) throws IOException {
        System.exit( new VocabPrinter().run(args) );
    }

    public int run(String[] args) {
        final BufferedOutputStream bos = new BufferedOutputStream(System.out);
        try {
            RDFSchemaUtils.serializeVocabulariesToNQuads(System.out);
        }catch (Exception e) {
            e.printStackTrace(System.err);
            return 1;
        } finally {
            try {
                bos.flush();
            } catch (IOException ioe) { ioe.printStackTrace(System.err); }
        }
        return 0;
    }

}
