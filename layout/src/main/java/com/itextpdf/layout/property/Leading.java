package com.itextpdf.layout.property;

import com.itextpdf.layout.IPropertyContainer;

/**
 * A specialized class that specifies the leading, "the vertical distance between
 * the baselines of adjacent lines of text" (ISO-32000-1, section 9.3.5).
 * Allows to use either an absolute (constant) leading value, or one
 * determined by font size. Pronounce as 'ledding' (cfr. Led Zeppelin).
 *
 * This class is meant to be used as the value for the
 * {@link Property#LEADING} key in an {@link IPropertyContainer}.
 */
public class Leading {
    /**
     * A leading type independent of font size.
     */
    public static final int FIXED = 1;

    /**
     * A leading type related to the font size and the resulting bounding box.
     */
    public static final int MULTIPLIED = 2;

    protected int type;
    protected float value;

    /**
     * Creates a Leading object.
     *
     * @param type a constant type that defines the calculation of actual
     * leading distance. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
     * @param value to be used as a basis for the leading calculation.
     */
    public Leading(int type, float value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the calculation type of the Leading object.
     *
     * @return the calculation type. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the value to be used as the basis for the leading calculation.
     * @return a calculation value
     */
    public float getValue() {
        return value;
    }
}
