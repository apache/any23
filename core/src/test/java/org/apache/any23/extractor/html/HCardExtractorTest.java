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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.VCard;
import org.junit.Test;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * {@link HCardExtractor} test case.
 */
public class HCardExtractorTest extends AbstractExtractorTestCase {

  private static final VCard vVCARD = VCard.getInstance();

  protected ExtractorFactory<?> getExtractorFactory() {
    return new HCardExtractorFactory();
  }


  @Test
  public void testNoNullPointers() {
    //see https://issues.apache.org/jira/browse/ANY23-351
    assertExtract("/microformats/hcard/null-pointer.html");
    assertContains(vVCARD.logo, RDFUtils.iri("http://cambridgewi.com/wp-content/uploads/connections-images/dean-bluhm/VillagePharmacy-e04951b21968ae4d9fd04cb14ce08ade.jpg"));
    assertContains(vVCARD.email, RDFUtils.iri("mailto:bluhmrph@yahoo.com"));
  }

  @Test
  public void testEMailNotUriReal() throws Exception {
    assertExtract("/microformats/hcard/17-email-not-uri.html");
    assertDefaultVCard();
    assertJohn();
    assertContains(vVCARD.email, RDFUtils.iri("mailto:john@example.com"));
  }

  @Test
  public void testTel() throws Exception {
    assertExtract("/microformats/hcard/21-tel.html");
    assertDefaultVCard();
    String[] tels = { "+1.415.555.1231", "+1.415.555.1235",
            "+1.415.555.1236", "+1.415.555.1237", "+1.415.555.1238",
            "+1.415.555.1239", "+1.415.555.1240", "+1.415.555.1241",
            "+1.415.555.1242", "+1.415.555.1243" };
    for (String tel : tels) {
      assertContains(vVCARD.tel, RDFUtils.iri("tel:" + tel));
    }
    Resource telResource = RDFUtils.iri("tel:+14155551233");
    assertContains(vVCARD.fax, telResource);
    assertContains(vVCARD.workTel, telResource);
    assertContains(vVCARD.homeTel, telResource);
    assertJohn();
  }

  @Test
  public void testAbbrTitleEverything() throws Exception {
    assertExtract("/microformats/hcard/23-abbr-title-everything.html");
    assertDefaultVCard();

    assertContains(vVCARD.fn, "John Doe");
    assertContains(vVCARD.nickname, "JJ");

    assertContains(vVCARD.given_name, "Jonathan");
    assertContains(vVCARD.additional_name, "John");
    assertContains(vVCARD.family_name, "Doe-Smith");
    assertContains(vVCARD.honorific_suffix, "Medical Doctor");

    assertContains(vVCARD.title, "President");
    assertContains(vVCARD.role, "Chief");
    assertContains(vVCARD.tz, "-0700");
    assertContains(vVCARD.bday, "2006-04-04");
    assertContains(vVCARD.tel, RDFUtils.iri("tel:415.555.1234"));
    assertContains(vVCARD.uid, "abcdefghijklmnopqrstuvwxyz");
    assertContains(vVCARD.class_, "public");
    assertContains(vVCARD.note, "this is a note");
    assertContains(vVCARD.organization_name, "Intellicorp");
    assertContains(vVCARD.organization_unit, "Intelligence");

    // We define the property in this extractor _but_ we do not parse it.
    assertContains(vVCARD.geo, (Resource) null);
    // Thus we do not cointain these.
    // The interaction is in @link RDFMergerTest.java
    assertNotContains(RDF.TYPE, vVCARD.Location);
    assertNotContains(null, vVCARD.latitude, "37.77");
    assertNotContains(null, vVCARD.longitude, "-122.41");

    // see above
    assertContains(vVCARD.adr, (Resource) null);
    assertNotContains(RDF.TYPE, vVCARD.Address);
    assertNotContains(null, vVCARD.post_office_box, "Box 1234");
    assertNotContains(null, vVCARD.extended_address, "Suite 100");
    assertNotContains(null, vVCARD.street_address, "123 Fake Street");
    assertNotContains(null, vVCARD.locality, "San Francisco");
    assertNotContains(null, vVCARD.region, "California");
    assertNotContains(null, vVCARD.postal_code, "12345-6789");
    assertNotContains(null, vVCARD.country_name, "United States of America");
    assertNotContains(null, vVCARD.addressType, "work");
  }

  @Test
  public void testGeoAbbr() throws Exception {
    assertExtract("/microformats/hcard/25-geo-abbr.html");
    assertModelNotEmpty();
    assertContains(vVCARD.fn, "Paradise");
    assertContains(RDF.TYPE, vVCARD.Organization);
    assertContains(vVCARD.organization_name, "Paradise");
    // See above: geo property yes, gteo blank node no.
    assertContains(vVCARD.geo, (Resource) null);
    assertNotContains(RDF.TYPE, vVCARD.Location);
    assertNotContains(null, vVCARD.latitude, "30.267991");
    assertNotContains(null, vVCARD.longitude, "-97.739568");
  }

