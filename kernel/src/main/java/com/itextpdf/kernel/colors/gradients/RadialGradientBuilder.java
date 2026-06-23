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

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * The radial gradient builder implementation with direct target gradient vector
 * and shading transformation ({@link AbstractRadialGradientBuilder more info})
 */
public class RadialGradientBuilder extends AbstractRadialGradientBuilder {

    private final RadialGradientPoint[] coordinates = new RadialGradientPoint[] {
            new RadialGradientPoint(),
            new RadialGradientPoint()
    };
    private AffineTransform transformation = null;

    /**
     * Constructs the builder instance
     */
    public RadialGradientBuilder() {
        // empty constructor
    }

    /**
     * Set coordinates for gradient vector circles ({@link AbstractRadialGradientBuilder more info})
     *
     * @param x0 the x coordinate of the vector start circle
     * @param y0 the y coordinate of the vector start circle
     * @param r0 the radius of the vector start circle
     * @param x1 the x coordinate of the vector end circle
     * @param y1 the y coordinate of the vector end circle
     * @param r1 the radius of the vector end circle
     *
     * @return the current builder instance
     */
    public RadialGradientBuilder setGradientVector(double x0, double y0, double r0, double x1, double y1, double r1) {
        this.coordinates[0].getCenter().setLocation(x0, y0);
        this.coordinates[0].setRadius(r0);
        this.coordinates[1].getCenter().setLocation(x1, y1);
        this.coordinates[1].setRadius(r1);
        return this;
    }

    /**
     * Set the radial gradient space transformation which specifies the transformation from
     * the current coordinates space to gradient vector space.
     *
     * <p>
     * The current space is the one on which radial gradient will be drawn (as a fill or stroke
     * color for shapes on PDF canvas). This transformation mainly used for ellipse based gradient.
     *
     * @param transformation the {@link AffineTransform} representing the transformation to set
     *
     * @return the current builder instance
     */
    public RadialGradientBuilder setCurrentSpaceToGradientVectorSpaceTransformation(
            AffineTransform transformation) {
        this.transformation = transformation;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Tuple2<RadialGradientPoint[], AffineTransform> getGradientVectorWithTransform(Rectangle targetBoundingBox,
            AffineTransform contextTransform) {
        return new Tuple2<RadialGradientPoint[], AffineTransform>(
                new RadialGradientPoint[] {
                        new RadialGradientPoint(this.coordinates[0]),
                        new RadialGradientPoint(this.coordinates[1]),
                },
                this.transformation
        );
    }
}
