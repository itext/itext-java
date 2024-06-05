package com.itextpdf.layout.properties.grid;

/**
 * Represents min-content template value.
 */
public final class MinContentValue extends BreadthValue {
    /**
     * min-content value.
     */
    public static final MinContentValue VALUE = new MinContentValue();

    private MinContentValue() {
        super(ValueType.MIN_CONTENT);
    }
}