  @Test
  public void testAncestors() throws Exception {
    assertExtract("/microformats/hcard/26-ancestors.html");
    assertModelNotEmpty();

    assertContains(vVCARD.fn, "John Doe");
    assertNotContains(null, vVCARD.fn,
            "Mister Jonathan John Doe-Smith Medical Doctor");
    assertContains(vVCARD.nickname, "JJ");
    assertNotContains(RDF.TYPE, vVCARD.Address);
    assertContains(vVCARD.tz, "-0700");
    assertContains(vVCARD.title, "President");
    assertContains(vVCARD.role, "Chief");
    assertContains(vVCARD.organization_name, "Intellicorp");
    assertContains(vVCARD.organization_unit, "Intelligence");

    assertContains(vVCARD.tel, RDFUtils.iri("tel:415.555.1234"));
    assertContains(vVCARD.uid, "abcdefghijklmnopqrstuvwxyz");
    assertContains(vVCARD.note, "this is a note");
    assertContains(vVCARD.class_, "public");

    assertNotContains(RDF.TYPE, vVCARD.Location);
    assertContains(vVCARD.geo, (Resource) null);
    assertNotContains(null, vVCARD.latitude, "37.77");
    assertNotContains(null, vVCARD.longitude, "-122.41");

    assertContains(RDF.TYPE, vVCARD.Name);
    assertContains(vVCARD.additional_name, "John");
    assertContains(vVCARD.given_name, "Jonathan");
    assertContains(vVCARD.family_name, "Doe-Smith");
    assertContains(vVCARD.honorific_prefix, "Mister");
    assertContains(vVCARD.honorific_suffix, "Medical Doctor");

    assertNotContains(null, vVCARD.post_office_box, "Box 1234");
    assertNotContains(null, vVCARD.extended_address, "Suite 100");
    assertNotContains(null, vVCARD.street_address, "123 Fake Street");
    assertNotContains(null, vVCARD.locality, "San Francisco");
    assertNotContains(null, vVCARD.region, "California");
    assertNotContains(null, vVCARD.postal_code, "12345-6789");
    assertNotContains(null, vVCARD.country_name, "United States of America");
    assertNotContains(null, vVCARD.addressType, "work");
  }

  @Test
  public void testfnOrg() throws Exception {
    assertExtract("/microformats/hcard/30-fn-org.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
    RepositoryResult<Statement> repositoryResult = getStatements(null,
            RDF.TYPE, vVCARD.VCard);
    try {
      while (repositoryResult.hasNext()) {
        Resource card = repositoryResult.next().getSubject();
        assertNotNull(findObject(card, vVCARD.fn));
        String name = findObjectAsLiteral(card, vVCARD.fn);

        assertNotNull(findObject(card, vVCARD.org));
        Resource org = findObjectAsResource(card, vVCARD.org);
        assertNotNull(findObject(org, vVCARD.organization_name));

        if (name.equals("Dan Connolly")) {
          assertNotNull(findObject(card, vVCARD.n));
          assertFalse(name.equals(org.stringValue()));
        }
      }
    } finally {
      repositoryResult.close();
    }
  }

  @Test
  public void testInclude() throws Exception {
    assertExtract("/microformats/hcard/31-include.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 3);
    assertStatementsSize(vVCARD.email, (Value) null, 3);

    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);
    try {
      while (statements.hasNext()) {
        Resource vcard = statements.next().getSubject();

        assertNotNull(findObject(vcard, vVCARD.fn));
        assertEquals("Brian Suda",
                findObjectAsLiteral(vcard, vVCARD.fn));

        assertNotNull(findObject(vcard, vVCARD.url));
        String url = findObjectAsResource(vcard, vVCARD.url)
                .stringValue();
        assertEquals("http://suda.co.uk/", url);

        Resource name = findObjectAsResource(vcard, vVCARD.n);
        assertEquals("Brian",
                findObjectAsLiteral(name, vVCARD.given_name));
        assertEquals("Suda",
                findObjectAsLiteral(name, vVCARD.family_name));

        // Included data.
        assertNotNull(findObject(vcard, vVCARD.email));
        String mail = findObjectAsLiteral(vcard, vVCARD.email);
        assertEquals("mailto:correct@example.com", mail);
      }
    } finally {
      statements.close();
    }
  }

  @Test
  public void testHeader() throws Exception {
    assertExtract("/microformats/hcard/32-header.html");
    assertModelNotEmpty();
    // check fn, name, family, nick.
    assertJohn();

    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);
    try {
      Resource example = RDFUtils.iri("http://example.org/");
      while (statements.hasNext()) {
        Resource card = statements.next().getSubject();
        assertNotNull(findObject(card, vVCARD.fn));

        String fn = findObjectAsLiteral(card, vVCARD.fn);
        if ("Jane Doe".equals(fn)) {
          assertNotFound(card, vVCARD.org);
        } else {
          assertTrue("John Doe".equals(fn)
                  || "Brian Suda".equals(fn));

          assertNotNull(findObject(card, vVCARD.url));
          assertEquals(example,
                  findObjectAsResource(card, vVCARD.url));

          assertNotNull(findObject(card, vVCARD.org));
          Resource org = findObjectAsResource(card, vVCARD.org);
          assertContains(org, RDF.TYPE, vVCARD.Organization);
          assertNotNull(org);
          assertNotNull(findObject(card, vVCARD.org));
          assertNotNull(findObject(org,
                  vVCARD.organization_name));
          assertEquals("example.org",
                  findObjectAsLiteral(org, vVCARD.organization_name));
        }
      }
      // Just to be sure there are no spurious statements.
      // assertStatementsSize(VCARD.org, null, 2);
      assertStatementsSize(vVCARD.url, example, 2);
    } finally {
      statements.close();
    }
  }

