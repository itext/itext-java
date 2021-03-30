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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * The linear gradient builder implementation with direct target gradient vector
 * and shading transformation ({@link AbstractLinearGradientBuilder more info})
 */
public class LinearGradientBuilder extends AbstractLinearGradientBuilder {

    private final Point[] coordinates = new Point[] {new Point(), new Point()};
    private AffineTransform transformation = null;

    /**
     * Constructs the builder instance
     */
    public LinearGradientBuilder() {
    }

    /**
     * Set coordinates for gradient vector ({@link AbstractLinearGradientBuilder more info})
     *
     * @param x0 the x coordinate of the vector start
     * @param y0 the y coordinate of the vector start
     * @param x1 the x coordinate of the vector end
     * @param y1 the y coordinate of the vector end
     * @return the current builder instance
     */
    public LinearGradientBuilder setGradientVector(double x0, double y0, double x1, double y1) {
        this.coordinates[0].setLocation(x0, y0);
        this.coordinates[1].setLocation(x1, y1);
        return this;
    }

    /**
     * Set the linear gradient space transformation which specifies the transformation from
     * the current coordinates space to gradient vector space
     * <p>
     * The current space is the one on which linear gradient will be drawn (as a fill or stroke
     * color for shapes on PDF canvas). This transformation mainly used for color lines skewing.
     *
     * @param transformation the {@link AffineTransform} representing the transformation to set
     * @return the current builder instance
     */
    public LinearGradientBuilder setCurrentSpaceToGradientVectorSpaceTransformation(
            AffineTransform transformation) {
        this.transformation = transformation;
        return this;
    }

    @Override
    public Point[] getGradientVector(Rectangle targetBoundingBox, AffineTransform contextTransform) {
        return new Point[] {this.coordinates[0].getLocation(), this.coordinates[1].getLocation()};
    }

    @Override
    public AffineTransform getCurrentSpaceToGradientVectorSpaceTransformation(
            Rectangle targetBoundingBox, AffineTransform contextTransform) {
        return this.transformation;
    }
}
