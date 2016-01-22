package com.itextpdf.core.pdf.canvas.image;

/**
 * Represents a 2-dimensional point.
 */
public class Point {

    /**
     * The X value of the point.
     */
    public int x;

    /**
     * The Y value of the point.
     */
    public int y;

    /**
     * Creates a point without coordinates.
     */
    public Point() {
        // empty body
    }

    /**
     * Creates a point and sets the two values as its coordinates.
     *
     * @param x the X value of the point
     * @param y the Y value of the point
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