  @Test
  public void testAreaFull() throws Exception {
    assertExtract("/microformats/hcard/33-area.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 5);

    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);
    while (statements.hasNext()) {
      Resource vcard = statements.next().getSubject();
      final Value fnValue = findObject(vcard, vVCARD.fn);
      assertNotNull(fnValue);
      String fn = fnValue.stringValue();
      final Value vcardValue = findObject(vcard, vVCARD.url);
      assertNotNull(vcardValue);
      String url = vcardValue.stringValue();
      final Value emailValue = findObject(vcard, vVCARD.email);
      assertNotNull(emailValue);
      String mail = emailValue.stringValue();
      assertEquals("Joe Public", fn);
      assertEquals("http://example.com/", url);
      assertEquals("mailto:joe@example.com", mail);
    }
  }

  @Test
  public void testCategories() throws Exception {
    assertExtract("/microformats/hcard/36-categories.html");
    assertModelNotEmpty();
    assertContains(vVCARD.given_name, "Joe");
    assertContains(vVCARD.given_name, "john");
    assertContains(vVCARD.family_name, "doe");
    assertContains(vVCARD.family_name, "User");
    assertContains(vVCARD.fn, "john doe");
    assertContains(vVCARD.fn, "Joe User");

    assertContains(vVCARD.category, "C1");
    assertContains(vVCARD.category, "C2a");
    assertContains(vVCARD.category, "C4");
    assertContains(vVCARD.category, "User");
    String[] cats = { "C3", "C5", "C6", "C7", "C9", "luser", "D1", "D2",
    "D3" };
    for (String cat : cats)
      assertContains(vVCARD.category, "http://example.com/tag/" + cat);

    assertNotContains(null, vVCARD.category, "D4");
  }

  @Test
  public void testSingleton() throws Exception {
    // this tests probably tests that e just get the first fn and so on
    assertExtract("/microformats/hcard/37-singleton.html");
    assertModelNotEmpty();
    assertStatementsSize(vVCARD.fn, (Value) null, 1);
    assertContains(vVCARD.fn, "john doe 1");

    assertStatementsSize(RDF.TYPE, vVCARD.Name, 1);
    assertStatementsSize(vVCARD.given_name, (Value) null, 1);
    assertContains(vVCARD.given_name, "john");
    assertStatementsSize(vVCARD.family_name, (Value) null, 1);
    assertContains(vVCARD.family_name, "doe");
    assertStatementsSize(vVCARD.sort_string, (Value) null, 1);
    assertContains(vVCARD.sort_string, "d");

    assertStatementsSize(vVCARD.bday, (Value) null, 1);
    assertContains(vVCARD.bday, "20060707");
    assertStatementsSize(vVCARD.rev, (Value) null, 1);
    assertContains(vVCARD.rev, "20060707");
    assertStatementsSize(vVCARD.class_, (Value) null, 1);
    assertContains(vVCARD.class_, "public");
    assertStatementsSize(vVCARD.tz, (Value) null, 1);
    assertContains(vVCARD.tz, "+0600");

    // Why 0? because the extractor does not look at geo uF!
    assertStatementsSize(RDF.TYPE, vVCARD.Location, 0);
    assertStatementsSize(vVCARD.geo, (Value) null, 2);

    assertNotContains(null, vVCARD.latitude, "123.45");
    assertNotContains(null, vVCARD.longitude, "67.89");

    assertStatementsSize(vVCARD.uid, (Value) null, 1);
    assertContains(vVCARD.uid, "unique-id-1");
  }

