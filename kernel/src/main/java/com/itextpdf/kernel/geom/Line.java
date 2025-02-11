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
import java.util.List;

/**
 * Represents a line.
 */
public class Line implements IShape {

    private final Point p1;
    private final Point p2;

    /**
     * Constructs a new zero-length line starting at zero.
     */
    public Line() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs a new line based on the given coordinates.
     * @param x1 x-coordinate of start point of this Line
     * @param y1 y-coordinate of start point of this Line
     * @param x2 x-coordinate of end point of this Line
     * @param y2 y-coordinate of end point of this Line
     */
    public Line(float x1, float y1, float x2, float y2) {
        p1 = new Point(x1, y1);
        p2 = new Point(x2, y2);
    }

    /**
     * Constructs a new line based on the given coordinates.
     * @param p1 start point of this Line
     * @param p2 end point of this Line
     */
    public Line(Point p1, Point p2) {
        this((float) p1.getX(), (float) p1.getY(), (float) p2.getX(), (float) p2.getY());
    }

    public List<Point> getBasePoints() {
        List<Point> basePoints = new ArrayList<>(2);
        basePoints.add(p1);
        basePoints.add(p2);

        return basePoints;
    }
}
