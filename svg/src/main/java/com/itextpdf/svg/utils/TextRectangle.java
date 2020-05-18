package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * A rectangle adapted for working with text elements.
 */
public class TextRectangle extends Rectangle {

    private static final long serialVersionUID = -1263921426258495543L;

    /**
     * The y coordinate of the line on which the text is located.
     */
    private float textBaseLineYCoordinate;

    /**
     * Create new instance of text rectangle.
     *
     * @param x      the x coordinate of lower left point
     * @param y      the y coordinate of lower left point
     * @param width  the width value
     * @param height the height value
     * @param textBaseLineYCoordinate the y coordinate of the line on which the text is located.
     */
    public TextRectangle(float x, float y, float width, float height, float textBaseLineYCoordinate) {
        super(x, y, width, height);
        this.textBaseLineYCoordinate = textBaseLineYCoordinate;
    }

    /**
     * Return the far right point of the rectangle with y on the baseline.
     *
     * @return the far right baseline point
     */
    public Point getTextBaseLineRightPoint() {
        return new Point(getRight(), textBaseLineYCoordinate);
    }
}