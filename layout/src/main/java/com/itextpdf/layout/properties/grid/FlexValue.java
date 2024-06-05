package com.itextpdf.layout.properties.grid;

/**
 * Represents flexible template value.
 */
public class FlexValue extends BreadthValue {
    protected float value;

    /**
     * Create new flexible value instance.
     *
     * @param value fraction value
     */
    public FlexValue(float value) {
        super(ValueType.FLEX);
        this.value = value;
    }

    /**
     * Gets fraction value.
     *
     * @return fraction value
     */
    public float getFlex() {
        return value;
    }
}
