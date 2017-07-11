/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.any23.extractor.yaml;

import java.io.InputStream;
import org.apache.any23.mime.MIMEType;
import org.apache.any23.mime.TikaMIMETypeDetector;
import org.apache.any23.mime.purifier.WhiteSpacesPurifier;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jacek
 */
public class YAMLTikaParserTest {

    private static final String file1 = "/org/apache/any23/extractor/yaml/simple-load.yml";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void tikaDetect()
            throws Exception {
        InputStream is = YAMLTikaParserTest.class.getResourceAsStream(file1);
        TikaMIMETypeDetector detector = new TikaMIMETypeDetector(new WhiteSpacesPurifier());
        MIMEType type = detector.guessMIMEType(null, is, null);

        log.info("Type: {}", type.toString());

        Assert.assertEquals("text/x-yaml", type.toString());
    }

}
