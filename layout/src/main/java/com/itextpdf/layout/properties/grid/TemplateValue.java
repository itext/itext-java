package com.itextpdf.layout.properties.grid;

/**
 * A class that indicates its descendant class can be used as a grid template value.
 */
public abstract class TemplateValue {
    /**
     * Template value type.
     */
    protected final ValueType type;

    /**
     * Creates template value with a given type.
     *
     * @param type template value type
     */
    protected TemplateValue(ValueType type) {
        this.type = type;
    }

    /**
     * Gets template value type.
     *
     * @return template value type
     */
    public ValueType getType() {
        return type;
    }

    /**
     * Enum of sizing value types.
     */
    public enum ValueType {
        /**
         * Type which represents absolute point value.
         */
        POINT,
        /**
         * Type which represents relative percent value.
         */
        PERCENT,
        /**
         * Type which represents relative auto value.
         */
        AUTO,
        /**
         * Type which represents relative min content value.
         */
        MIN_CONTENT,
        /**
         * Type which represents relative max content value.
         */
        MAX_CONTENT,
        /**
         * Type which presents fit content function value.
         */
        FIT_CONTENT,
        /**
         * Type which represents minmax function value.
         */
        MINMAX,
        /**
         * Type which represents relative flexible value.
         */
        FLEX,
        /**
         * Type which represents fixed repeat value.
         */
        FIXED_REPEAT,
        /**
         * Type which represents auto-repeat value.
         */
        AUTO_REPEAT
    }
}
