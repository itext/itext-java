package com.itextpdf.layout.properties.grid;

import com.itextpdf.layout.properties.UnitValue;

/**
 * Represents fit content function template value.
 */
public class FitContentValue extends FunctionValue {
    private LengthValue length;

    /**
     * Create fit content function value based on provided {@link LengthValue} instance.
     *
     * @param length max size value
     */
    public FitContentValue(LengthValue length) {
        super(ValueType.FIT_CONTENT);
        this.length = length;
    }

    /**
     * Create fit content function value based on provided {@link UnitValue} instance.
     *
     * @param length max size value
     */
    public FitContentValue(UnitValue length) {
        super(ValueType.FIT_CONTENT);
        if (length != null) {
            if (length.isPointValue()) {
                this.length = new PointValue(length.getValue());
            } else if (length.isPercentValue()) {
                this.length = new PercentValue(length.getValue());
            }
        }
    }

    /**
     * Get underlying {@link LengthValue} which represents max size on a grid for this value.
     *
     * @return underlying {@link LengthValue} value
     */
    public LengthValue getLength() {
        return length;
    }

    /**
     * Gets the maximum size which the value can take on passed space.
     *
     * @param space the space for which fit-content size will be calculated
     *
     * @return the maximum size of the value on passed space
     */
    public float getMaxSizeForSpace(float space) {
        if (length.getType() == GridValue.ValueType.POINT) {
            return length.getValue();
        } else {
            return length.getValue() / 100 * space;
        }
    }
}
