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

package org.apache.any23.cli;

import org.apache.any23.Any23OnlineTestBase;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test case for {@link Crawler} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CrawlerTest extends Any23OnlineTestBase {

    public static final Logger logger = LoggerFactory.getLogger(CrawlerTest.class);

    @Test
    public void testCLI() throws IOException, RDFHandlerException, RDFParseException {
        assumeOnlineAllowed();

        final File outFile = File.createTempFile("crawler-test", ".nq");
        outFile.delete();
        logger.debug( "Outfile: " + outFile.getAbsolutePath() );

        final Future future = Executors.newSingleThreadExecutor().submit(
            new Runnable() {
                @Override
                public void run() {
                    Crawler.main(
                            String.format(
                                    "-f nquads -maxpages 50 -maxdepth 1 -politenessdelay 500 -o %s " +
                                    "http://eventiesagre.it/",
                                    outFile.getAbsolutePath()
                            ).split(" ")
                    );
                }
            }
        );

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            // OK.
            if( ! (e instanceof TimeoutException) ) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue("The output file has not been created.", outFile.exists());

        final String[] lines = FileUtils.readFileLines(outFile);
        final StringBuilder allLinesExceptLast = new StringBuilder();
        for(int i = 0; i < lines.length - 1; i++) {
            allLinesExceptLast.append(lines[i]);
        }

        final Statement[] statements = RDFUtils.parseRDF(RDFUtils.Parser.NQuads, allLinesExceptLast.toString());
        Assert.assertTrue(statements.length > 0);
    }

}
