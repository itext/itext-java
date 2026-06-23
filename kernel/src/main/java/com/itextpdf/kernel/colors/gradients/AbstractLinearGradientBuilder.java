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
import com.itextpdf.kernel.pdf.colorspace.shading.PdfAxialShading;
import com.itextpdf.kernel.pdf.function.IPdfFunction;

import java.util.List;

/**
 * Base class for linear gradient builders implementations.
 * <p>
 * Color transitions for linear gradients are defined by a series of color stops along a gradient
 * vector. A gradient normal defines how the colors in a vector are painted to the surface. For
 * a linear gradient, a normal is a line perpendicular to the vector.
 * <p>
 * Contains the main logic that works with stop colors and creation of the resulted pdf color object.
 */
public abstract class AbstractLinearGradientBuilder extends AbstractGradientBuilder<Point> {

    /**
     * The epsilon value used for data creation
     *
     * @deprecated use {@link AbstractGradientBuilder#isZero(double)} instead for zero comparisons
     */
    @Deprecated
    protected static final double ZERO_EPSILON = 1E-10;

    /**
     * Adds the new color stop to the end ({@link AbstractLinearGradientBuilder more info}).
     *
     * <p>
     * Note: if the previously added color stop's offset would have grater offset than the added
     * one, then the new offset would be normalized to be equal to the previous one. (Comparison
     * made between relative on coordinates vector offsets. If any of them has
     * the absolute offset, then the absolute value would converted to relative first.)
     *
     * @param gradientColorStop the gradient stop color to add
     *
     * @return the current builder instance
     *
     * @deprecated use {@link AbstractGradientBuilder#addStopColor(GradientColorStop)} instead
     */
    @Deprecated
    public AbstractLinearGradientBuilder addColorStop(GradientColorStop gradientColorStop) {
        super.addStopColor(gradientColorStop);
        return this;
    }

    /**
     * Set the spread method to use for the gradient
     *
     * @param gradientSpreadMethod the gradient spread method to set
     *
     * @return the current builder instance
     *
     * @deprecated use {@link AbstractGradientBuilder#setSpread(GradientSpreadMethod)} instead
     */
    @Deprecated
    public AbstractLinearGradientBuilder setSpreadMethod(GradientSpreadMethod gradientSpreadMethod) {
        super.setSpread(gradientSpreadMethod);
        return this;
    }

    /**
     * Get the copy of current color stops list. Note that the stop colors are not copied here
     *
     * @return the copy of current stop colors list
     *
     * @deprecated use {@link AbstractGradientBuilder#getStopColors()} instead
     */
    @Deprecated
    public List<GradientColorStop> getColorStops() {
        return super.getStopColors();
    }

    /**
     * Get the current spread method
     *
     * @return the current spread method
     *
     * @deprecated use {@link AbstractGradientBuilder#getSpread()} instead
     */
    @Deprecated
    public GradientSpreadMethod getSpreadMethod() {
        return super.getSpread();
    }

    /**
     * Returns the base gradient vector in gradient vector space. This vector would be set
     * as shading coordinates vector and its length would be used to translate all color stops
     * absolute offsets into the relatives.
     *
     * @param targetBoundingBox the rectangle to be covered by constructed color in current space
     * @param contextTransform  the current canvas transformation
     *
     * @return the array of exactly two elements specifying the gradient coordinates vector
     *
     * @deprecated use {@link AbstractGradientBuilder#getGradientVectorWithTransform(Rectangle, AffineTransform)}
     */
    @Deprecated
    protected abstract Point[] getGradientVector(Rectangle targetBoundingBox,
            AffineTransform contextTransform);

