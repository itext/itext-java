package com.itextpdf.layout.property;

/**
 * Class to hold background-repeat property.
 */
public class BackgroundRepeat {
    private final boolean repeatX;
    private final boolean repeatY;

    /**
     * Creates a new {@link BackgroundRepeat} instance.
     *
     * @param repeatX whether the background repeats in the x dimension.
     * @param repeatY whether the background repeats in the y dimension.
     */
    public BackgroundRepeat(final boolean repeatX, final boolean repeatY) {
        this.repeatX = repeatX;
        this.repeatY = repeatY;
    }

    /**
     * Is repeatX is true.
     *
     * @return repeatX value
     */
    public boolean isRepeatX() {
        return repeatX;
    }

    /**
     * Is repeatY is true.
     *
     * @return repeatY value
     */
    public boolean isRepeatY() {
        return repeatY;
    }
}
