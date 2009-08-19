package org.deri.any23.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deri.any23.mime.MIMEType;

/**
 * Concatenates a collection of MIME specs in "type/subtype;q=x.x" notation
 * into an HTTP Accept header value, and removes duplicates and types
 * covered by wildcards. For example, if the type list contains "text/*;q=0.5",
 * then "text/plain;q=0.1" in the list will be ignored because it's already
 * covered by the wildcard with a higher q value.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AcceptHeaderBuilder {

	public static AcceptHeaderBuilder fromStrings(Collection<String> typesAsStrings) {
		Collection<MIMEType> types = new ArrayList<MIMEType>(typesAsStrings.size());
		for (String type: typesAsStrings) {
			types.add(MIMEType.parse(type));
		}
		return new AcceptHeaderBuilder(types);
	}
	
	private Collection<MIMEType> mimeTypes;
	private MIMEType highestAnyType = null;
	private Map<String, MIMEType> highestAnySubtype = new HashMap<String, MIMEType>();
	private Map<String, MIMEType> highestSpecificType = new HashMap<String, MIMEType>();

	public AcceptHeaderBuilder(Collection<MIMEType> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	/**
	 * Builds and returns an accept header.
	 * 
	 * @throws IllegalArgumentException if an input MIME type cannot be parsed.
	 */
	public String getAcceptHeader() {
		if (mimeTypes.isEmpty()) return null;
		for (MIMEType mimeType: mimeTypes) {
			add(mimeType);
		}
		removeSpecificTypesCoveredByWildcard();
		removeTypesCoveredByWildcard();
		List<MIMEType> highest = new ArrayList<MIMEType>();
		if (highestAnyType != null) {
			highest.add(highestAnyType);
		}
		highest.addAll(highestAnySubtype.values());
		highest.addAll(highestSpecificType.values());
		Collections.sort(highest);
		StringBuffer result = new StringBuffer();
		Iterator<MIMEType> it = mimeTypes.iterator();
		while (it.hasNext()) {
			MIMEType a = it.next();
			if (!highest.contains(a)) continue;
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append(a);
		}
		return result.toString();
	}
	
	private void add(MIMEType newAccept) {
		if (newAccept.isAnyMajorType()) {
			if (highestAnyType == null || newAccept.getQuality() > highestAnyType.getQuality()) {
				highestAnyType = newAccept;
			}
		} else if (newAccept.isAnySubtype()) {
			if (!highestAnySubtype.containsKey(newAccept.getMajorType()) 
					|| newAccept.getQuality() > highestAnySubtype.get(newAccept.getMajorType()).getQuality()) {
				highestAnySubtype.put(newAccept.getMajorType(), newAccept);
			}
		} else {
			if (!highestSpecificType.containsKey(newAccept.getFullType())
					|| newAccept.getQuality() > highestSpecificType.get(newAccept.getFullType()).getQuality()) {
				highestSpecificType.put(newAccept.getFullType(), newAccept);
			}
		}
	}
	
	private void removeSpecificTypesCoveredByWildcard() {
		for (MIMEType accept: highestSpecificType.values()) {
			if (highestAnySubtype.containsKey(accept.getMajorType()) 
					&& accept.getQuality() <= highestAnySubtype.get(accept.getMajorType()).getQuality()) {
				highestSpecificType.remove(accept.getFullType());
			}
		}
		if (highestAnyType == null) return;
		for (MIMEType accept: highestSpecificType.values()) {
			if (accept.getQuality() <= highestAnyType.getQuality()) {
				highestSpecificType.remove(accept.getFullType());
			}
		}
	}
	
	private void removeTypesCoveredByWildcard() {
		if (highestAnyType == null) return;
		for (MIMEType accept: highestAnySubtype.values()) {
			if (accept.getQuality() <= highestAnyType.getQuality()) {
				highestAnySubtype.remove(accept.getMajorType());
			}
		}
	}
}
