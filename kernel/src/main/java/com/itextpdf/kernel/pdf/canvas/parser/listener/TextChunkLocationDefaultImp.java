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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Vector;

class TextChunkLocationDefaultImp implements ITextChunkLocation {

    private static final float DIACRITICAL_MARKS_ALLOWED_VERTICAL_DEVIATION = 2;

    /**
     * The starting location of the chunk.
     */
    private final Vector startLocation;
    /**
     * The ending location of the chunk.
     */
    private final Vector endLocation;
    /**
     * Unit vector in the orientation of the chunk.
     */
    private final Vector orientationVector;
    /**
     * The orientation as a scalar for quick sorting.
     */
    private final int orientationMagnitude;
    /**
     * Perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system).
     * We round to the nearest integer to handle the fuzziness of comparing floats.
     */
    private final int distPerpendicular;
    /**
     * Distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system).
     */
    private final float distParallelStart;
    /**
     * Distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system).
     */
    private final float distParallelEnd;
    /**
     * The width of a single space character in the font of the chunk.
     */
    private final float charSpaceWidth;

    public TextChunkLocationDefaultImp(Vector startLocation, Vector endLocation, float charSpaceWidth) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.charSpaceWidth = charSpaceWidth;

        Vector oVector = endLocation.subtract(startLocation);
        if (oVector.length() == 0) {
            oVector = new Vector(1, 0, 0);
        }
        orientationVector = oVector.normalize();
        orientationMagnitude = (int) FontProgram.convertGlyphSpaceToTextSpace(
                Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1)));

        // see http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
        // the two vectors we are crossing are in the same plane, so the result will be purely
        // in the z-axis (out of plane) direction, so we just take the I3 component of the result
        Vector origin = new Vector(0, 0, 1);
        distPerpendicular = (int) (startLocation.subtract(origin)).cross(orientationVector).get(Vector.I3);

        distParallelStart = orientationVector.dot(startLocation);
        distParallelEnd = orientationVector.dot(endLocation);
    }


    public int orientationMagnitude() {
        return orientationMagnitude;
    }

    public int distPerpendicular() {
        return distPerpendicular;
    }

    public float distParallelStart() {
        return distParallelStart;
    }

    public float distParallelEnd() {
        return distParallelEnd;
    }

    /**
     * @return the start location of the text
     */
    public Vector getStartLocation() {
        return startLocation;
    }

    /**
     * @return the end location of the text
     */
    public Vector getEndLocation() {
        return endLocation;
    }

    /**
     * @return the width of a single space character as rendered by this chunk
     */
    public float getCharSpaceWidth() {
        return charSpaceWidth;
    }

    /**
     * @param as the location to compare to
     * @return true is this location is on the the same line as the other
     */
    public boolean sameLine(ITextChunkLocation as) {
        if (orientationMagnitude() != as.orientationMagnitude()) {
            return false;
        }
        float distPerpendicularDiff = distPerpendicular() - as.distPerpendicular();
        if (distPerpendicularDiff == 0) {
            return true;
        }
        LineSegment mySegment = new LineSegment(startLocation, endLocation);
        LineSegment otherSegment = new LineSegment(as.getStartLocation(), as.getEndLocation());
        return Math.abs(distPerpendicularDiff) <= DIACRITICAL_MARKS_ALLOWED_VERTICAL_DEVIATION && (mySegment.getLength() == 0 || otherSegment.getLength() == 0);
    }

    /**
     * Computes the distance between the end of 'other' and the beginning of this chunk
     * in the direction of this chunk's orientation vector.  Note that it's a bad idea
     * to call this for chunks that aren't on the same line and orientation, but we don't
     * explicitly check for that condition for performance reasons.
     *
     * @param other
     * @return the number of spaces between the end of 'other' and the beginning of this chunk
     */
    public float distanceFromEndOf(ITextChunkLocation other) {
        return distParallelStart() - other.distParallelEnd();
    }

    public boolean isAtWordBoundary(ITextChunkLocation previous) {
        // In case a text chunk is of zero length, this probably means this is a mark character,
        // and we do not actually want to insert a space in such case
        if (startLocation.equals(endLocation) || previous.getEndLocation().equals(previous.getStartLocation())) {
            return false;
        }

        float dist = distanceFromEndOf(previous);

        if (dist < 0) {
            dist = previous.distanceFromEndOf(this);

            //The situation when the chunks intersect. We don't need to add space in this case
            if (dist < 0) {
                return false;
            }
        }
        return dist > getCharSpaceWidth() / 2.0f;
    }

    static boolean containsMark(ITextChunkLocation baseLocation, ITextChunkLocation markLocation) {
        return baseLocation.getStartLocation().get(Vector.I1) <= markLocation.getStartLocation().get(Vector.I1) && baseLocation.getEndLocation().get(Vector.I1) >= markLocation.getEndLocation().get(Vector.I1) &&
                Math.abs(baseLocation.distPerpendicular() - markLocation.distPerpendicular()) <= DIACRITICAL_MARKS_ALLOWED_VERTICAL_DEVIATION;
    }

}
