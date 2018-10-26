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

package org.apache.any23.extractor.calendar;

import org.apache.any23.extractor.ExtractorFactory;
import org.junit.Test;

import java.io.IOException;

public class JCalExtractorTest extends BaseCalendarExtractorTest {

    @Override
    String filePrefix() {
        return "/calendar/json/";
    }

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new JCalExtractorFactory();
    }

    @Test
    public void testRFC7265example1() throws IOException {
        extractAndVerifyAgainstNQuads("rfc7265-example1.json", "rfc7265-example1-expected.nquads");
    }

    @Test
    public void testRFC7265example2() throws IOException {
        extractAndVerifyAgainstNQuads("rfc7265-example2.json", "rfc7265-example2-expected.nquads");
    }
}
