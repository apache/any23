package org.deri.any23.extractor;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionParameters {

    private boolean validate;

    private boolean fix;

    public ExtractionParameters(boolean validate, boolean fix) {
        if(fix && !validate) {
            throw new IllegalArgumentException("Cannot enable the fix flag without the validate flag.");
        }
        this.validate = validate;
        this.fix = fix;
    }

    public boolean isValidate() {
        return validate;
    }

    public boolean isFix() {
        return fix;
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
        return (validate ? 0 : 1)* (fix ? 0 : 2);
    }

}
