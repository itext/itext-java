package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.kernel.geom.Point;

/**
 * Radial gradient vector point represented as circle with center and radius.
 */
public class RadialGradientPoint {

    private final Point center;
    private double radius;

    /**
     * Creates an instance with (0,0) center and 0 radius.
     */
    public RadialGradientPoint() {
        this(new Point(), 0d);
    }

    /**
     * Creates an instance with specified center and radius.
     *
     * @param center the center of the circle
     * @param radius the radius of the circle
     */
    public RadialGradientPoint(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * Copy constructor.
     *
     * @param point the point to copy
     */
    public RadialGradientPoint(RadialGradientPoint point) {
        this(point.center.getLocation(), point.radius);
    }

    /**
     * Get the center as a {@link Point} instance
     *
     * @return the {@link Point} object representing the circle center
     */
    public Point getCenter() {
        return center;
    }

    /**
     * Get the radius.
     *
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Set the radius.
     *
     * @param radius the radius value
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Get the X coordinate of the center.
     *
     * @return the X coordinate
     */
    public double getX() {
        return this.center.getX();
    }

    /**
     * Get the Y coordinate of the center.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return this.center.getY();
    }

    /**
     * Get the distance between this and other point centers.
     *
     * @param c the point to which center the distance is needed
     *
     * @return the distance value
     */
    public double distance(RadialGradientPoint c) {
        return this.center.distance(c.center);
    }
}
