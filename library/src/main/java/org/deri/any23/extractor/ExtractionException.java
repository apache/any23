package org.deri.any23.extractor;

// TODO: ExtractionException Should receive an ExtractionContext?
@SuppressWarnings("serial")
public class ExtractionException extends Exception {

	public ExtractionException(String message) {
		super(message);
	}
	
	public ExtractionException(Throwable cause) {
		super(cause);
	}
	
	public ExtractionException(String message, Throwable cause) {
		super(message, cause);
	}
}
