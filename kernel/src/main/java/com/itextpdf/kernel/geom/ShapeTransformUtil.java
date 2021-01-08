/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.PdfException;

import java.util.Arrays;
import java.util.List;

/**
 * Public helper class for transforming segments and paths.
 * */
public final class ShapeTransformUtil {
    /**
     * Method for transforming a bezier curve.
     *
     * The method creates a new transformed bezier curve without changing the original curve.
     *
     * @param bezierCurve the source bezier curve for transformation
     * @param ctm the transformation matrix
     * @return the new transformed bezier curve
     */
    public static BezierCurve transformBezierCurve(BezierCurve bezierCurve, Matrix ctm) {
        return (BezierCurve) transformSegment(bezierCurve, ctm);
    }

    /**
     * Method for transforming a line.
     *
     * The method creates a new transformed line without changing the original line.
     *
     * @param line the source line for transformation
     * @param ctm the transformation matrix
     * @return the new transformed line
     */
    public static Line transformLine(Line line, Matrix ctm) {
        return (Line) transformSegment(line, ctm);
    }

    /**
     * Method for transforming a path.
     *
     * The method creates a new transformed path without changing the original path.
     *
     * @param path the source path for transformation
     * @param ctm the transformation matrix
     * @return the new transformed path
     */
    public static Path transformPath(Path path, Matrix ctm) {
        Path newPath = new Path();

        for (Subpath subpath : path.getSubpaths()) {
            Subpath transformedSubpath = transformSubpath(subpath, ctm);
            newPath.addSubpath(transformedSubpath);
        }

        return newPath;
    }

    private static Subpath transformSubpath(Subpath subpath, Matrix ctm) {
        Subpath newSubpath = new Subpath();
        newSubpath.setClosed(subpath.isClosed());

        for (IShape segment : subpath.getSegments()) {
            IShape transformedSegment = transformSegment(segment, ctm);
            newSubpath.addSegment(transformedSegment);
        }

        return newSubpath;
    }

    private static IShape transformSegment(IShape segment, Matrix ctm) {
        List<Point> basePoints = segment.getBasePoints();
        Point[] newBasePoints = transformPoints(ctm, basePoints.toArray(new Point[basePoints.size()]));

        IShape newSegment;
        if (segment instanceof BezierCurve) {
            newSegment = new BezierCurve(Arrays.asList(newBasePoints));
        } else {
            newSegment = new Line(newBasePoints[0], newBasePoints[1]);
        }

        return newSegment;
    }

    private static Point[] transformPoints(Matrix ctm, Point... points) {
        try {
            AffineTransform t = new AffineTransform(
                    ctm.get(Matrix.I11), ctm.get(Matrix.I12),
                    ctm.get(Matrix.I21), ctm.get(Matrix.I22),
                    ctm.get(Matrix.I31), ctm.get(Matrix.I32)
            );
            t = t.createInverse();

            Point[] newPoints = new Point[points.length];
            t.transform(points, 0, newPoints, 0, points.length);
            return newPoints;
        } catch (NoninvertibleTransformException e) {
            throw new PdfException(PdfException.NoninvertibleMatrixCannotBeProcessed, e);
        }
    }
}