  @Test
  public void testUidFull() throws Exception {
    assertExtract("/microformats/hcard/38-uid.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);

    try {
      while (statements.hasNext()) {
        Resource vcard = statements.next().getSubject();
        assertNotNull(findObject(vcard, vVCARD.fn));
        String fn = findObjectAsLiteral(vcard, vVCARD.fn);
        assertEquals("Ryan King", fn);

        assertNotNull(findObject(vcard, vVCARD.n));
        Resource n = findObjectAsResource(vcard, vVCARD.n);
        assertNotNull(n);
        assertNotNull(findObject(n, vVCARD.given_name));
        assertEquals("Ryan",
                findObjectAsLiteral(n, vVCARD.given_name));
        assertNotNull(findObject(n, vVCARD.family_name));
        assertEquals("King",
                findObjectAsLiteral(n, vVCARD.family_name));

        assertNotNull(findObject(vcard, vVCARD.url));
        Resource url = findObjectAsResource(vcard, vVCARD.url);

        assertNotNull(findObject(vcard, vVCARD.uid));
        String uid = findObjectAsLiteral(vcard, vVCARD.uid);

        assertEquals("http://theryanking.com/contact/",
                url.stringValue());
        assertEquals("http://theryanking.com/contact/", uid);
      }
    } finally {
      statements.close();
    }
  }

  @Test
  public void testRomanianWikipedia() throws Exception {
    assertExtract("/microformats/hcard/40-fn-inside-adr.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);
    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);

    try {
      while (statements.hasNext()) {
        Resource card = statements.next().getSubject();
        assertNotNull(findObject(card, vVCARD.fn));
        String fn = findObjectAsLiteral(card, vVCARD.fn);
        assertEquals("Berlin", fn);

        assertNotNull(findObject(card, vVCARD.org));
        Resource org = findObjectAsResource(card, vVCARD.org);
        assertContains(org, RDF.TYPE, vVCARD.Organization);
        assertNotNull(org);
        assertNotNull(findObject(card, vVCARD.org));
        assertNotNull(findObject(org, vVCARD.organization_name));
        assertEquals("Berlin",
                findObjectAsLiteral(org, vVCARD.organization_name));

      }
    } finally {
      statements.close();
    }
  }

  @Test
  public void testNoMicroformats() throws Exception, IOException,
  ExtractionException {
    extract("/html/html-without-uf.html");
    assertModelEmpty();
  }

  @Test
  public void testBasic() throws Exception {
    assertExtract("/microformats/hcard/01-tantek-basic.html");
    assertModelNotEmpty();
    assertContains(RDF.TYPE, vVCARD.VCard);
    // assertContains(RDF.TYPE, vVCARD.Organization);
    assertContains(RDF.TYPE, vVCARD.Name);
    // assertContains(vVCARD.organization_name, "Technorati");
    Resource person = findExactlyOneBlankSubject(vVCARD.fn,
            RDFUtils.literal("Tantek Celik"));
    assertNotNull(person);
    Resource org = findExactlyOneBlankSubject(vVCARD.organization_name,
            RDFUtils.literal("Technorati"));
    assertNotNull(org);
    assertContains(person, vVCARD.url, RDFUtils.iri("http://tantek.com/"));
    assertContains(person, vVCARD.n, (Resource) null);
    assertContains(person, vVCARD.org, (Resource) null);
  }

  @Test
  public void testMultipleclassNamesOnVCard() throws Exception {
    assertExtract("/microformats/hcard/02-multiple-class-names-on-vcard.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
    Resource name;
    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);
    while (statements.hasNext()) {
      name = statements.next().getSubject();
      assertContains(name, vVCARD.fn, "Ryan King");
    }
  }

  @Test
  public void testImpliedNames() throws Exception {
    String[] ns = { "Ryan King", "King", "Ryan",

            "Ryan King", "King", "Ryan",

            "Ryan King", "King", "Ryan",

            "Brian Suda", "Suda", "Brian",

            "King, Ryan", "King", "Ryan",

            "King, R", "King", "R",

            "King R", "R", "King",

            "R King", "King", "R",

            "King R.", "R.", "King",

            "Jesse James Garrett", "Garrett", "Jesse",

            "Thomas Vander Wall", "Wall", "Thomas" };
    List<String> NAMES = Arrays.asList(ns);
    assertExtract("/microformats/hcard/03-implied-n.html");
    assertModelNotEmpty();

    RepositoryResult<Statement> statements = getStatements(null, vVCARD.fn,
            null);
    Resource vcard;
    int count = 0;
    try {
      while (statements.hasNext()) {
        vcard = statements.next().getSubject();
        assertContains(vcard, RDF.TYPE, vVCARD.VCard);
        Resource name = findObjectAsResource(vcard, vVCARD.n);

        final String objLiteral = findObjectAsLiteral(vcard, vVCARD.fn);
        int idx = NAMES.indexOf(objLiteral);
        assertTrue(
                String.format("not in names: '%s'", objLiteral),
                idx >= 0);
        assertEquals(NAMES.get(idx + 1),
                findObjectAsLiteral(name, vVCARD.family_name));
        assertEquals(NAMES.get(idx + 2),
                findObjectAsLiteral(name, vVCARD.given_name));
        count++;
      }
    } finally {
      statements.close();
    }
    assertEquals(10, count);
  }

  @Test
  public void testIgnoreUnknowns() throws Exception {
    assertExtract("/microformats/hcard/04-ignore-unknowns.html");
    assertDefaultVCard();
    assertContains(vVCARD.fn, "Ryan King");
    assertContains(vVCARD.n, (Resource) null);
    assertContains(null, "Ryan");
    assertContains(vVCARD.given_name, "Ryan");
    assertContains(vVCARD.family_name, "King");
  }

  @Test
  public void testMailto1() throws Exception {
    assertExtract("/microformats/hcard/05-mailto-1.html");
    assertDefaultVCard();
    assertContains(vVCARD.fn, "Ryan King");
    assertContains(RDF.TYPE, vVCARD.Name);

    assertContains(vVCARD.email, RDFUtils.iri("mailto:ryan@technorati.com"));

    assertContains(vVCARD.given_name, "Ryan");
    assertContains(vVCARD.family_name, "King");
  }

  @Test
  public void testMailto2() throws Exception {
    assertExtract("/microformats/hcard/06-mailto-2.html");
    assertDefaultVCard();
    assertContains(vVCARD.fn, "Brian Suda");

    assertContains(vVCARD.email, RDFUtils.iri("mailto:brian@example.com"));
    assertContains(vVCARD.given_name, "Brian");
    assertContains(vVCARD.family_name, "Suda");
  }

  @Test
  public void testRelativeUrl() throws Exception {
    assertExtract("/microformats/hcard/07-relative-url.html");
    assertDefaultVCard();
    assertJohn();
    assertContains(vVCARD.url, RDFUtils.iri(baseIRI + "home/blah"));
  }

  @Test
  public void testRelativeUrlBase() throws Exception {
    assertExtract("/microformats/hcard/08-relative-url-base.html");
    assertDefaultVCard();
    assertContains(vVCARD.url, RDFUtils.iri(baseIRI + "home/blah"));
    assertJohn();
  }

  @Test
  public void testRelativeUrlXmlBase1() throws Exception {
    assertExtract("/microformats/hcard/09-relative-url-xmlbase-1.html");
    assertDefaultVCard();
    assertContains(vVCARD.url, RDFUtils.iri((baseIRI + "home/blah")));
    assertJohn();
  }

  @Test
  public void testRelativeUrlXmlBase2() throws Exception {
    assertExtract("/microformats/hcard/10-relative-url-xmlbase-2.html");
    assertDefaultVCard();
    assertContains(vVCARD.url, RDFUtils.iri((baseIRI + "home/blah")));
    assertJohn();
  }

  @Test
  public void testMultipleUrls() throws Exception {
    assertExtract("/microformats/hcard/11-multiple-urls.html");
    assertDefaultVCard();
    assertContains(vVCARD.url, RDFUtils.iri(("http://example.com/foo")));
    assertContains(vVCARD.url, RDFUtils.iri(("http://example.com/bar")));

    assertJohn();
  }

  @Test
  public void testImageSrc() throws Exception {
    assertExtract("/microformats/hcard/12-img-src-url.html");
    assertDefaultVCard();
    assertJohn();
  }

  @Test
  public void testPhotoLogo() throws Exception {
    assertExtract("/microformats/hcard/13-photo-logo.html");
    assertDefaultVCard();
    assertContains(vVCARD.photo,
            RDFUtils.iri(("http://example.org/picture1.png")));
    assertContains(vVCARD.photo,
            RDFUtils.iri(("http://example.org/picture2.png")));
    assertContains(vVCARD.logo,
            RDFUtils.iri(("http://example.org/picture1.png")));
    assertContains(vVCARD.logo,
            RDFUtils.iri(("http://example.org/picture2.png")));
    assertJohn();
  }

  @Test
  public void testImgSrcDataUrl() throws Exception {
    assertExtract("/microformats/hcard/14-img-src-data-url.html");
    assertDefaultVCard();
    Resource data = RDFUtils.iri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAMAAAAp4XiDAAAABGdBTUEAAK/"
            + "INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAASUExURf///8zMzJmZmWZmZ"
            + "jMzMwAAAPOPemkAAAM1SURBVHjaYmBgYGBkYQUBFkYWFiCPCchixQAMCCZAACF0MAMVM4K4TFh0IGsBCC"
            + "AkOxhYmBnAAKaHhZkZmxaAAGJgYIbpYGBihGgBWsTMzMwE4jIhaWGAYoAAYmCECDExYAcwGxkg5oNIgAB"
            + "igDqLARdgZmGB2wICrKwAAcSA3xKgIxlZ0PwCEEAMBCxhgHoWSQtAADFAAxgfYEJ1GEAAQbQw4tUCsocB"
            + "YQVAADEgu4uRkREeUCwszEwwLhOKLQABhNDCBA4aSDgwwhIAJKqYUPwCEEAMUK/AUwnc9aywJMCI7DAgA"
            + "AggBohZ8JTBhGIJzCoWZL8ABBCYidAB8RUjWppkYUG2BSCAGMDqEMZiswUtXgACiAHsFYixTMywGGLGpgU"
            + "WYgABxAA2mQkWCMyMqFoYmdD8ACQAAogBHJHMrCxg1cyIiICmCkYWDFsAAgiihYmZCewFFpR0BfI3LLch+"
            + "QUggBiQ0iQjEyMDmh54qCBlUIAAYsCRJsElADQvgWKTlRGeKwECiAF3XgGmMEYQYADZzcoA9z5AAMG9RQC"
            + "AtEC9DxBADFiyFyMjVi0wABBAWLQwQdIiuhYGWJIACCBg+KKUJ9BoBRdS2LQALQMIIGDQIEmwAO1kYcVWH"
            + "CDZAhBAqFqYmOAxj2YNtAwDAYAAYmDEiBYWzHKKkRERYiwAAYSphZEZwxZGZiZQVEJTJkAAMTCyokc7M5o"
            + "ORlC5wcoEjxeAAAJqQXU0UB6W5WFmABMtEzMi1wEEEFAbE0YyAUuzMMEsYQalMkQSBQggUDmNPU3C9IA4L"
            + "CxI+QUggEBiKOU8yExgqccCL3chnkPKlQABhGo6ejHBDKmdUHMlQAAhhQvQaGZGkBIkjcAMywLmI+VKgAB"
            + "CSowsTJhZkhlWXiBpAQggYBqBZl9GVOdBcz0LZqEEEEAMqLULMBLg1THWog9IAwQQA0qiZcRW5aPbAhBAD"
            + "Cg1El4tMAAQQAxoiZYZXnTh1AIQQAzo2QlYpDDjcBgrxGEAAcSAJTthswmiBUwDBBC2GpkZJTaRvQ+mAQK"
            + "IAUuuxdZWQvILQABBmSxMjBj5EpcWgACCMoFOYYSpZyHQHgMIMACt2hmoVEikCQAAAABJRU5ErkJggg==");

    assertContains(vVCARD.photo, data);
    assertContains(vVCARD.logo, data);
    assertJohn();
  }

