package org.deri.any23.rdf;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

/**
 * Reference Test class for {@link Any23ValueFactoryWrapper}.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 *
 */
public class Any23ValueFactoryWrapperTest {

    // TODO (low) fill out this test for a better test coverage

    @Test
    public void testXSDCompliantDate() throws DatatypeConfigurationException, ParseException {
        Assert.assertEquals("1997-09-01T13:00:00.000Z",
                Any23ValueFactoryWrapper.getXSDDate(
                        "19970901T1300Z",
                        "yyyyMMdd'T'HHmm'Z'"
                )
        );
    }

}
