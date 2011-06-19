package org.deri.any23.extractor;

import org.deri.any23.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * This class models the parameters to be used to perform an extraction.
 *
 * @see org.deri.any23.Any23
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionParameters {

    /**
     * Declares the supported validation actions.
     */
    public enum ValidationMode {
        None,
        Validate,
        ValidateAndFix
    }

    private final ValidationMode extractionMode;

    private final Map<String, Boolean> extractionFlags;

    /**
     * Constructor.
     *
     * @param extractionMode specifies the required extraction mode.
     * @param extractionFlags list of specific flags used for extraction, if not specified they will
     *        be retrieved by the default {@link Configuration}.
     */
    public ExtractionParameters(ValidationMode extractionMode, Map<String, Boolean> extractionFlags) {
        if(extractionMode == null) {
            throw new NullPointerException("Extraction mode cannot be null.");
        }
        this.extractionMode = extractionMode;
        this.extractionFlags =
                extractionFlags == null
                        ?
                 new HashMap<String,Boolean>()
                        :
                new HashMap<String,Boolean>(extractionFlags);
    }

    /**
     * Constructor.
     *
     * @param extractionMode specifies the required extraction mode.
     */
    public ExtractionParameters(ValidationMode extractionMode) {
        this(extractionMode, null);
    }

    /**
     * Constructor, allows to set explicitly the value for flag
     * {@link SingleDocumentExtraction#METADATA_NESTING_FLAG}.
     *
     * @param extractionMode specifies the required extraction mode.
     * @param nesting if <code>true</code> nesting triples will be expressed.
     */
    public ExtractionParameters(ValidationMode extractionMode, final boolean nesting) {
        this(
                extractionMode,
                new HashMap<String, Boolean>(){{
                    put(SingleDocumentExtraction.METADATA_NESTING_FLAG, nesting);
                }}
        );
    }

    /**
     * @return <code>true</code> if validation is active.
     */
    public boolean isValidate() {
        return extractionMode == ValidationMode.Validate || extractionMode == ValidationMode.ValidateAndFix;
    }

    /**
     * @return <code>true</code> if fix is active.
     */
    public boolean isFix() {
        return extractionMode == ValidationMode.ValidateAndFix;
    }

    /**
     * Returns the value of the specified extraction flag, if the flag is undefined
     * it will be retrieved by the default {@link Configuration}.
     *
     * @param flagName name of flag.
     * @return flag value.
     */
    public boolean getFlag(String flagName) {
        final Boolean value = extractionFlags.get(flagName);
        if(value == null) {
            return Configuration.instance().getFlagProperty(flagName);
        }
        return value;
    }

    /**
     * Sets the value for an extraction flag.
     *
     * @param flagName flag name.
     * @param value new flag value.
     * @return the previous flag value.
     */
    public boolean setFlag(String flagName, boolean value) {
        return extractionFlags.put(flagName, value);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof ExtractionParameters) {
            ExtractionParameters other = (ExtractionParameters) obj;
            return extractionMode == other.extractionMode && extractionFlags.equals( other.extractionFlags);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return extractionMode.hashCode() * 2 * extractionFlags.hashCode() * 3;
    }
}
