package com.itextpdf.layout.properties.grid;

/**
 * Represents max-content template value.
 */
public final class MaxContentValue extends BreadthValue {
    /**
     * max-content value.
     */
    public static final MaxContentValue VALUE = new MaxContentValue();

    private MaxContentValue() {
        super(ValueType.MAX_CONTENT);
    }
}
