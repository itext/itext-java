package com.itextpdf.layout.properties.grid;

/**
 * Represents a breadth value on a grid.
 */
public abstract class BreadthValue extends GridValue {
    /**
     * Init a breadth value with a given type
     *
     * @param type value type
     */
    protected BreadthValue(ValueType type) {
        super(type);
    }
}
