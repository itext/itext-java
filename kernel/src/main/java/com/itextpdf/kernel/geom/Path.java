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
import java.util.Arrays;
import java.util.List;

/**
 * Paths define shapes, trajectories, and regions of all sorts. They shall be used
 * to draw lines, define the shapes of filled areas, and specify boundaries for clipping
 * other graphics. A path shall be composed of straight and curved line segments, which
 * may connect to one another or may be disconnected.
 */
public class Path {

    private static final String START_PATH_ERR_MSG = "Path shall start with \"re\" or \"m\" operator";

    private List<Subpath> subpaths = new ArrayList<>();
    private Point currentPoint;

    public Path() {
    }

    public Path(List<? extends Subpath> subpaths) {
        addSubpaths(subpaths);
    }

    public Path(Path path) {
        addSubpaths(path.getSubpaths());
    }

    /**
     * @return A {@link java.util.List} of subpaths forming this path.
     */
    public List<Subpath> getSubpaths() {
        return subpaths;
    }

    /**
     * Adds the subpath to this path.
     *
     * @param subpath The subpath to be added to this path.
     */
    public void addSubpath(Subpath subpath) {
        subpaths.add(subpath);
        currentPoint = subpath.getLastPoint();
    }

    /**
     * Adds the subpaths to this path.
     *
     * @param subpaths {@link java.util.List} of subpaths to be added to this path.
     */
    public void addSubpaths(List<? extends Subpath> subpaths) {
        if (subpaths.size() > 0) {
            for (Subpath subpath : subpaths) {
                this.subpaths.add(new Subpath(subpath));
            }
            currentPoint = this.subpaths.get(subpaths.size() - 1).getLastPoint();
        }
    }

    /**
     * The current point is the trailing endpoint of the segment most recently added to the current path.
     *
     * @return The current point.
     */
    public Point getCurrentPoint() {
        return currentPoint;
    }

    /**
     * Begins a new subpath by moving the current point to coordinates <CODE>(x, y)</CODE>.
     * @param x x-coordinate of the new point
     * @param y y-coordinate of the new point
     */
    public void moveTo(float x, float y) {
        currentPoint = new Point(x, y);
        Subpath lastSubpath = subpaths.size() > 0 ? subpaths.get(subpaths.size() - 1) : null;

        if (lastSubpath != null && lastSubpath.isSinglePointOpen()) {
            lastSubpath.setStartPoint(currentPoint);
        } else {
            subpaths.add(new Subpath(currentPoint));
        }
    }

    /**
     * Appends a straight line segment from the current point to the point <CODE>(x, y)</CODE>.
     * @param x x-coordinate of the new point
     * @param y y-coordinate of the new point
     */
    public void lineTo(float x, float y) {
        if (currentPoint == null) {
            throw new RuntimeException(START_PATH_ERR_MSG);
        }
        Point targetPoint = new Point(x, y);
        getLastSubpath().addSegment(new Line(currentPoint, targetPoint));
        currentPoint = targetPoint;
    }

    /**
     * Appends a cubic Bezier curve to the current path. The curve shall extend from
     * the current point to the point <CODE>(x3, y3)</CODE>.
     * @param x1 x-coordinate of the first control point
     * @param y1 y-coordinate of the first control point
     * @param x2 x-coordinate of the second control point
     * @param y2 y-coordinate of the second control point
     * @param x3 x-coordinate of the third control point
     * @param y3 y-coordinate of the third control point
     */
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (currentPoint == null) {
            throw new RuntimeException(START_PATH_ERR_MSG);
        }
        // Numbered in natural order
        Point secondPoint = new Point(x1, y1);
        Point thirdPoint = new Point(x2, y2);
        Point fourthPoint = new Point(x3, y3);

        List<Point> controlPoints = new ArrayList<>(Arrays.asList(currentPoint, secondPoint, thirdPoint, fourthPoint));
        getLastSubpath().addSegment(new BezierCurve(controlPoints));

