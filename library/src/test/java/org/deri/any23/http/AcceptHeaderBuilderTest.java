package org.deri.any23.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.deri.any23.mime.MIMEType;

import junit.framework.TestCase;

public class AcceptHeaderBuilderTest extends TestCase {

	public void testEmpty() {
		assertNull(buildHeader(Collections.<String>emptyList()));
	}
	
	public void testSingleHeaderSpecific() {
		assertEquals("text/html",
				buildHeader(Arrays.asList("text/html")));
	}
	
	public void testSingleHeaderSpecificWithQ() {
		assertEquals("text/html;q=0.5",
				buildHeader(Arrays.asList("text/html;q=0.5")));
	}
	
	public void testSuppressQIfEquals1() {
		assertEquals("text/html",
				buildHeader(Arrays.asList("text/html;q=1")));
	}
	
	public void testSingleHeaderSubtypeWildcard() {
		assertEquals("text/*;q=0.5",
				buildHeader(Arrays.asList("text/*;q=0.5")));
	}
	
	public void testSingleHeaderTypeWildcard() {
		assertEquals("*/*;q=0.5",
				buildHeader(Arrays.asList("*/*;q=0.5")));
	}
	
	public void testMultipleIndependentHeaders() {
		assertEquals("image/jpeg;q=0.2, text/html, text/plain;q=0.5",
				buildHeader(Arrays.asList(
						"image/jpeg;q=0.2", "text/html;q=1.0", "text/plain;q=0.5")));
	}
	
	public void testHighestSpecificValueIsChosen() {
		assertEquals("image/jpeg",
				buildHeader(Arrays.asList(
						"image/jpeg;q=0.2", "image/jpeg")));
		assertEquals("image/jpeg",
				buildHeader(Arrays.asList(
						"image/jpeg", "image/jpeg;q=0.2")));
	}
	
	public void testHighestSubtypeWildcardIsChosen() {
		assertEquals("image/*",
				buildHeader(Arrays.asList(
						"image/*;q=0.2", "image/*")));
		assertEquals("image/*",
				buildHeader(Arrays.asList(
						"image/*", "image/*;q=0.2")));
	}
	
	public void testHighestTypeWildcardIsChosen() {
		assertEquals("*/*",
				buildHeader(Arrays.asList(
						"*/*;q=0.2", "*/*")));
		assertEquals("*/*",
				buildHeader(Arrays.asList(
						"*/*", "*/*;q=0.2")));
	}
	
	public void testTypeWildcardSuppressesLowerValues() {
		assertEquals("*/*;q=0.5",
				buildHeader(Arrays.asList(
						"*/*;q=0.5", "image/*;q=0.2")));
		assertEquals("*/*;q=0.5",
				buildHeader(Arrays.asList(
						"*/*;q=0.5", "image/jpeg;q=0.2")));
	}
	
	public void testSubtypeWildcardSuppressesLowerValues() {
		assertEquals("image/*;q=0.5",
				buildHeader(Arrays.asList(
						"image/*;q=0.5", "image/jpeg;q=0.2")));
	}
	
	private String buildHeader(Collection<String> mimeTypes) {
		Collection<MIMEType> parsedTypes = new ArrayList<MIMEType>();
		for (String s: mimeTypes) {
			parsedTypes.add(MIMEType.parse(s));
		}
		return new AcceptHeaderBuilder(parsedTypes).getAcceptHeader();
	}
}
