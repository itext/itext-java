package com.itextpdf.core.parser;

import com.itextpdf.core.geom.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a line.
 *
 * @since 5.5.6
 */
public class Line implements Shape {

    private final Point2D p1;
    private final Point2D p2;

    /**
     * Constructs a new zero-length line starting at zero.
     */
    public Line() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs a new line based on the given coordinates.
     */
    public Line(float x1, float y1, float x2, float y2) {
        p1 = new Point2D.Float(x1, y1);
        p2 = new Point2D.Float(x2, y2);
    }

    /**
     * Constructs a new line based on the given coordinates.
     */
    public Line(Point2D p1, Point2D p2) {
        this((float) p1.getX(), (float) p1.getY(), (float) p2.getX(), (float) p2.getY());
    }

    public List<Point2D> getBasePoints() {
        List<Point2D> basePoints = new ArrayList<>(2);
        basePoints.add(p1);
        basePoints.add(p2);

        return basePoints;
    }
}
