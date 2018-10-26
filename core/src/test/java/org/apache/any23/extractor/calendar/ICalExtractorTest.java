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


public class ICalExtractorTest extends BaseCalendarExtractorTest {
    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new ICalExtractorFactory();
    }

    @Override
    String filePrefix() {
        return "/calendar/text/";
    }

    @Test
    public void testRFC5545example1() throws IOException {
        extractAndVerifyAgainstNQuads("rfc5545-example1.ics", "rfc5545-example1-expected.nquads");
    }

    @Test
    public void testRFC5545example2() throws IOException {
        extractAndVerifyAgainstNQuads("rfc5545-example2.ics", "rfc5545-example2-expected.nquads");
    }

    @Test
    public void testBadTimezone() throws IOException {
        extractAndVerifyAgainstNQuads("example2-bad-timezone.ics", "example2-bad-timezone-expected.nquads");
    }

    @Test
    public void testExternalTimezone() throws IOException {
        extractAndVerifyAgainstNQuads("example2-external-timezone.ics", "example2-external-timezone-expected.nquads");
    }

    @Test
    public void testRFC5545example3() throws IOException {
        extractAndVerifyAgainstNQuads("rfc5545-example3.ics", "rfc5545-example3-expected.nquads");
    }

    @Test
    public void testRFC5545example4() throws IOException {
        extractAndVerifyAgainstNQuads("rfc5545-example4.ics", "rfc5545-example4-expected.nquads");
    }

    @Test
    public void testRFC5545example5() throws IOException {
        extractAndVerifyAgainstNQuads("rfc5545-example5.ics", "rfc5545-example5-expected.nquads");
    }

    @Test
    public void testRFC5545example6() throws IOException {
        extractAndVerifyAgainstNQuads("rfc5545-example6.ics", "rfc5545-example6-expected.nquads");
    }

}
