package com.itextpdf.layout.properties.grid;

/**
 * Represents percent template value.
 */
public class PercentValue extends LengthValue {

    /**
     * Creates percent value.
     *
     * @param value percent value
     */
    public PercentValue(float value) {
        super(ValueType.PERCENT, value);
    }
}
