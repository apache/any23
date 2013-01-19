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

package org.apache.any23.extractor.html;

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.FOAF;
import org.apache.any23.vocab.HLISTING;
import org.apache.any23.vocab.SINDICE;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * Reference Test class for the {@link HListingExtractor} extractor.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 *
 */
public class HListingExtractorTest extends AbstractExtractorTestCase {

    private static final SINDICE  vSINDICE  = SINDICE.getInstance();
    private static final HLISTING vHLISTING = HLISTING.getInstance();
    private static final FOAF     vFOAF     = FOAF.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(HListingExtractorTest.class);

    protected ExtractorFactory<?> getExtractorFactory() {
        return new HListingExtractorFactory();
    }

    @Test
    public void testNoMicroformats() throws RepositoryException {
        assertExtract("/html/html-without-uf.html");
        assertModelEmpty();
    }

    @Test
    public void testListingWithouthContent() throws RepositoryException {
        assertExtract("/microformats/hlisting/empty.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 3);
    }

    @Test
    public void testSingleAction() throws RepositoryException {
        assertExtract("/microformats/hlisting/single-action.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.action, vHLISTING.offer);
    }

    @Test
    public void testMultipleActions() throws RepositoryException {
        assertExtract("/microformats/hlisting/multiple-actions.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.action, vHLISTING.offer);
        assertContains(vHLISTING.action, vHLISTING.sell);
    }

    @Test
    public void testMultipleActionsNested() throws RepositoryException {
        assertExtract("/microformats/hlisting/multiple-actions-nested.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.action, vHLISTING.offer);
        assertContains(vHLISTING.action, vHLISTING.sell);
        assertContains(vHLISTING.action, vHLISTING.rent);
    }

    @Test
    public void testActionsOutside() throws RepositoryException {
        assertExtract("/microformats/hlisting/single-action-outside.html");
        assertModelNotEmpty();
        assertNotContains(vHLISTING.action, vHLISTING.offer);
    }

    @Test
    public void testListerFn() throws RepositoryException {
        assertExtract("/microformats/hlisting/actions-lister-fn.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.action,     vHLISTING.offer);
        assertContains(RDF.TYPE,             vHLISTING.Lister);
        assertContains(vHLISTING.listerName, "mike");
    }

    @Test
    public void testListerFnTel() throws RepositoryException {
        assertExtract("/microformats/hlisting/actions-lister-fn-tel.html");
        assertModelNotEmpty();

        assertContains(vHLISTING.action    , vHLISTING.offer);
        assertContains(vHLISTING.listerName, "John Broker");
        assertContains(RDF.TYPE,             vHLISTING.Lister);
        assertContains(vHLISTING.tel,        "(110) 555-1212");
    }

    @Test
    public void testItemFn() throws RepositoryException {
        assertExtract("/microformats/hlisting/item-fn.html");
        assertModelNotEmpty();
        assertContains(RDF.TYPE,           vHLISTING.Item);
        assertContains(vHLISTING.itemName, "Parking space");
    }

    @Test
    public void testItemFnUrl() throws RepositoryException {
        assertExtract("/microformats/hlisting/item-fn-url.html");
        assertModelNotEmpty();
        assertContains(RDF.TYPE,             vHLISTING.Item);
        assertContains(vHLISTING.itemUrl,    RDFUtils.uri("http://item.com/"));
        assertContains(vHLISTING.itemName,   "Parking space");
    }

    @Test
    public void testItemPhotoImg() throws RepositoryException {
        assertExtract("/microformats/hlisting/item-fn-url-photo-img.html");
        assertModelNotEmpty();
        assertContains(RDF.TYPE,             vHLISTING.Item);
        assertContains(vHLISTING.itemUrl,    RDFUtils.uri("http://item.com/"));
        assertContains(vHLISTING.itemName,   "Parking space");
        assertContains(vHLISTING.itemPhoto,  RDFUtils.uri(baseURI.stringValue() + "photo.jpg"));
    }

    @Test
    public void testItemPhotoHref() throws RepositoryException {
        assertExtract("/microformats/hlisting/item-fn-photo-href.html");
        assertModelNotEmpty();
        assertContains(RDF.TYPE,             vHLISTING.Item);
        assertContains(vHLISTING.itemName,   "Parking space");
        assertContains(vHLISTING.itemPhoto,  RDFUtils.uri(baseURI.stringValue() + "pic.jpg"));
    }

    @Test
    public void testKelkoo() throws RepositoryException {
        assertExtract("/microformats/hlisting/kelkoo.html");
        assertModelNotEmpty();

        assertContains(RDF.TYPE,             vHLISTING.Listing);
        assertContains(RDF.TYPE,             vHLISTING.Item);
        assertContains(vHLISTING.action,     vHLISTING.offer);
        assertContains(vHLISTING.itemName,   "Benq MP622 - DLP Projector - 2700 ANSI lumens - XGA...");

        assertContains(vHLISTING.description, (Resource) null);

        assertContains(RDF.TYPE, vHLISTING.Lister);

        assertContains(vHLISTING.listerUrl, RDFUtils.uri(baseURI.stringValue() +
                "m-4621623-pc-world-business.html"));
        assertContains(vHLISTING.listerOrg, "PC World Business");

        assertContains(vHLISTING.listerLogo, RDFUtils.uri(baseURI.stringValue() +
                "data/merchantlogos/4621623/pcworld.gif"));

        assertContains(vHLISTING.listerName, "PC World Business");

        assertContains(vHLISTING.itemPhoto,
                RDFUtils.uri("http://img.kelkoo.com/uk/medium/675/496/00117250662929509422269096808645163496675.jpg"));

        assertContains(vHLISTING.price, "\u00A3480.17");
    }

    @Test
    public void testKelkooFull() throws RepositoryException {
        assertExtract("/microformats/hlisting/kelkoo-full.html");
        assertModelNotEmpty();
        assertContains(RDF.TYPE,            vHLISTING.Listing);
        assertContains(RDF.TYPE,            vHLISTING.Item);
        assertContains(vHLISTING.action,     vHLISTING.offer);
        assertContains(vHLISTING.itemUrl,    RDFUtils.uri("http://bob.example.com/"));
        assertContains(RDF.TYPE,            vHLISTING.Lister);

        assertContains(vHLISTING.itemName, "Hanro Touch Feeling Shape Bodysuit Underwear");
        assertContains(vHLISTING.itemName, "Spanx Slim Cognito - Shaping Mid-Thigh Bodysuit");
        assertContains(vHLISTING.itemName, "Spanx Spanx Slim Cognito High Leg Shaping...");

        assertContains(vHLISTING.itemPhoto,
                RDFUtils.uri("http://img.kelkoo.com/uk/medium/657/449/00162475823966154731749844283942320449657.jpg"));
        assertContains(vHLISTING.itemPhoto,
                RDFUtils.uri("http://img.kelkoo.com/uk/medium/545/091/00154244199719224091151116421737036091545.jpg"));
        assertContains(vHLISTING.itemPhoto,
                RDFUtils.uri("http://img.kelkoo.com/uk/medium/018/426/00156227992563192632349212375692442426018.jpg"));


        assertContains(vHLISTING.listerLogo,
                RDFUtils.uri("http://bob.example.com/data/merchantlogos/6957423/socksfox.gif"));
        assertContains(vHLISTING.listerLogo,
                RDFUtils.uri("http://bob.example.com/data/merchantlogos/3590723/mytightsnew.gif"));
        assertContains(vHLISTING.listerLogo,
                RDFUtils.uri("http://bob.example.com/data/merchantlogos/2977501/pleaseonlinelogo88x311.gif"));


        assertContains(vHLISTING.listerName, "Socks Fox");
        assertContains(vHLISTING.listerName, "My Tights");
        assertContains(vHLISTING.listerName, "Tightsplease");


        assertContains(vHLISTING.listerOrg, "Socks Fox");
        assertContains(vHLISTING.listerOrg, "My Tights");
        assertContains(vHLISTING.listerName, "Tightsplease");

        assertContains(vHLISTING.listerUrl, RDFUtils.uri("http://bob.example.com/m-6957423-socks-fox.html"));
        assertContains(vHLISTING.listerUrl, RDFUtils.uri("http://bob.example.com/m-3590723-my-tights.html"));
        assertContains(vHLISTING.listerUrl, RDFUtils.uri("http://bob.example.com/m-2977501-tightsplease.html"));

        assertContains(vHLISTING.price, "\u00A380");
        assertContains(vHLISTING.price, "\u00A347.95");
        assertContains(vHLISTING.price, "\u00A354.99");
    }

    @Test
    public void testListerURL() throws RepositoryException {
        assertExtract("/microformats/hlisting/actions-lister-url.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.action,     vHLISTING.offer);
        assertContains(vHLISTING.listerName, "John Broker");
        assertContains(RDF.TYPE,            vHLISTING.Lister);
        assertContains(vHLISTING.listerUrl,  RDFUtils.uri("http://homepage.com"));
    }

    @Test
    public void testListerEmail() throws RepositoryException {
        assertExtract("/microformats/hlisting/actions-lister-email.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.action,     vHLISTING.offer);
        assertContains(vHLISTING.listerName, "John Broker");
        assertContains(RDF.TYPE,             vHLISTING.Lister);
        assertContains(vFOAF.mbox,           RDFUtils.uri("mailto:info@commerce.net"));
    }

    @Test
    public void testListerEmailHref() throws RepositoryException {
        assertExtract("/microformats/hlisting/actions-lister-email-href.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.action,     vHLISTING.offer);
        assertContains(RDF.TYPE,             vHLISTING.Lister);
        assertContains(vHLISTING.listerName, "John Broker");
        assertContains(vFOAF.mbox,           RDFUtils.uri("mailto:info@commerce.net"));
    }

    @Test
    public void testDtListed() throws RepositoryException {
        assertExtract("/microformats/hlisting/dtlisted-dtexpired.html");
        assertModelNotEmpty();
        assertNotContains(vHLISTING.action, vHLISTING.offer);
        assertContains(vHLISTING.dtlisted,  "2006-02-02");
    }

    @Test
    public void testDtExpired() throws RepositoryException {
        assertExtract("/microformats/hlisting/dtlisted-dtexpired.html");
        assertModelNotEmpty();
        assertNotContains(vHLISTING.action,  vHLISTING.offer);
        assertContains(vHLISTING.dtexpired, "2006-04-01");
    }

    @Test
    public void testSummary() throws RepositoryException {
        assertExtract("/microformats/hlisting/summary.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.summary, "summary stuff");
    }

    @Test
    public void testDtListedAndExpired() throws RepositoryException {
        assertExtract("/microformats/hlisting/dtlisted-dtexpired.html");
        assertModelNotEmpty();
        assertNotContains(vHLISTING.action,   vHLISTING.offer);
        assertContains(vHLISTING.dtlisted,   "2006-02-02");
        assertContains(vHLISTING.dtexpired,  "2006-04-01");
    }

    @Test
    public void testPrice() throws RepositoryException {
        assertExtract("/microformats/hlisting/price.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.price,      "$215/qtr");
    }

    @Test
    public void testPriceAndDt() throws RepositoryException {
        assertExtract("/microformats/hlisting/dtlisted-dtexpired.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.price,      "$215/qtr");
        assertContains(vHLISTING.dtlisted,   "2006-02-02");
        assertContains(vHLISTING.dtexpired,  "2006-04-01");
    }

    @Test
    public void testPermalink() throws RepositoryException {
        assertExtract("/microformats/hlisting/summary-bookmark.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.permalink,  "http://livre.com/book");
        assertContains(vHLISTING.listerUrl,  RDFUtils.uri("http://livre.com/author"));
    }

    @Test
    public void testComplexDescription() throws RepositoryException {
        assertExtract("/microformats/hlisting/description-complex.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.description,
                "BenQ today introduced two new additions to its renowned bus... + Show details");
    }

    @Test
    public void testDescription() throws RepositoryException {
        assertExtract("/microformats/hlisting/description.html");
        assertModelNotEmpty();
        assertContains(vHLISTING.description,    "bla bla bla");
    }

}