    /**
     * Returns the current space to gradient vector space transformations that should be applied
     * to the shading color. The transformation should be invertible as the current target
     * bounding box coordinates should be transformed into the resulted shading space coordinates.
     *
     * @param targetBoundingBox the rectangle to be covered by constructed color in current space
     * @param contextTransform  the current canvas transformation
     *
     * @return the additional transformation to be concatenated to the current for resulted shading
     * or {@code null} if no additional transformation is specified
     *
     * @deprecated use {@link AbstractGradientBuilder#getGradientVectorWithTransform(Rectangle, AffineTransform)}
     */
    @Deprecated
    protected AffineTransform getCurrentSpaceToGradientVectorSpaceTransformation(
            Rectangle targetBoundingBox, AffineTransform contextTransform) {
        return null;
    }

    /**
     * Evaluates the minimal domain that covers the box with vector normals.
     * The domain corresponding to the initial vector is [0, 1].
     *
     * @param coords  the array of exactly two elements that describe
     *                the base vector (corresponding to [0,1] domain, that need to be adjusted
     *                to cover the box
     * @param toCover the box that needs to be covered
     *
     * @return the array of two elements in ascending order specifying the calculated covering
     * domain
     */
    // TODO: DEVSIX-8808 move the implementation directly into the place where we call this method
    protected static double[] evaluateCoveringDomain(Point[] coords, Rectangle toCover) {
        if (toCover == null) {
            return new double[] {0d, 1d};
        }

        double scale = 1d / (coords[0].distance(coords[1]));
        AffineTransform transform = getToIntervalTransform(coords[0], coords[1], scale);

        Point[] rectanglePoints = toCover.toPointsArray();
        double minX = transform.transform(rectanglePoints[0], null).getX();
        double maxX = minX;
        for (int i = 1; i < rectanglePoints.length; ++i) {
            double currentX = transform.transform(rectanglePoints[i], null).getX();
            minX = Math.min(minX, currentX);
            maxX = Math.max(maxX, currentX);
        }

        return new double[] {minX, maxX};
    }

    /**
     * Expand the base vector to cover the new domain
     *
     * @param newDomain  the array of exactly two elements that specifies the domain
     *                   that should be covered by the created vector
     * @param baseVector the array of exactly two elements that specifies the base vector
     *                   which corresponds to [0, 1] domain
     *
     * @return the array of two
     */
    // TODO: DEVSIX-8808 move the implementation directly into the place where we call this method
    protected static Point[] createCoordinatesForNewDomain(double[] newDomain, Point[] baseVector) {
        double xDiff = baseVector[1].getX() - baseVector[0].getX();
        double yDiff = baseVector[1].getY() - baseVector[0].getY();

        Point[] targetCoords = new Point[] {
                baseVector[0].getLocation(),
                baseVector[1].getLocation()
        };
        targetCoords[0].move(xDiff * newDomain[0], yDiff * newDomain[0]);
        targetCoords[1].move(xDiff * (newDomain[1] - 1), yDiff * (newDomain[1] - 1));
        return targetCoords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double[] computeCoveringDomain(Point[] coords, Rectangle toCover) {
        return evaluateCoveringDomain(coords, toCover);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Point[] createCoordsForNewDomain(double[] newDomain, Point[] baseVector) {
        return createCoordinatesForNewDomain(newDomain, baseVector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Point[] createCoveringCoordinates(Rectangle targetBoundingBox) {
        return new Point[] {new Point(targetBoundingBox.getLeft(), targetBoundingBox.getBottom()),
                new Point(targetBoundingBox.getRight(), targetBoundingBox.getBottom())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double getBaseVectorLength(Point[] coordinates) {
        return coordinates[1].distance(coordinates[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PdfArray createCoordsDictEntry(Point[] coordinates) {
        assert coordinates != null && coordinates.length == 2;

        return new PdfArray(new double[] {coordinates[0].getX(), coordinates[0].getY(),
                coordinates[1].getX(), coordinates[1].getY()});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractPdfShading createPdfShading(
            PdfColorSpace colorSpace, PdfArray coordinates,
            PdfArray coordinatesDomain, IPdfFunction stopsFunction) {
        return new PdfAxialShading(colorSpace, coordinates, coordinatesDomain, stopsFunction);
    }
}
