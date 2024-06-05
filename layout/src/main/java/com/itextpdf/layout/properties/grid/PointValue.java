package com.itextpdf.layout.properties.grid;

/**
 * Represents point template value.
 */
public class PointValue extends LengthValue {
    /**
     * Creates point value with a given length.
     *
     * @param value length value
     */
    public PointValue(float value) {
        super(ValueType.POINT, value);
    }
}