        currentPoint = fourthPoint;
    }

    /**
     * Appends a cubic Bezier curve to the current path. The curve shall extend from
     * the current point to the point <CODE>(x3, y3)</CODE> using the current point and
     * <CODE>(x2, y2)</CODE> as intermediate control points. Note that current point is both
     * used as the starting point and a control point
     * @param x2 x-coordinate of the second intermediate control point
     * @param y2 y-coordinate of the second intermediate control point
     * @param x3 x-coordinate of the ending point
     * @param y3 y-coordinate of the ending point
     */
    public void curveTo(float x2, float y2, float x3, float y3) {
        if (currentPoint == null) {
            throw new RuntimeException(START_PATH_ERR_MSG);
        }
        curveTo((float) currentPoint.getX(), (float) currentPoint.getY(), x2, y2, x3, y3);
    }

    /**
     * Appends a cubic Bezier curve to the current path. The curve shall extend from
     * the current point to the point <CODE>(x3, y3)</CODE> using <CODE>(x1, y1)</CODE> and
     * <CODE>(x3, y3)</CODE> as control points. Note that <CODE>(x3, y3)</CODE> is used both
     * as both a control point and an ending point
     * @param x1 x-coordinate of the first intermediate control point
     * @param y1 y-coordinate of the first intermediate control point
     * @param x3 x-coordinate of the second intermediate control point (and ending point)
     * @param y3 y-coordinate of the second intermediate control point (and ending point)
     */
    public void curveFromTo(float x1, float y1, float x3, float y3) {
        if (currentPoint == null) {
            throw new RuntimeException(START_PATH_ERR_MSG);
        }
        curveTo(x1, y1, x3, y3, x3, y3);
    }

    /**
     * Appends a rectangle to the current path as a complete subpath.
     * @param rect the rectangle to append to the current path
     */
    public void rectangle(Rectangle rect) {
        rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    /**
     * Appends a rectangle to the current path as a complete subpath.
     * @param x lower left x-coordinate of the rectangle
     * @param y lower left y-coordinate of the rectangle
     * @param w width of the rectangle
     * @param h height of the rectangle
     */
    public void rectangle(float x, float y, float w, float h) {
        moveTo(x, y);
        lineTo(x + w, y);
        lineTo(x + w, y + h);
        lineTo(x, y + h);
        closeSubpath();
    }

    /**
     * Closes the current subpath.
     */
    public void closeSubpath() {
        if (!isEmpty()) {
            Subpath lastSubpath = getLastSubpath();
            lastSubpath.setClosed(true);

            Point startPoint = lastSubpath.getStartPoint();
            moveTo((float) startPoint.getX(), (float) startPoint.getY());
        }
    }

    /**
     * Closes all subpathes contained in this path.
     */
    public void closeAllSubpaths() {
        for (Subpath subpath : subpaths) {
            subpath.setClosed(true);
        }
    }

    /**
     * Adds additional line to each closed subpath and makes the subpath unclosed.
     * The line connects the last and the first points of the subpaths.
     *
     * @return Indices of modified subpaths.
     */
    public List<Integer> replaceCloseWithLine() {
        List<Integer> modifiedSubpathsIndices = new ArrayList<>();
        int i = 0;

        /* It could be replaced with "for" cycle, because IList in C# provides effective
         * access by index. In Java List interface has at least one implementation (LinkedList)
         * which is "bad" for access elements by index.
         */
        for (Subpath subpath : subpaths) {
            if (subpath.isClosed()) {
                subpath.setClosed(false);
                subpath.addSegment(new Line(subpath.getLastPoint(), subpath.getStartPoint()));
                modifiedSubpathsIndices.add(i);
            }
            ++i;
        }

        return modifiedSubpathsIndices;
    }

    /**
     * Path is empty if it contains no subpaths.
     * @return <code>true</code> in case the path is empty and <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return subpaths.size() == 0;
    }

    private Subpath getLastSubpath() {
        assert subpaths.size() > 0;
        return subpaths.get(subpaths.size() - 1);
    }
}
