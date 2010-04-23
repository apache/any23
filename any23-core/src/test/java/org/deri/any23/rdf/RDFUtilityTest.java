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

package org.deri.any23.rdf;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Reference test class for {@link org.deri.any23.rdf.RDFUtility}.
 *
 * @author Davide Palmisano (palmisano@gmail.com)
 */
public class RDFUtilityTest {

    @Test
    public void testFixAbsoluteURI() throws UnsupportedEncodingException, URISyntaxException {
        Assert.assertEquals(
                "Error: passed URIs are not the same.",
                "http://example.com/resource/the%20godfather",
                RDFUtility.fixAbsoluteURI("http://example.com/resource/the godfather")
        );

        Assert.assertEquals(
                "Error: passed URIs are not the same.",
                "http://dbpedia.org/",
                RDFUtility.fixAbsoluteURI("http://dbpedia.org")
        );
    }

    @Test
    public void testGetXSDDate() throws DatatypeConfigurationException, ParseException {
        Assert.assertEquals("1997-09-01T13:00:00.000Z",
                RDFUtility.getXSDDate(
                        "19970901T1300Z",
                        "yyyyMMdd'T'HHmm'Z'"
                )
        );
    }

}
