package com.itextpdf.layout.properties.grid;

/**
 * Represents minmax function template value.
 */
public class MinMaxValue extends FunctionValue {
    private final BreadthValue min;
    private final BreadthValue max;

    /**
     * Create a minmax function with a given values.
     *
     * @param min min value of a track
     * @param max max value of a track
     */
    public MinMaxValue(BreadthValue min, BreadthValue max) {
        super(ValueType.MINMAX);
        this.min = min;
        this.max = max;
    }

    /**
     * Gets min template value
     *
     * @return {@link BreadthValue} instance
     */
    public BreadthValue getMin() {
        return min;
    }

    /**
     * Gets max template value
     *
     * @return {@link BreadthValue} instance
     */
    public BreadthValue getMax() {
        return max;
    }
}
