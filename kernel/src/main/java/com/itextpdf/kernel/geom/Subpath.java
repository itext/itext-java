/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.geom;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * As subpath is a part of a path comprising a sequence of connected segments.
 */
public class Subpath implements Serializable {

    private static final long serialVersionUID = -3464451279777771490L;
    private Point startPoint;
    private List<IShape> segments = new ArrayList<>();
    private boolean closed;

    /**
     * Creates a new SubPath instance.
     */
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
    public Subpath(Point startPoint) {
        this((float) startPoint.getX(), (float) startPoint.getY());
    }

    /**
     * Constructs a new subpath starting at the given point.
     */
    public Subpath(float startPointX, float startPointY) {
        this.startPoint = new Point(startPointX, startPointY);
    }

    /**
     * Sets the start point of the subpath.
     * @param startPoint
     */
    public void setStartPoint(Point startPoint) {
        setStartPoint((float) startPoint.getX(), (float) startPoint.getY());
    }

    /**
     * Sets the start point of the subpath.
     * @param x
     * @param y
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
