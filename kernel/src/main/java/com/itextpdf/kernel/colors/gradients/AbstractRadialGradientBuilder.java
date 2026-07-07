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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.shading.AbstractPdfShading;
import com.itextpdf.kernel.pdf.colorspace.shading.PdfRadialShading;
import com.itextpdf.kernel.pdf.function.IPdfFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for radial gradient builders implementations.
 */
public abstract class AbstractRadialGradientBuilder extends AbstractGradientBuilder<RadialGradientPoint> {

    // tan(t/2), with t == 2 deg we have tan(t/2) = 0.017455
    private static final double TAN_CONSTANT = 0.017455;

    /**
     * {@inheritDoc}
     */
    @Override
    protected RadialGradientPoint[] createCoordsForNewDomain(double[] newDomain, RadialGradientPoint[] baseVector) {
        double xDiff = baseVector[1].getX() - baseVector[0].getX();
        double yDiff = baseVector[1].getY() - baseVector[0].getY();
        double rDiff = baseVector[1].getRadius() - baseVector[0].getRadius();

        RadialGradientPoint[] targetCoords = new RadialGradientPoint[] {
                new RadialGradientPoint(baseVector[0]),
                new RadialGradientPoint(baseVector[1])
        };
        targetCoords[0].getCenter().move(xDiff * newDomain[0], yDiff * newDomain[0]);
        targetCoords[0].setRadius(targetCoords[0].getRadius() + rDiff * newDomain[0]);

        targetCoords[1].getCenter().move(xDiff * (newDomain[1] - 1), yDiff * (newDomain[1] - 1));
        targetCoords[1].setRadius(targetCoords[1].getRadius() + rDiff * (newDomain[1] - 1));
        return targetCoords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RadialGradientPoint[] createCoveringCoordinates(Rectangle targetBoundingBox) {
        // take one vertex as a center of both circles
        // and bigger circles should cover the whole rectangle, i.e. radius should cover the farthest corner
        Point center = new Point(targetBoundingBox.getLeft(), targetBoundingBox.getBottom());
        double radius = center.distance(new Point(targetBoundingBox.getRight(), targetBoundingBox.getTop()));
        return new RadialGradientPoint[] {
                new RadialGradientPoint(center, 0d),
                new RadialGradientPoint(center, radius)
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double getBaseVectorLength(RadialGradientPoint[] coordinates) {
        return coordinates[1].getCenter().distance(coordinates[0].getCenter())
                + Math.abs(coordinates[1].getRadius() - coordinates[0].getRadius());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PdfArray createCoordsDictEntry(RadialGradientPoint[] coordsPoints) {
        assert coordsPoints != null && coordsPoints.length == 2;

        return new PdfArray(new double[] {coordsPoints[0].getX(), coordsPoints[0].getY(), coordsPoints[0].getRadius(),
                coordsPoints[1].getX(), coordsPoints[1].getY(), coordsPoints[1].getRadius()});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractPdfShading createPdfShading(PdfColorSpace colorSpace, PdfArray coordinates,
            PdfArray coordinatesDomain, IPdfFunction stopsFunction) {
        return new PdfRadialShading(colorSpace, coordinates, coordinatesDomain, stopsFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double[] computeCoveringDomain(RadialGradientPoint[] coords, Rectangle toCover) {
        if (toCover == null) {
            return new double[] {0d, 1d};
        }

        double originalCentersDistance = coords[0].distance(coords[1]);
        // matching centers case
        if (isZero(originalCentersDistance)) {
            return getDomainForMatchingCenters(coords, toCover);
        }

        // For all other cases we will transform the plane to have base domain circles in points (0,0) and (1,0).
        // So that covering circles will have x coordinate equals to target domain.
        double scale = 1d / originalCentersDistance;
        AffineTransform transform = getToIntervalTransform(coords[0].getCenter(), coords[1].getCenter(), scale);
        List<Point> transformedRectVertices = new ArrayList<>(4);
        for (Point v : toCover.toPointsArray()) {
            transformedRectVertices.add(transform.transform(v, null));
        }

        // radii on transformed plane
        double r0 = coords[0].getRadius() * scale;
        double r1 = coords[1].getRadius() * scale;
        double rDiffAbs = Math.abs(r1 - r0);

        // four possible cases: rDiffAbs == 0 (lane), 0 < rDiffAbs < 1 (cone),
        // rDiffAbs == 1 (half-plane), rDiffAbs > 1 (full plane)
        if (isZero(rDiffAbs - 1)) {
            // rDiffAbs == 1 (half-plane)
            return getDomainForHalfPlaneCase(r0, r1, transformedRectVertices);
        } else if (rDiffAbs > 1) {
            // rDiffAbs > 1 (full plane)
            return getDomainForFullPlaneCase(r0, r1, transformedRectVertices);
        } else {
            // rDiffAbs == 0 (lane), 0 < rDiffAbs < 1 (cone)
            return getDomainForConeCase(r0, r1, transformedRectVertices);
        }
    }

    private static double[] getDomainForHalfPlaneCase(double r0, double r1, List<Point> rectVertices) {
        double rDiff = r1 - r0;
        // The method assumes that radii diff is 1 (i.e. all circles has one common touch point)
        assert isZero(Math.abs(rDiff) - 1);
        assert !rectVertices.isEmpty();

        double xZeroRad = -1d * r0 / rDiff;

        boolean hasCoveredVertex = false;
        boolean hasUncoveredVertex = false;
        double xMin = xZeroRad;
        double xMax = xZeroRad;
        for (Point point : rectVertices) {
            double px = point.getX();
            double denominator = 2 * r0 * rDiff + 2 * px;
            if (isZero(denominator)) {
                // With zero denominator the point is placed on non-covered edge of the surface.
                // So we need infinite max domain.
                hasUncoveredVertex = true;
            } else {
                double py = point.getY();
                double xCandidate = (-1d * r0 * r0 + px * px + py * py) / denominator;
                if (getRadius(xCandidate, r0, r1) < 0) {
                    // uncovered half of the surface
                    hasUncoveredVertex = true;
                } else {
                    hasCoveredVertex = true;
                    xMin = Math.min(xMin, xCandidate);
                    xMax = Math.max(xMax, xCandidate);
                }
            }
        }

        boolean isIncreasingRadius = rDiff > 0;
        xMin = isIncreasingRadius ? xZeroRad : xMin;
        xMax = isIncreasingRadius ? xMax : xZeroRad;
        // Cases:
        // - hasUncoveredVertex == false, hasCoveredVertex == false: unreachable
        // - hasUncoveredVertex == false, hasCoveredVertex == true: all vertices are covered, we have valid xMax
        // - hasUncoveredVertex == false, hasCoveredVertex == true: all vertices are uncovered, xMax = xZeroRad
        // - hasUncoveredVertex == false, hasCoveredVertex == true: xMax should be equal to positive infinity
        if (hasUncoveredVertex && hasCoveredVertex) {
            // We should choose finite but big enough domain to cover the bbox.
            double maxY = 0;
            for (Point point : rectVertices) {
                double py = point.getY();
                maxY = Math.abs(py) > Math.abs(maxY) ? py : maxY;
            }
            double coveredSign = isIncreasingRadius ? 1d : -1d;
            // Looking for px so that arc between (xZeroRad, 0) and (px, maxY) would correspond predefined t deg.
            // Formula: px = xZeroRad +/- maxY * tan(t/2)
            double px = xZeroRad + coveredSign * maxY * TAN_CONSTANT;
            double denominator = 2 * r0 * rDiff + 2 * px;
            double targetX = (-1d * r0 * r0 + px * px + maxY * maxY) / denominator;
            if (isIncreasingRadius) {
                xMax = targetX;
            } else {
                xMin = targetX;
            }
        }
        return new double[] {xMin, xMax};
    }

    private static double[] getDomainForFullPlaneCase(double r0, double r1, List<Point> rectVertices) {
        double rDiff = r1 - r0;
        // The method assumes that radii diff is greater than 1 (i.e. any circles covers all smaller circles)
        assert Math.abs(rDiff) > 1;

        double xZeroRad = -1d * r0 / rDiff;

        double xMin = xZeroRad;
        double xMax = xZeroRad;
        for (Point point : rectVertices) {
            double px = point.getX();
            double py = point.getY();

            // solving ax^2 + bx + c = 0;
            double a = rDiff * rDiff - 1d;
            double b = 2 * (r0 * rDiff + px);
            double c = r0 * r0 - px * px - py * py;
            double dSqrt = Math.sqrt(b * b - 4 * a * c);
            double x1 = (-1 * b - dSqrt) / (2 * a);
            double x2 = (-1 * b + dSqrt) / (2 * a);

            if (getRadius(x1, r0, r1) >= 0) {
                xMin = Math.min(xMin, x1);
                xMax = Math.max(xMax, x1);
            }
            if (getRadius(x2, r0, r1) >= 0) {
                xMin = Math.min(xMin, x2);
                xMax = Math.max(xMax, x2);
            }
        }

        boolean isIncreasingRadius = rDiff > 0;
        xMin = isIncreasingRadius ? xZeroRad : xMin;
        xMax = isIncreasingRadius ? xMax : xZeroRad;

        return new double[] {xMin, xMax};
    }

    private static double[] getDomainForConeCase(double r0, double r1, List<Point> rectVertices) {
        double rDiff = r1 - r0;

        // The method assumes that radii diff is smaller than 1 (i.e. cone or lane)
        assert Math.abs(rDiff) < 1;

        double vXMax = rectVertices.get(0).getX();
        double vXMin = vXMax;
        for (int i = 1; i < rectVertices.size(); ++i) {
            vXMax = Math.max(vXMax, rectVertices.get(i).getX());
            vXMin = Math.min(vXMin, rectVertices.get(i).getX());
        }

        double xMax = (vXMax + r0) / (1 - rDiff);
        double xMin = (vXMin - r0) / (1 + rDiff);

        if (!isZero(rDiff)) {
            // for zero diff case there is no xZeroRad value,
            // but both xMin and xMax should have non-negative radius (equal to r0)
            double xZeroRad = -1 * r0 / rDiff;
            if (getRadius(xMin, r0, r1) < 0) {
                xMin = xZeroRad;
            }
            if (getRadius(xMax, r0, r1) < 0) {
                xMax = xZeroRad;
            }
        }
        return new double[] {xMin, xMax};
    }

    private static double getRadius(double x, double r0, double r1) {
        return r0 + x * (r1 - r0);
    }

    private static double[] getDomainForMatchingCenters(RadialGradientPoint[] coords, Rectangle toCover) {
        // The method assumes that circles has identical centers
        assert isZero(coords[0].distance(coords[1]));

        // First calculate min and max radii to cover the rectangle.
        Point center = coords[0].getCenter();
        double minRadius = getMinDistance(center, toCover);

        double maxRadius = 0.0;
        for (Point p : toCover.toPointsArray()) {
            maxRadius = Math.max(maxRadius, center.distance(p));
        }

        // Second calculate the domain
        double domainStep = coords[1].getRadius() - coords[0].getRadius();
        double maxRadDomain = (maxRadius - coords[0].getRadius()) / domainStep;
        double minRadDomain = (minRadius - coords[0].getRadius()) / domainStep;

        double domainStart = Math.min(minRadDomain, maxRadDomain);
        double domainEnd = Math.max(minRadDomain, maxRadDomain);
        return new double[] {domainStart, domainEnd};
    }

    private static double getMinDistance(Point from, Rectangle to) {
        double dx = 0.0;
        if (from.getX() < to.getLeft()) {
            dx = to.getLeft() - from.getX();
        } else if (from.getX() > to.getRight()) {
            dx = from.getX() - to.getRight();
        }

        double dy = 0.0;
        if (from.getY() < to.getBottom()) {
            dy = to.getBottom() - from.getY();
        } else if (from.getY() > to.getTop()) {
            dy = from.getY() - to.getTop();
        }

        return Math.sqrt(dx * dx + dy * dy);
    }
}
