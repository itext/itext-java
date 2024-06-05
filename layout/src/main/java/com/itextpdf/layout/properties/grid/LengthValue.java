package com.itextpdf.layout.properties.grid;

/**
 * Abstract class representing length value on a grid.
 */
public abstract class LengthValue extends BreadthValue {
    protected float value;

    /**
     * Gets length value.
     *
     * @return length value
     */
    public float getValue() {
        return value;
    }

    /**
     * Init a breadth value with a given type and value.
     *
     * @param type value type
     * @param value length value
     */
    protected LengthValue(ValueType type, float value) {
        super(type);
        this.value = value;
    }
}
