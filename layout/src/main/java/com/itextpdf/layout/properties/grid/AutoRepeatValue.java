package com.itextpdf.layout.properties.grid;

import java.util.List;

/**
 * This class represents an auto-repeat template value.
 * This value is preprocessed before grid sizing algorithm so its only exists at template level.
 */
public class AutoRepeatValue extends TemplateValue {
    private final List<GridValue> values;
    private final boolean autoFit;

    /**
     * Create a new auto-repeat value
     *
     * @param autoFit determines whether to shrink flatten template values to match the grid size
     * @param values template values to repeat
     */
    public AutoRepeatValue(boolean autoFit, List<GridValue> values) {
        super(ValueType.AUTO_REPEAT);
        this.values = values;
        this.autoFit = autoFit;
    }

    /**
     * Get template values which should be repeated.
     *
     * @return template values list
     */
    public List<GridValue> getValues() {
        return values;
    }

    /**
     * Determines whether to shrink flatten template values to match the grid size.
     *
     * @return {@code true} if to shrink, {@code false} otherwise
     */
    public boolean isAutoFit() {
        return autoFit;
    }
}
