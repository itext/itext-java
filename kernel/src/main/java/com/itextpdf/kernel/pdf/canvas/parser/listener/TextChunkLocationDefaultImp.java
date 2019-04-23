/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Vector;

class TextChunkLocationDefaultImp implements ITextChunkLocation {

    private static final float DIACRITICAL_MARKS_ALLOWED_VERTICAL_DEVIATION = 2;

    /**
     * the starting location of the chunk
     */
    private final Vector startLocation;
    /**
     * the ending location of the chunk
     */
    private final Vector endLocation;
    /**
     * unit vector in the orientation of the chunk
     */
    private final Vector orientationVector;
    /**
     * the orientation as a scalar for quick sorting
     */
    private final int orientationMagnitude;
    /**
     * perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
     * we round to the nearest integer to handle the fuzziness of comparing floats
     */
    private final int distPerpendicular;
    /**
     * distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
     */
    private final float distParallelStart;
    /**
     * distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
     */
    private final float distParallelEnd;
    /**
     * the width of a single space character in the font of the chunk
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
        orientationMagnitude = (int) (Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1)) * 1000);

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
