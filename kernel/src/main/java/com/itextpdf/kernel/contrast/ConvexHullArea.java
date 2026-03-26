/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.contrast;


import com.itextpdf.kernel.geom.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for computing the convex hull of a set of points in 2D space.
 * <p>
 * The convex hull is the smallest convex polygon that contains all the given points.
 * This implementation uses Andrew's monotone chain algorithm, which is a variant of
 * Graham's scan with improved stability and efficiency.
 * <p>
 * The algorithm runs in O(n log n) time complexity, where n is the number of input points.
 */
final class ConvexHullArea {

    private static final int MIN_POINTS_FOR_HULL = 2;
    private static final double EPSILON = 1e-6;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ConvexHullArea() {
        // Utility class
    }

    /**
     * Computes the convex hull of a set of points using Andrew's monotone chain algorithm.
     * <p>
     * The algorithm works by:
     * <ol>
     *     * Sorting all points lexicographically (first by x-coordinate, then by y-coordinate)
     *     * Constructing the lower hull by scanning from left to right
     *     * Constructing the upper hull by scanning from right to left
     *     * Combining both hulls to form the complete convex hull
     * </ol>
     * <p>
     * The returned list of points represents the vertices of the convex hull in counter-clockwise order.
     * <p>
     *
     * @param points the list of points for which to compute the convex hull. Must not be {@code null}.
     *               Points may be in any order and may include duplicates.
     *
     * @return a list of points representing the vertices of the convex hull in counter-clockwise order.
     * If the input contains 0 or 1 points, returns the input list unchanged.
     * If all points are collinear, returns the two extreme points.
     */
    public static List<Point> convexHull(List<Point> points) {
        List<Point> copiedPoints = new ArrayList<>(points);
        if (copiedPoints.size() <= 1) {
            return copiedPoints;
        }

        // Sort copiedPoints lexicographically (first by x, then by y)

        Collections.sort(copiedPoints, (p1, p2) -> {
            if (p1.getX() == p2.getX()) {
                return Double.compare(p1.getY(), p2.getY());
            }
            return Double.compare(p1.getX(), p2.getX());
        });

        // Build lower hull
        List<Point> lower = new ArrayList<>();
        for (Point p : copiedPoints) {
            buildHull(lower, p);
        }

        // Build upper hull
        List<Point> upper = new ArrayList<>();
        for (int i = copiedPoints.size() - 1; i >= 0; i--) {
            Point p = copiedPoints.get(i);
            buildHull(upper, p);
        }

        // Remove last point of each half because it's repeated at the beginning of the other half
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);

        // Combine lower and upper hulls
        lower.addAll(upper);

        return lower;
    }

    private static void buildHull(List<Point> lower, Point p) {
        while (lower.size() >= MIN_POINTS_FOR_HULL) {
            double crossProduct = cross(lower.get(lower.size() - 2),
                    lower.get(lower.size() - 1), p);
            if (crossProduct > EPSILON) {
                break;
            }
            lower.remove(lower.size() - 1);
        }
        lower.add(p);
    }

    /**
     * Calculates the cross product of vectors OA and OB, where O, A, and B are points in 2D space.
     * <p>
     * The cross product is used to determine the orientation of three points:
     * <p>
     * * Positive value: counter-clockwise turn (left turn)
     * * Negative value: clockwise turn (right turn)
     * * Zero: collinear points (no turn)
     *
     * <p>
     * The formula used is: (A.x - O.x) * (B.y - O.y) - (A.y - O.y) * (B.x - O.x)
     *
     * @param o the origin point
     * @param a the first point forming vector OA
     * @param b the second point forming vector OB
     *
     * @return the cross product value. Positive indicates a counter-clockwise turn,
     * negative indicates a clockwise turn, and zero indicates collinear points.
     */
    private static double cross(Point o, Point a, Point b) {
        return (a.getX() - o.getX()) * (b.getY() - o.getY()) - (a.getY() - o.getY()) * (b.getX() - o.getX());
    }
}
