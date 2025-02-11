/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.geom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * As subpath is a part of a path comprising a sequence of connected segments.
 */
public class Subpath {

    private Point startPoint;
    private List<IShape> segments = new ArrayList<>();
    private boolean closed;

    /**
     * Creates a new SubPath instance.
     */
    public Subpath() {
    }

    /**
     * Copy constructor.
     *
     * @param subpath {@link Subpath} which contents will be used to create this {@link Subpath}
     */
    public Subpath(Subpath subpath) {
        this.startPoint = subpath.startPoint;
        this.segments.addAll(subpath.getSegments());
        this.closed = subpath.closed;
    }

    /**
     * Constructs a new subpath starting at the given point.
     *
     * @param startPoint the point this subpath starts at
     */
    public Subpath(Point startPoint) {
        this((float) startPoint.getX(), (float) startPoint.getY());
    }

    /**
     * Constructs a new subpath starting at the given point.
     *
     * @param startPointX x-coordinate of the start point of subpath
     * @param startPointY y-coordinate of the start point of subpath
     */
    public Subpath(float startPointX, float startPointY) {
        this.startPoint = new Point(startPointX, startPointY);
    }

    /**
     * Sets the start point of the subpath.
     * @param startPoint the point this subpath starts at
     */
    public void setStartPoint(Point startPoint) {
        setStartPoint((float) startPoint.getX(), (float) startPoint.getY());
    }

    /**
     * Sets the start point of the subpath.
     * @param x x-coordinate of the start pint
     * @param y y-coordinate of the start pint
     */
    public void setStartPoint(float x, float y) {
        this.startPoint = new Point(x, y);
    }

    /**
     * @return The point this subpath starts at.
     */
    public Point getStartPoint() {
        return startPoint;
    }

    /**
     * @return The last point of the subpath.
     */
    public Point getLastPoint() {
        Point lastPoint = startPoint;

        if (segments.size() > 0 && !closed) {
            IShape shape = segments.get(segments.size() - 1);
            lastPoint = shape.getBasePoints().get(shape.getBasePoints().size() - 1);
        }

        return lastPoint;
    }

    /**
     * Adds a segment to the subpath.
     * Note: each new segment shall start at the end of the previous segment.
     * @param segment new segment.
     */
    public void addSegment(IShape segment) {
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
    public List<IShape> getSegments() {
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

    /**
     * @return <CODE>true</CODE> if this subpath contains only one point and it is closed,
     *         <CODE>false</CODE> otherwise
     */
    public boolean isSinglePointClosed() {
        return segments.size() == 0 && closed;
    }

    /**
     * Returns a <CODE>boolean</CODE> value indicating whether the subpath must be closed or not.
     * Ignore this value if the subpath is a rectangle because in this case it is already closed
     * (of course if you paint the path using <CODE>re</CODE> operator)
     *
     * @return <CODE>boolean</CODE> value indicating whether the path must be closed or not.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * See {@link #isClosed()}
     *
     * @param closed <CODE>boolean</CODE> value indicating whether the path is closed or not.
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
     */
    public boolean isDegenerate() {
        if (segments.size() > 0 && closed) {
            return false;
        }

        for (IShape segment : segments) {
            Set<Point> points = new HashSet<>(segment.getBasePoints());

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
     */
    public List<Point> getPiecewiseLinearApproximation() {
        List<Point> result = new ArrayList<>();

        if (segments.size() == 0) {
            return result;
        }

        if (segments.get(0) instanceof BezierCurve) {
            result.addAll(((BezierCurve) segments.get(0)).getPiecewiseLinearApproximation());
        } else {
            result.addAll(segments.get(0).getBasePoints());
        }

        for (int i = 1; i < segments.size(); ++i) {
            List<Point> segApprox;

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
