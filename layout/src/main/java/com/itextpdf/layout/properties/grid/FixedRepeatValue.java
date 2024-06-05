package com.itextpdf.layout.properties.grid;

import java.util.List;

/**
 * This class represents an fixed-repeat template value.
 * This value is preprocessed before grid sizing algorithm so its only exists at template level.
 */
public class FixedRepeatValue extends TemplateValue {
    private final List<GridValue> values;
    private final int repeatCount;

    /**
     * Create a new fixed-repeat value
     *
     * @param repeatCount number of repetitions
     * @param values template values to repeat
     */
    public FixedRepeatValue(int repeatCount, List<GridValue> values) {
        super(ValueType.FIXED_REPEAT);
        this.values = values;
        this.repeatCount = repeatCount;
    }

    /**
     * Gets template values which should be repeated.
     *
     * @return template values list
     */
    public List<GridValue> getValues() {
        return values;
    }

    /**
     * Gets number of template values repetitions.
     *
     * @return number of template values repetitions
     */
    public int getRepeatCount() {
        return repeatCount;
    }
}
