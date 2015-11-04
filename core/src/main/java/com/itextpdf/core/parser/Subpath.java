package com.itextpdf.core.parser;


import com.itextpdf.basics.geom.Point2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * As subpath is a part of a path comprising a sequence of connected segments.
 *
 * @since 5.5.6
 */
public class Subpath {

    private Point2D startPoint;
    private List<Shape> segments = new ArrayList<Shape>();
    private boolean closed;

    public Subpath() {
    }

    /**
     * Copy constuctor.
     * @param subpath
     */
    public Subpath(Subpath subpath) {
        this.startPoint = subpath.startPoint;
        this.segments.addAll(subpath.getSegments());
        this.closed = subpath.closed;
    }

    /**
     * Constructs a new subpath starting at the given point.
     */
    public Subpath(Point2D startPoint) {
        this((float) startPoint.getX(), (float) startPoint.getY());
    }

    /**
     * Constructs a new subpath starting at the given point.
     */
    public Subpath(float startPointX, float startPointY) {
        this.startPoint = new Point2D.Float(startPointX, startPointY);
    }

    /**
     * Sets the start point of the subpath.
     * @param startPoint
     */
    public void setStartPoint(Point2D startPoint) {
        setStartPoint((float) startPoint.getX(), (float) startPoint.getY());
    }

    /**
     * Sets the start point of the subpath.
     * @param x
     * @param y
     */
    public void setStartPoint(float x, float y) {
        this.startPoint = new Point2D.Float(x, y);
    }

    /**
     * @return The point this subpath starts at.
     */
    public Point2D getStartPoint() {
        return startPoint;
    }

    /**
     * @return The last point of the subpath.
     */
    public Point2D getLastPoint() {
        Point2D lastPoint = startPoint;

        if (segments.size() > 0 && !closed) {
            Shape shape = segments.get(segments.size() - 1);
            lastPoint = shape.getBasePoints().get(shape.getBasePoints().size() - 1);
        }

        return lastPoint;
    }

    /**
     * Adds a segment to the subpath.
     * Note: each new segment shall start at the end of the previous segment.
     * @param segment new segment.
     */
    public void addSegment(Shape segment) {
        if (closed) {
            return;
        }

        if (isSinglePointOpen()) {
            startPoint = segment.getBasePoints().get(0);
        }

        segments.add(segment);
    }

    /**
     * @return {@link java.util.List} comprising all the segments
     *         the subpath made on.
     */
    public List<Shape> getSegments() {
        return segments;
    }

    /**
     * Checks whether subpath is empty or not.
     * @return true if the subpath is empty, false otherwise.
     */
    public boolean isEmpty() {
        return startPoint == null;
    }

    /**
     * @return <CODE>true</CODE> if this subpath contains only one point and it is not closed,
     *         <CODE>false</CODE> otherwise
     */
    public boolean isSinglePointOpen() {
        return segments.size() == 0 && !closed;
    }

    public boolean isSinglePointClosed() {
        return segments.size() == 0 && closed;
    }

    /**
     * Returns a <CODE>boolean</CODE> value indicating whether the subpath must be closed or not.
     * Ignore this value if the subpath is a rectangle because in this case it is already closed
     * (of course if you paint the path using <CODE>re</CODE> operator)
     *
     * @return <CODE>boolean</CODE> value indicating whether the path must be closed or not.
     * @since 5.5.6
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * See {@link #isClosed()}
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Returns a <CODE>boolean</CODE> indicating whether the subpath is degenerate or not.
     * A degenerate subpath is the subpath consisting of a single-point closed path or of
     * two or more points at the same coordinates.
     *
     * @return <CODE>boolean</CODE> value indicating whether the path is degenerate or not.
     * @since 5.5.6
     */
    public boolean isDegenerate() {
        if (segments.size() > 0 && closed) {
            return false;
        }

        for (Shape segment : segments) {
            Set<Point2D> points = new HashSet<Point2D>(segment.getBasePoints());

            // The first segment of a subpath always starts at startPoint, so...
            if (points.size() != 1) {
                return false;
            }
        }

        // the second clause is for case when we have single point
        return segments.size() > 0 || closed;
    }

    /**
     * @return {@link java.util.List} containing points of piecewise linear approximation
     *         for this subpath.
     * @since 5.5.6
     */
    public List<Point2D> getPiecewiseLinearApproximation() {
        List<Point2D> result = new ArrayList<Point2D>();

        if (segments.size() == 0) {
            return result;
        }

        if (segments.get(0) instanceof BezierCurve) {
            result.addAll(((BezierCurve) segments.get(0)).getPiecewiseLinearApproximation());
        } else {
            result.addAll(segments.get(0).getBasePoints());
        }

        for (int i = 1; i < segments.size(); ++i) {
            List<Point2D> segApprox;

            if (segments.get(i) instanceof BezierCurve) {
                segApprox = ((BezierCurve) segments.get(i)).getPiecewiseLinearApproximation();
                segApprox = segApprox.subList(1, segApprox.size());
            } else {
                segApprox = segments.get(i).getBasePoints();
                segApprox = segApprox.subList(1, segApprox.size());
            }

            result.addAll(segApprox);
        }

        return result;
    }
}
