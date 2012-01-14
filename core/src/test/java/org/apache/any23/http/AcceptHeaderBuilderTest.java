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

package org.apache.any23.http;

import org.apache.any23.mime.MIMEType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Reference test for {@link AcceptHeaderBuilder}
 * 
 */
public class AcceptHeaderBuilderTest {

    @Test
    public void testEmpty() {
        Assert.assertNull(buildHeader(Collections.<String>emptyList()));
    }

    @Test
    public void testSingleHeaderSpecific() {
        Assert.assertEquals("text/html",
                buildHeader(Arrays.asList("text/html")));
    }

    @Test
    public void testSingleHeaderSpecificWithQ() {
        Assert.assertEquals("text/html;q=0.5",
                buildHeader(Arrays.asList("text/html;q=0.5")));
    }

    @Test
    public void testSuppressQIfEquals1() {
        Assert.assertEquals("text/html",
                buildHeader(Arrays.asList("text/html;q=1")));
    }

    @Test
    public void testSingleHeaderSubtypeWildcard() {
        Assert.assertEquals("text/*;q=0.5",
                buildHeader(Arrays.asList("text/*;q=0.5")));
    }

    @Test
    public void testSingleHeaderTypeWildcard() {
        Assert.assertEquals("*/*;q=0.5",
                buildHeader(Arrays.asList("*/*;q=0.5")));
    }

    @Test
    public void testMultipleIndependentHeaders() {
        Assert.assertEquals("image/jpeg;q=0.2, text/html, text/plain;q=0.5",
                buildHeader(Arrays.asList(
                        "image/jpeg;q=0.2", "text/html;q=1.0", "text/plain;q=0.5")));
    }

    @Test
    public void testHighestSpecificValueIsChosen() {
        Assert.assertEquals("image/jpeg",
                buildHeader(Arrays.asList(
                        "image/jpeg;q=0.2", "image/jpeg")));
        Assert.assertEquals("image/jpeg",
                buildHeader(Arrays.asList(
                        "image/jpeg", "image/jpeg;q=0.2")));
    }

    @Test
    public void testHighestSubtypeWildcardIsChosen() {
        Assert.assertEquals("image/*",
                buildHeader(Arrays.asList(
                        "image/*;q=0.2", "image/*")));
        Assert.assertEquals("image/*",
                buildHeader(Arrays.asList(
                        "image/*", "image/*;q=0.2")));
    }

    @Test
    public void testHighestTypeWildcardIsChosen() {
        Assert.assertEquals("*/*",
                buildHeader(Arrays.asList(
                        "*/*;q=0.2", "*/*")));
        Assert.assertEquals("*/*",
                buildHeader(Arrays.asList(
                        "*/*", "*/*;q=0.2")));
    }

    @Test
    public void testTypeWildcardSuppressesLowerValues() {
        Assert.assertEquals("*/*;q=0.5",
                buildHeader(Arrays.asList(
                        "*/*;q=0.5", "image/*;q=0.2")));
        Assert.assertEquals("*/*;q=0.5",
                buildHeader(Arrays.asList(
                        "*/*;q=0.5", "image/jpeg;q=0.2")));
    }

    @Test
    public void testSubtypeWildcardSuppressesLowerValues() {
        Assert.assertEquals("image/*;q=0.5",
                buildHeader(Arrays.asList(
                        "image/*;q=0.5", "image/jpeg;q=0.2")));
    }

    private String buildHeader(Collection<String> mimeTypes) {
        Collection<MIMEType> parsedTypes = new ArrayList<MIMEType>();
        for (String s : mimeTypes) {
            parsedTypes.add(MIMEType.parse(s));
        }
        return new AcceptHeaderBuilder(parsedTypes).getAcceptHeader();
    }
}
