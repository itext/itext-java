package com.itextpdf.layout.properties.grid;

/**
 * Represents an auto template value.
 */
public final class AutoValue extends BreadthValue {
    /**
     * auto value constant.
     */
    public static final AutoValue VALUE = new AutoValue();

    private AutoValue() {
        super(ValueType.AUTO);
    }
}