  @Test
  public void testHonorificAdditionalSingle() throws Exception {
    assertExtract("/microformats/hcard/15-honorific-additional-single.html");
    assertDefaultVCard();
    assertContains(vVCARD.fn, "Mr. John Maurice Doe, Ph.D.");

    assertContains(vVCARD.honorific_prefix, "Mr.");
    assertContains(vVCARD.honorific_suffix, "Ph.D.");

    assertContains(vVCARD.given_name, "John");
    assertContains(vVCARD.additional_name, "Maurice");
    assertContains(vVCARD.family_name, "Doe");
  }

  @Test
  public void testHonorificAdditionalMultiple() throws Exception {
    assertExtract("/microformats/hcard/16-honorific-additional-multiple.html");
    assertDefaultVCard();
    assertContains(vVCARD.honorific_prefix, "Mr.");
    assertContains(vVCARD.honorific_prefix, "Dr.");

    assertContains(vVCARD.honorific_suffix, "Ph.D.");
    assertContains(vVCARD.honorific_suffix, "J.D.");

    assertContains(vVCARD.given_name, "John");
    assertContains(vVCARD.additional_name, "Maurice");
    assertContains(vVCARD.additional_name, "Benjamin");
    assertContains(vVCARD.family_name, "Doe");

    assertContains(vVCARD.fn,
            "Mr. Dr. John Maurice Benjamin Doe Ph.D., J.D.");
  }

