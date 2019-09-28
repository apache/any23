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
package org.apache.any23.extractor.yaml;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.apache.any23.mime.MIMEType;
import org.apache.any23.mime.TikaMIMETypeDetector;
import org.apache.any23.mime.purifier.WhiteSpacesPurifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jacek
 */
public class YAMLTikaParserTest {

    private final String file1 = "/org/apache/any23/extractor/yaml/simple-load.yml";

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private TikaMIMETypeDetector detector;
    
    @Before
    public void prepareDetector() throws Exception {
        detector = new TikaMIMETypeDetector(new WhiteSpacesPurifier());
    }

    /**
     * Yaml type is detected by file name only so detector returns octet type.
     * @throws Exception if there is an error asserting the test data.
     */
    @Test
    public void tikaStreamDetect()
            throws Exception {
        InputStream is = new BufferedInputStream(this.getClass().getResourceAsStream(file1));
        Assert.assertNotNull("Could not find test file: " + file1, is);
        MIMEType type = detector.guessMIMEType(null, is, null);

        // Not currently doing stream detection for YAML, so it returns the default, octet-stream
        Assert.assertEquals("application/octet-stream", type.toString());
    }
    
    @Test
    public void tikaNameDetect() throws Exception {
        String fileName = java.net.URI.create(file1).getPath();
        
        log.debug("normatised file name: {}", fileName);
        MIMEType type = detector.guessMIMEType(fileName, null, null);
        
        Assert.assertEquals("text/x-yaml", type.toString());
    }

}
