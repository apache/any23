package org.deri.any23.mime;

/**
 * A MIME type with an optional q (quality) value.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MIMEType implements Comparable<MIMEType> {
	private final static String msg = "Cannot parse MIME type (expected type/subtype[;q=x.x] format): ";
	
	public static MIMEType parse(String mimeType) {
		if (mimeType == null) return null;
		int i = mimeType.indexOf(';');
		double q = 1.0; 
		if (i > -1) {
			String[] params = mimeType.substring(i + 1).split(";");
			for (String param: params) {
				int i2 = param.indexOf('=');
				if (i2 == -1) continue;
				if (!"q".equals(param.substring(0, i2).trim().toLowerCase())) continue;
				String value = param.substring(i2 + 1);
				try {
					q = Double.parseDouble(value);
				} catch (NumberFormatException ex) {
					continue;
				}
				if (q <= 0.0 || q >= 1.0) {
					q = 1.0;
				}
			}
		} else {
			i = mimeType.length();
		}
		String type = mimeType.substring(0, i);
		int i2 = type.indexOf('/');
		if (i2 == -1) {
			throw new IllegalArgumentException(msg + mimeType);
		}
		String p1 = type.substring(0, i2).trim().toLowerCase();
		String p2 = type.substring(i2 + 1).trim().toLowerCase();
		if ("*".equals(p1)) {
			if (!"*".equals(p2)) {
				throw new IllegalArgumentException(msg + mimeType);
			}
			return new MIMEType(null, null, q);
		}
		if ("*".equals(p2)) {
			return new MIMEType(p1, null, q);
		}
		return new MIMEType(p1, p2, q);
	}

	private final String type;
	private final String subtype;
	private final double q;

	private MIMEType(String type, String subtype, double q) {
		this.type = type;
		this.subtype = subtype;
		this.q = q;
	}
	
	public String getMajorType() {
		return (type == null ? "*" : type);
	}
	
	public String getSubtype() {
		return (subtype == null ? "*" : subtype);
	}
	
	public String getFullType() {
		return getMajorType() + "/" + getSubtype();
	}
	
	public double getQuality() {
		return q;
	}
	
	public boolean isAnyMajorType() {
		return type == null;
	}
	
	public boolean isAnySubtype() {
		return subtype == null;
	}
	
	public String toString() {
		if (q == 1.0) {
			return getFullType();
		}
		return getFullType() + ";q=" + q;
	}
	
	public int compareTo(MIMEType other) {
		return getFullType().compareTo(other.getFullType());
	}
}