  @Test
  public void testEMailNotUri() throws Exception {
    assertExtract("/microformats/hcard/17-email-not-uri.html");
    assertDefaultVCard();
    assertJohn();
    assertContains(vVCARD.email, RDFUtils.iri("mailto:john@example.com"));
  }

  @Test
  public void testObjectDataHttpUri() throws Exception {
    assertExtract("/microformats/hcard/18-object-data-http-uri.html");
    assertDefaultVCard();
    assertJohn();
  }

  @Test
  public void testObjectDataDataUri() throws Exception {
    assertExtract("/microformats/hcard/19-object-data-data-uri.html");
    assertDefaultVCard();
    assertJohn();

    assertContains(vVCARD.photo, (Resource) null);
    assertContains(vVCARD.logo, (Resource) null);
  }

  @Test
  public void testImgAlt() throws Exception {
    assertExtract("/microformats/hcard/20-image-alt.html");
    assertDefaultVCard();
    Resource uri = RDFUtils.iri("http://example.com/foo.png");
    assertContains(vVCARD.photo, uri);
    assertContains(vVCARD.logo, uri);
    assertJohn();
  }

  @Test
  public void testAdr() throws Exception {
    assertExtract("/microformats/hcard/22-adr.html");
    assertDefaultVCard();
    assertJohn();
    assertStatementsSize(RDF.TYPE, vVCARD.Address, 0);
  }

