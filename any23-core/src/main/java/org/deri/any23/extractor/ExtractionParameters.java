package org.deri.any23.extractor;

/**
 * This class models the parameters to be used to perform an extraction.
 *
 * @see org.deri.any23.Any23
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionParameters {

    private boolean validate;

    private boolean fix;

    private boolean nestingEnabled;

    public ExtractionParameters(boolean validate, boolean fix) {
        if(fix && !validate) {
            throw new IllegalArgumentException("Cannot enable the fix flag without the validate flag.");
        }
        this.validate = validate;
        this.fix = fix;
        this.nestingEnabled = true;
    }

    public ExtractionParameters(boolean validate, boolean fix, boolean nestingEnabled) {
        if(fix && !validate) {
            throw new IllegalArgumentException("Cannot enable the fix flag without the validate flag.");
        }
        this.validate = validate;
        this.fix = fix;
        this.nestingEnabled = nestingEnabled;
    }

    public boolean isValidate() {
        return validate;
    }

    public boolean isFix() {
        return fix;
    }

    public boolean isNestingEnabled() {
        return nestingEnabled;
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
            return validate == other.validate && fix == other.fix && nestingEnabled == other.nestingEnabled;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (validate ? 0 : 1) * (fix ? 0 : 2) * (nestingEnabled ? 0 : 3);
    }
}
