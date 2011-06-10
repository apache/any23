package org.deri.any23.extractor;

import org.deri.any23.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class models the parameters to be used to perform an extraction.
 *
 * @see org.deri.any23.Any23
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionParameters {

    private final boolean validate;

    private final boolean fix;

    private final Map<String, Boolean> flags;

    /**
     * Constructor.
     *
     * @param validate if <code>true</code> validation will be performed.
     * @param fix if <code>true</code> fixes will be applied.
     * @param flags list of values for extraction flags.
     */
    public ExtractionParameters(boolean validate, boolean fix, Map<String, Boolean> flags) {
        if(fix && !validate) {
            throw new IllegalArgumentException("Cannot enable the fix flag without the validate flag.");
        }
        this.validate = validate;
        this.fix      = fix;
        this.flags    = Collections.unmodifiableMap( flags == null ? Collections.<String,Boolean>emptyMap() : flags );
    }

    /**
     * Constructor.
     *
     * @param validate if <code>true</code> validation will be performed.
     * @param fix if <code>true</code> fixes will be applied.
     */
    public ExtractionParameters(boolean validate, boolean fix) {
        this(validate, fix, null);
    }

    /**
     * Constructor, allows to set explicitly the value for flag
     * {@link SingleDocumentExtraction#METADATA_NESTING_FLAG}.
     *
     * @param validate if <code>true</code> validation will be performed.
     * @param fix if <code>true</code> fixes will be applied.
     * @param nesting if <code>true</code> nesting triples will be expressed.
     */
    public ExtractionParameters(boolean validate, boolean fix, final boolean nesting) {
        this(
                validate,
                fix,
                new HashMap<String, Boolean>(){{
                    put(SingleDocumentExtraction.METADATA_NESTING_FLAG, nesting);
                }}
        );
    }

    /**
     * @return <code>true</code> if validation is active.
     */
    public boolean isValidate() {
        return validate;
    }

    /**
     * @return <code>true</code> if fix is active.
     */
    public boolean isFix() {
        return fix;
    }

    /**
     * Returns the value of the flag, if the flag is undefined
     * it will be retrieved by the default configuration.
     *
     * @param flagName name of flag.
     * @return flag value.
     */
    public boolean getFlag(String flagName) {
        final Boolean value = flags.get(flagName);
        if(value == null) {
            return Configuration.instance().getFlagProperty(flagName);
        }
        return value;
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
            return validate == other.validate && fix == other.fix;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (validate ? 1 : 1) * (fix ? 2 : 2);
    }
}
