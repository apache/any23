package org.deri.any23.rdf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A mapping from prefixes to namespace URIs. Supports “volatile mappings”,
 * which will be overwritten without notice when mappings are merged,
 * while for normal mappings this causes an exception. This allows
 * combining “hard” mappings (which must be retained or something breaks)
 * and “soft” mappings (which might be read from input RDF files and
 * should be retained only if they are not in conflict with the hard
 * ones).
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Prefixes {
	
	public static Prefixes create1(String prefix, String namespaceURI) {
		Prefixes result = new Prefixes();
		result.add(prefix, namespaceURI);
		return result;
	}
	
	public static Prefixes createFromMap(Map<String, String> prefixesToNamespaceURIs, boolean areVolatile) {
		Prefixes result = new Prefixes();
		for (Entry<String, String> entry: prefixesToNamespaceURIs.entrySet()) {
			if (areVolatile) {
				result.addVolatile(entry.getKey(), entry.getValue());
			} else {
				result.add(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
	
	public static Prefixes EMPTY = new Prefixes(Collections.<String, String>emptyMap());
	
	private final Map<String, String> mappings;
	private final Set<String> volatilePrefixes = new HashSet<String>();
	
	public Prefixes() {
		this(new HashMap<String, String>());
	}
	
	public Prefixes(Prefixes initial) {
		this();
		add(initial);
	}
	
	private Prefixes(Map<String, String> mappings) {
		this.mappings = mappings;
	}
	
	public String expand(String curie) {
		String prefix = parsePrefix(curie);
		if (prefix == null || !hasPrefix(prefix)) {
			return null;
		}
		return getNamespaceURIFor(prefix) + parseLocalName(curie);
	}
	
	public String abbreviate(String uri) {
		for (Entry<String, String> namespace: mappings.entrySet()) {
			if (uri.startsWith(namespace.getValue())) {
				return namespace.getKey() + ":" + 
						uri.substring(namespace.getValue().length());
			}
		}
		return null;
	}

	public boolean canExpand(String curie) {
		String prefix = parsePrefix(curie);
		return prefix != null && hasPrefix(prefix);
	}

	public boolean canAbbreviate(String uri) {
		for (Entry<String, String> namespace: mappings.entrySet()) {
			if (uri.startsWith(namespace.getValue())) {
				return true;
			}
		}
		return false;
	}

	public String getNamespaceURIFor(String prefix) {
		return mappings.get(prefix);
	}
	
	public boolean hasNamespaceURI(String uri) {
		return mappings.containsValue(uri);
	}

	public boolean hasPrefix(String prefix) {
		return mappings.containsKey(prefix);
	}

	public Set<String> allPrefixes() {
		return mappings.keySet();
	}

	public boolean isEmpty() {
		return mappings.isEmpty();
	}
	
	public void add(String prefix, String namespaceURI) {
		if (isVolatile(prefix)) {
			volatilePrefixes.remove(prefix);
		} else {
			if (hasPrefix(prefix)) {
				if (getNamespaceURIFor(prefix).equals(namespaceURI)) {
					return;	// re-assigned same prefix to same URI, let's just ignore it
				}
				throw new IllegalStateException("Attempted to re-assign prefix '" + prefix + 
						"'; clashing values '" + getNamespaceURIFor(prefix) + "' and '" +
						namespaceURI);
			}
		}
		mappings.put(prefix, namespaceURI);
	}
	
	public void add(Prefixes other) {
		for (String otherPrefix: other.allPrefixes()) {
			if (other.isVolatile(otherPrefix)) {
				addVolatile(otherPrefix, other.getNamespaceURIFor(otherPrefix));
			} else {
				add(otherPrefix, other.getNamespaceURIFor(otherPrefix));
			}
		}
	}

	public void removePrefix(String prefix) {
		mappings.remove(prefix);
		volatilePrefixes.remove(prefix);
	}
	
	public Prefixes createSubset(String... prefixes) {
		Prefixes result = new Prefixes();
		for (String prefix: prefixes) {
			if (!hasPrefix(prefix)) {
				throw new IllegalArgumentException("No namespace URI declared for prefix " + prefix);
			}
			result.add(prefix, getNamespaceURIFor(prefix));
		}
		return result;
	}
	
	public void addVolatile(String prefix, String namespaceURI) {
		if (hasPrefix(prefix)) {
			return;	// new prefix is volatile, so we don't overwrite the old one
		}
		mappings.put(prefix, namespaceURI);
		volatilePrefixes.add(prefix);
	}
	
	public void addVolatile(Prefixes other) {
		for (String otherPrefix: other.allPrefixes()) {
			addVolatile(otherPrefix, other.getNamespaceURIFor(otherPrefix));
		}
	}

	public boolean isVolatile(String prefix) {
		return volatilePrefixes.contains(prefix);
	}

	private Map<String, String> mapUnmodifiable = null;
	public Map<String, String> asMap() {
		// Optimization: Create the unmodifiable map only once, lazily
		if (mapUnmodifiable == null) {
			mapUnmodifiable = Collections.unmodifiableMap(mappings);
		}
		return mapUnmodifiable;
	}
	
	private String parsePrefix(String curie) {
		int index = curie.indexOf(':');
		if (index == -1) {
			throw new IllegalArgumentException("Not a CURIE: '" + curie + "'");
		}
		return curie.substring(0, index);
	}
	
	private String parseLocalName(String curie) {
		int index = curie.indexOf(':');
		if (index == -1) {
			throw new IllegalArgumentException("Not a CURIE: '" + curie + "'");
		}
		return curie.substring(index + 1);
	}
}