  @Test
  public void testBirthDayDate() throws Exception {
    assertExtract("/microformats/hcard/27-bday-date.html");
    assertModelNotEmpty();
    assertContains(vVCARD.fn, "john doe");
    assertContains(vVCARD.given_name, "john");
    assertContains(vVCARD.family_name, "doe");
    assertContains(vVCARD.bday, "2000-01-01");
  }

  @Test
  public void testBirthDayDateTime() throws Exception {
    assertExtract("/microformats/hcard/28-bday-datetime.html");
    assertModelNotEmpty();
    assertContains(vVCARD.fn, "john doe");
    assertContains(vVCARD.given_name, "john");
    assertContains(vVCARD.family_name, "doe");
    assertContains(vVCARD.bday, "2000-01-01T00:00:00");
  }

  @Test
  public void testBirthDayDateTimeTimeZone() throws Exception {
    assertExtract("/microformats/hcard/29-bday-datetime-timezone.html");
    assertModelNotEmpty();
    assertContains(vVCARD.fn, "john doe");
    assertContains(vVCARD.given_name, "john");
    assertContains(vVCARD.family_name, "doe");
    assertContains(vVCARD.bday, "2000-01-01T00:00:00-0800");
  }

  @Test
  public void testArea() throws Exception {
    assertExtract("/microformats/hcard/33-area.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 5);
    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);
    try {
      while (statements.hasNext()) {
        Resource vcard = statements.next().getSubject();

        assertNotNull(findObject(vcard, vVCARD.fn));
        assertEquals("Joe Public",
                findObjectAsLiteral(vcard, vVCARD.fn));
        assertNotNull(findObject(vcard, vVCARD.url));
        String url = findObjectAsLiteral(vcard, vVCARD.url);
        assertNotNull(findObject(vcard, vVCARD.email));
        String mail = findObjectAsLiteral(vcard, vVCARD.email);
        assertEquals("http://example.com/", url);
        assertEquals("mailto:joe@example.com", mail);
      }
    } finally {
      statements.close();
    }

    // Check that there are 4 organizations.
    assertStatementsSize(RDF.TYPE, vVCARD.Organization, 4);
    statements = getStatements(null, RDF.TYPE, vVCARD.Organization);
    try {
      while (statements.hasNext()) {
        Resource org = statements.next().getSubject();
        assertContains(null, vVCARD.org, org);
        assertNotNull(findObject(org, vVCARD.organization_name));
        assertEquals("Joe Public",
                findObjectAsLiteral(org, vVCARD.organization_name));
      }
    } finally {
      statements.close();
    }
  }

