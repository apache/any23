package org.deri.any23;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.deri.any23.Any23;
import org.deri.any23.stream.StringOpener;
import org.deri.any23.writer.NTriplesWriter;

// TODO These tests are not very clever
public class TestVariousParsers extends TestCase {
	private String url;

	public void setUp() throws MalformedURLException {
		url = "http://bob.com";
	}

	public void testTTL() throws Exception {
		assertReads("<a> <b> <c> .");
	}

	public void testN3() throws Exception {
		assertReads("<Bob><brothers>(<Jim><Mark>).");
	}

	public void testNTRIP() throws Exception {
		assertReads(
				"<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> .");
	}

	public void testHTML() throws Exception {
		assertReads("<html><body><div class=\"vcard fn\">Joe</div></body></html>");
	}

	private void assertReads(String content) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Any23 runner = new Any23();
		runner.extract(new StringOpener(content), url, new NTriplesWriter(out));
		String result = out.toString("us-ascii");
		assertNotNull(result);
		assertTrue(result.length() > 10);
	}
}
