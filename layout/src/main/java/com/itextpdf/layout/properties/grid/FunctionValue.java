package com.itextpdf.layout.properties.grid;

/**
 * Abstract class representing function value on a grid.
 */
public abstract class FunctionValue extends GridValue {
    /**
     * Init a function value with a given type.
     *
     * @param type value type
     */
    protected FunctionValue(ValueType type) {
        super(type);
    }
}