  @Test
  public void testNotes() throws Exception {
    final String[] NOTES = { "Note 1", "Note 3",
    "Note 4 with a ; and a , to be escaped" };

    assertExtract("/microformats/hcard/34-notes.html");
    assertModelNotEmpty();
    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.VCard);
    try {
      while (statements.hasNext()) {
        Resource vcard = statements.next().getSubject();
        String fn = findObjectAsLiteral(vcard, vVCARD.fn);
        String mail = findObjectAsLiteral(vcard, vVCARD.email);
        assertEquals("Joe Public", fn);
        assertEquals("mailto:joe@example.com", mail);
      }
    } finally {
      statements.close();
    }
    for (String note : NOTES) {
      assertContains(vVCARD.note, note);
    }
  }

  @Test
  public void testIncludePattern() throws Exception {
    assertExtract("/microformats/hcard/35-include-pattern.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 3);

    RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE,
            vVCARD.Name);
    try {
      while (statements.hasNext()) {
        Resource name = statements.next().getSubject();
        assertNotNull(findObject(name, vVCARD.given_name));
        String gn = findObjectAsLiteral(name, vVCARD.given_name);
        assertEquals("James", gn);
        assertNotNull(findObject(name, vVCARD.family_name));
        String fn = findObjectAsLiteral(name, vVCARD.family_name);
        assertEquals("Levine", fn);
      }
    } finally {
      statements.close();
    }

    assertStatementsSize(RDF.TYPE, vVCARD.Organization, 2);
    statements = getStatements(null, RDF.TYPE, vVCARD.Organization);
    try {
      while (statements.hasNext()) {
        Resource org = statements.next().getSubject();
        assertNotNull(findObject(org, vVCARD.organization_name));
        assertEquals("SimplyHired",
                findObjectAsLiteral(org, vVCARD.organization_name));

        RepositoryResult<Statement> statements2 = getStatements(null,
                vVCARD.org, org);
        try {
          while (statements2.hasNext()) {
            Resource vcard = statements2.next().getSubject();
            assertNotNull(findObject(vcard, vVCARD.title));
            assertEquals("Microformat Brainstormer",
                    findObjectAsLiteral(vcard, vVCARD.title));
          }
        } finally {
          statements2.close();
        }
      }
    } finally {
      statements.close();
    }
  }

  @Test
  public void testUid() throws Exception {
    assertExtract("/microformats/hcard/38-uid.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
    RepositoryResult<Statement> iter = getStatements(null, RDF.TYPE,
            vVCARD.VCard);
    while (iter.hasNext()) {
      Resource vcard = iter.next().getSubject();
      assertNotNull(findObject(vcard, vVCARD.fn));
      String fn = findObjectAsLiteral(vcard, vVCARD.fn);
      assertNotNull(findObject(vcard, vVCARD.url));
      String url = findObjectAsLiteral(vcard, vVCARD.url);
      assertNotNull(findObject(vcard, vVCARD.uid));
      String uid = findObjectAsLiteral(vcard, vVCARD.uid);
      assertEquals("Ryan King", fn);
      assertEquals("http://theryanking.com/contact/", url);
      assertEquals("http://theryanking.com/contact/", uid);

    }
  }

  @Test
  public void testIgnoreChildren() throws Exception {
    assertExtract("/microformats/hcard/41-ignore-children.html");
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);
    assertContains(vVCARD.fn, "Melanie Kl\u00f6\u00df");
    assertContains(vVCARD.email, RDFUtils.iri("mailto:mkloes@gmail.com"));
    assertContains(vVCARD.adr, (Resource) null);
    assertNotContains(null, vVCARD.postal_code, "53127");
    assertNotContains(null, vVCARD.locality, "Bonn");
    assertNotContains(null, vVCARD.street_address, "Ippendorfer Weg. 24");
    assertNotContains(null, vVCARD.country_name, "Germany");
  }

  /**
   * Tests that the HCardName data is not cumulative and is cleaned up at each
   * extraction.
   *
   * @throws Exception if there is an error asserting the test data.
   */
  @Test
  public void testCumulativeHNames() throws Exception {
    assertExtract("/microformats/hcard/linkedin-michelemostarda.html");
    assertModelNotEmpty();
    assertStatementsSize(vVCARD.given_name, "Michele", 7);
    assertStatementsSize(vVCARD.family_name, "Mostarda", 7);
  }

  /**
   * Tests the detection and prevention of the inclusion of an ancestor by a
   * sibling node. This test is related to issue <a
   * href="https://issues.apache.org/jira/browse/ANY23-58">ANY23-58</a>.
   *
   * @throws IOException if there is an error interpreting the input data
   * @throws ExtractionException if there is an exception during extraction
   */
  @Test
  public void testInfiniteLoop() throws IOException, ExtractionException {
    assertExtract("/microformats/hcard/infinite-loop.html", false);
    assertIssue(IssueReport.IssueLevel.WARNING,
            ".*Current node tries to include an ancestor node.*");
  }

  /**
   * Tests extractor performances. This test is related to issue <a
   * href="https://issues.apache.org/jira/browse/ANY23-76">ANY23-76</a>.
   */
  @Test(timeout = 30 * 1000)
  public void testExtractionPerformance() {
    assertExtract("/microformats/hcard/performance.html");
  }

  private void assertDefaultVCard() throws Exception {
    assertModelNotEmpty();
    assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);
  }

  private void assertJohn() throws Exception {
    assertContains(vVCARD.fn, "John Doe");
    assertContains(vVCARD.given_name, "John");
    assertContains(vVCARD.family_name, "Doe");
  }

}
