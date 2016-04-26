package com.itextpdf.layout.property;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.IPropertyContainer;

/**
 * A specialized class holding configurable properties related to an {@link
 * com.itextpdf.layout.element.IElement}'s background. This class is meant to be used as the value for the
 * {@link Property#BACKGROUND} key in an {@link IPropertyContainer}. Allows
 * to define a background color, and positive or negative changes to the
 * location of the edges of the background coloring.
 */
public class Background {
    protected Color color;
    protected float extraLeft;
    protected float extraRight;
    protected float extraTop;
    protected float extraBottom;

    /**
     * Creates a background with a specified color.
     * @param color the background color
     */
    public Background(Color color) {
        this(color, 0, 0, 0, 0);
    }

    /**
     * Creates a background with a specified color, and extra space that
     * must be counted as part of the background and therefore colored.
     * These values are allowed to be negative.
     * @param color the background color
     * @param extraLeft extra coloring to the left side
     * @param extraTop extra coloring at the top
     * @param extraRight extra coloring to the right side
     * @param extraBottom extra coloring at the bottom
     */
    public Background(Color color, float extraLeft, float extraTop, float extraRight, float extraBottom) {
        this.color = color;
        this.extraLeft = extraLeft;
        this.extraRight = extraRight;
        this.extraTop = extraTop;
        this.extraBottom = extraBottom;
    }

    /**
     * Gets the background's color.
     * @return a {@link Color} of any supported kind
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the extra space that must be filled to the left of the Element.
     * @return a float value
     */
    public float getExtraLeft() {
        return extraLeft;
    }

    /**
     * Gets the extra space that must be filled to the right of the Element.
     * @return a float value
     */
    public float getExtraRight() {
        return extraRight;
    }

    /**
     * Gets the extra space that must be filled at the top of the Element.
     * @return a float value
     */
    public float getExtraTop() {
        return extraTop;
    }

    /**
     * Gets the extra space that must be filled at the bottom of the Element.
     * @return a float value
     */
    public float getExtraBottom() {
        return extraBottom;
    }
}
