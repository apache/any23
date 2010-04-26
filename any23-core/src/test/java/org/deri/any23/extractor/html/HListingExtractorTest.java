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

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.HLISTING;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;


/**
 *
 * Reference Test class for the {@link org.deri.any23.extractor.html.HListingExtractor} extractor.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 *
 */
public class HListingExtractorTest extends AbstractExtractorTestCase {

    protected ExtractorFactory<?> getExtractorFactory() {
        return HListingExtractor.factory;
    }

    @Test
    public void testNoMicroformats() throws RepositoryException {
        assertExtracts("html/html-without-uf.html");
        Assert.assertTrue(conn.isEmpty());
    }

    @Test
    public void testListingWithouthContent() throws RepositoryException {
        assertExtracts("microformats/hlisting/empty.html");
        Assert.assertFalse(conn.isEmpty());
    }

    @Test
    public void testSingleAction() throws RepositoryException {
        assertExtracts("microformats/hlisting/single-action.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.action, HLISTING.offer);
    }

    @Test
    public void testMultipleActions() throws RepositoryException {
        assertExtracts("microformats/hlisting/multiple-actions.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.action, HLISTING.offer);
        assertContains(HLISTING.action, HLISTING.sell);
    }

    @Test
    public void testMultipleActionsNested() throws RepositoryException {
        assertExtracts("microformats/hlisting/multiple-actions-nested.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.action, HLISTING.offer);
        assertContains(HLISTING.action, HLISTING.sell);
        assertContains(HLISTING.action, HLISTING.rent);
    }

    @Test
    public void testActionsOutside() throws RepositoryException {
        assertExtracts("microformats/hlisting/single-action-outside.html");
        Assert.assertFalse(conn.isEmpty());
        assertNotContains(HLISTING.action, HLISTING.offer);
    }

    @Test
    public void testListerFn() throws RepositoryException {
        assertExtracts("microformats/hlisting/actions-lister-fn.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.action,     HLISTING.offer);
        assertContains(RDF.TYPE,            HLISTING.Lister);
        assertContains(HLISTING.listerName, "mike");
    }

    @Test
    public void testListerFnTel() throws RepositoryException {
        assertExtracts("microformats/hlisting/actions-lister-fn-tel.html");
        Assert.assertFalse(conn.isEmpty());

        assertContains(HLISTING.action, HLISTING.offer);
        assertContains(HLISTING.listerName,     "John Broker");
        assertContains(RDF.TYPE,                HLISTING.Lister);
        assertContains(HLISTING.tel,            "(110) 555-1212");
    }

    @Test
    public void testItemFn() throws RepositoryException {
        assertExtracts("microformats/hlisting/item-fn.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(RDF.TYPE,          HLISTING.Item);
        assertContains(HLISTING.itemName, "Parking space");
    }

    @Test
    public void testItemFnUrl() throws RepositoryException {
        assertExtracts("microformats/hlisting/item-fn-url.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(RDF.TYPE,            HLISTING.Item);
        assertContains(HLISTING.itemUrl,    RDFHelper.uri("http://item.com/"));
        assertContains(HLISTING.itemName,   "Parking space");
    }

    @Test
    public void testItemPhotoImg() throws RepositoryException {
        assertExtracts("microformats/hlisting/item-fn-url-photo-img.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(RDF.TYPE,            HLISTING.Item);
        assertContains(HLISTING.itemUrl,    RDFHelper.uri("http://item.com/"));
        assertContains(HLISTING.itemName,   "Parking space");
        assertContains(HLISTING.itemPhoto,  RDFHelper.uri(baseURI.stringValue() + "photo.jpg"));
    }

    @Test
    public void testItemPhotoHref() throws RepositoryException {
        assertExtracts("microformats/hlisting/item-fn-photo-href.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(RDF.TYPE,            HLISTING.Item);
        assertContains(HLISTING.itemName,   "Parking space");
        assertContains(HLISTING.itemPhoto,  RDFHelper.uri(baseURI.stringValue() + "pic.jpg"));
    }

    @Test
    public void testKelkoo() throws RepositoryException {
        assertExtracts("microformats/hlisting/kelkoo.html");
        Assert.assertFalse(conn.isEmpty());

        assertContains(RDF.TYPE,            HLISTING.Listing);
        assertContains(RDF.TYPE,            HLISTING.Item);
        assertContains(HLISTING.action,     HLISTING.offer);
        assertContains(HLISTING.itemName,   "Benq MP622 - DLP Projector - 2700 ANSI lumens - XGA...");

        assertContains(HLISTING.description, (Resource) null);

        assertContains(RDF.TYPE, HLISTING.Lister);

        assertContains(HLISTING.listerUrl, RDFHelper.uri(baseURI.stringValue() +
                "m-4621623-pc-world-business.html"));
        assertContains(HLISTING.listerOrg, "PC World Business");

        assertContains(HLISTING.listerLogo, RDFHelper.uri(baseURI.stringValue() +
                "data/merchantlogos/4621623/pcworld.gif"));

        assertContains(HLISTING.listerName, "PC World Business");

        assertContains(HLISTING.itemPhoto,
                RDFHelper.uri("http://img.kelkoo.com/uk/medium/675/496/00117250662929509422269096808645163496675.jpg"));

        assertContains(HLISTING.price, "\u00A3480.17");
    }

    @Test
    public void testKelkooFull() throws RepositoryException {
        assertExtracts("microformats/hlisting/kelkoo-full.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(RDF.TYPE,            HLISTING.Listing);
        assertContains(RDF.TYPE,            HLISTING.Item);
        assertContains(HLISTING.action,     HLISTING.offer);
        assertContains(HLISTING.itemUrl,    RDFHelper.uri("http://bob.example.com/"));
        assertContains(RDF.TYPE,            HLISTING.Lister);

        assertContains(HLISTING.itemName, "Hanro Touch Feeling Shape Bodysuit Underwear");
        assertContains(HLISTING.itemName, "Spanx Slim Cognito - Shaping Mid-Thigh Bodysuit");
        assertContains(HLISTING.itemName, "Spanx Spanx Slim Cognito High Leg Shaping...");

        assertContains(HLISTING.itemPhoto,
                RDFHelper.uri("http://img.kelkoo.com/uk/medium/657/449/00162475823966154731749844283942320449657.jpg"));
        assertContains(HLISTING.itemPhoto,
                RDFHelper.uri("http://img.kelkoo.com/uk/medium/545/091/00154244199719224091151116421737036091545.jpg"));
        assertContains(HLISTING.itemPhoto,
                RDFHelper.uri("http://img.kelkoo.com/uk/medium/018/426/00156227992563192632349212375692442426018.jpg"));


        assertContains(HLISTING.listerLogo,
                RDFHelper.uri("http://bob.example.com/data/merchantlogos/6957423/socksfox.gif"));
        assertContains(HLISTING.listerLogo,
                RDFHelper.uri("http://bob.example.com/data/merchantlogos/3590723/mytightsnew.gif"));
        assertContains(HLISTING.listerLogo,
                RDFHelper.uri("http://bob.example.com/data/merchantlogos/2977501/pleaseonlinelogo88x311.gif"));


        assertContains(HLISTING.listerName, "Socks Fox");
        assertContains(HLISTING.listerName, "My Tights");
        assertContains(HLISTING.listerName, "Tightsplease");


        assertContains(HLISTING.listerOrg, "Socks Fox");
        assertContains(HLISTING.listerOrg, "My Tights");
        assertContains(HLISTING.listerName, "Tightsplease");

        assertContains(HLISTING.listerUrl, RDFHelper.uri("http://bob.example.com/m-6957423-socks-fox.html"));
        assertContains(HLISTING.listerUrl, RDFHelper.uri("http://bob.example.com/m-3590723-my-tights.html"));
        assertContains(HLISTING.listerUrl, RDFHelper.uri("http://bob.example.com/m-2977501-tightsplease.html"));

        assertContains(HLISTING.price, "\u00A380");
        assertContains(HLISTING.price, "\u00A347.95");
        assertContains(HLISTING.price, "\u00A354.99");
    }

    @Test
    public void testListerURL() throws RepositoryException {
        assertExtracts("microformats/hlisting/actions-lister-url.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.action,     HLISTING.offer);
        assertContains(HLISTING.listerName, "John Broker");
        assertContains(RDF.TYPE,            HLISTING.Lister);
        assertContains(HLISTING.listerUrl,  RDFHelper.uri("http://homepage.com"));
    }

    @Test
    public void testListerEmail() throws RepositoryException {
        assertExtracts("microformats/hlisting/actions-lister-email.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.action,     HLISTING.offer);
        assertContains(HLISTING.listerName, "John Broker");
        assertContains(RDF.TYPE,            HLISTING.Lister);
        assertContains(FOAF.mbox,           RDFHelper.uri("mailto:info@commerce.net"));
    }

    @Test
    public void testListerEmailHref() throws RepositoryException {
        assertExtracts("microformats/hlisting/actions-lister-email-href.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.action,     HLISTING.offer);
        assertContains(RDF.TYPE,            HLISTING.Lister);
        assertContains(HLISTING.listerName, "John Broker");
        assertContains(FOAF.mbox,           RDFHelper.uri("mailto:info@commerce.net"));
    }

    @Test
    public void testDtListed() throws RepositoryException {
        assertExtracts("microformats/hlisting/dtlisted-dtexpired.html");
        Assert.assertFalse(conn.isEmpty());
        assertNotContains(HLISTING.action, HLISTING.offer);
        assertContains(HLISTING.dtlisted,  "2006-02-02");
    }

    @Test
    public void testDtExpired() throws RepositoryException {
        assertExtracts("microformats/hlisting/dtlisted-dtexpired.html");
        Assert.assertFalse(conn.isEmpty());
        assertNotContains(HLISTING.action, HLISTING.offer);
        assertContains(HLISTING.dtexpired, "2006-04-01");
    }

    @Test
    public void testSummary() throws RepositoryException {
        assertExtracts("microformats/hlisting/summary.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.summary, "summary stuff");
    }

    @Test
    public void testDtListedAndExpired() throws RepositoryException {
        assertExtracts("microformats/hlisting/dtlisted-dtexpired.html");
        Assert.assertFalse(conn.isEmpty());
        assertNotContains(HLISTING.action,  HLISTING.offer);
        assertContains(HLISTING.dtlisted,   "2006-02-02");
        assertContains(HLISTING.dtexpired,  "2006-04-01");
    }

    @Test
    public void testPrice() throws RepositoryException {
        assertExtracts("microformats/hlisting/price.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.price,      "$215/qtr");
    }

    @Test
    public void testPriceAndDt() throws RepositoryException {
        assertExtracts("microformats/hlisting/dtlisted-dtexpired.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.price,      "$215/qtr");
        assertContains(HLISTING.dtlisted,   "2006-02-02");
        assertContains(HLISTING.dtexpired,  "2006-04-01");
    }

    @Test
    public void testPermalink() throws RepositoryException {
        assertExtracts("microformats/hlisting/summary-bookmark.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.permalink,  "http://livre.com/book");
        assertContains(HLISTING.listerUrl,  RDFHelper.uri("http://livre.com/author"));
    }

    @Test
    public void testComplexDescription() throws RepositoryException {
        assertExtracts("microformats/hlisting/description-complex.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.description,
                "BenQ today introduced two new additions to its renowned bus... + Show details");
    }

    @Test
    public void testDescription() throws RepositoryException {
        assertExtracts("microformats/hlisting/description.html");
        Assert.assertFalse(conn.isEmpty());
        assertContains(HLISTING.description,    "bla bla bla");
    }

}
