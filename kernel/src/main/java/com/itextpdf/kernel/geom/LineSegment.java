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

/**
 * Represents a line segment in a particular coordinate system.  This class is immutable.
 */
public class LineSegment {

    /** Start vector of the segment. */
    private final Vector startPoint;
    /** End vector of the segment. */
    private final Vector endPoint;

    /**
     * Creates a new line segment.
     * @param startPoint the start point of a line segment.
     * @param endPoint the end point of a line segment.
     */
    public LineSegment(Vector startPoint, Vector endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    /**
     * @return the start point
     */
    public Vector getStartPoint() {
        return startPoint;
    }

    /**
     * @return the end point
     */
    public Vector getEndPoint() {
        return endPoint;
    }

    /**
     * @return the length of this line segment
     */
    public float getLength(){
        return endPoint.subtract(startPoint).length();
    }

    /**
     * Computes the bounding rectangle for this line segment.  The rectangle has a rotation 0 degrees
     * with respect to the coordinate system that the line system is in.  For example, if a line segment
     * is 5 unit long and sits at a 37 degree angle from horizontal, the bounding rectangle will have
     * origin of the lower left hand end point of the segment, with width = 4 and height = 3.
     * @return the bounding rectangle
     */
    public Rectangle getBoundingRectangle(){
        float x1 = getStartPoint().get(Vector.I1);
        float y1 = getStartPoint().get(Vector.I2);
        float x2 = getEndPoint().get(Vector.I1);
        float y2 = getEndPoint().get(Vector.I2);
        return new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
    }

    /**
     * Transforms the segment by the specified matrix
     * @param m the matrix for the transformation
     * @return the transformed segment
     */
    public LineSegment transformBy(Matrix m){
        Vector newStart = startPoint.cross(m);
        Vector newEnd = endPoint.cross(m);
        return new LineSegment(newStart, newEnd);
    }

    /**
     * Checks if a segment contains another segment in itself
     * @param other a segment to be checked
     * @return true if this segment contains other one, false otherwise
     */
    public boolean containsSegment(LineSegment other) {
        return other != null && containsPoint(other.startPoint) && containsPoint(other.endPoint);

    }

    /**
     * Checks if a segment contains a given point in itself
     * @param point a point to be checked
     * @return true if this segment contains given point, false otherwise
     */
    public boolean containsPoint(Vector point) {
        if (point == null) {
            return false;
        }

        Vector diff1 = point.subtract(startPoint);
        if (diff1.get(0) < 0 || diff1.get(1) < 0 || diff1.get(2) < 0) {
            return false;
        }

        Vector diff2 = endPoint.subtract(point);
        if (diff2.get(0) < 0 || diff2.get(1) < 0 || diff2.get(2) < 0) {
            return false;
        }

        return true;
    }
}
