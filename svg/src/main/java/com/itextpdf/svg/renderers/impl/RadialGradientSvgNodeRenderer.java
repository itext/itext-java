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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.colors.gradients.IGradientBuilder;
import com.itextpdf.kernel.colors.gradients.RadialGradientPoint;
import com.itextpdf.kernel.colors.gradients.RadialGradientBuilder;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;radialGradient&gt; tag.
 */
public class RadialGradientSvgNodeRenderer extends AbstractGradientSvgNodeRenderer {

    /**
     * Creates a new instance of {@link RadialGradientSvgNodeRenderer}.
     */
    public RadialGradientSvgNodeRenderer() {
        // Empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISvgNodeRenderer createDeepCopy() {
        RadialGradientSvgNodeRenderer copy = new RadialGradientSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IGradientBuilder createGradientBuilderAndConfigureGeometry(
            SvgDrawContext context, Rectangle objectBoundingBox) {
        RadialGradientBuilder builder = new RadialGradientBuilder();
        boolean isObjectBoundingBox = isObjectBoundingBoxUnits();
        Tuple2<RadialGradientPoint, RadialGradientPoint> coordinates = getCoordinates(context, isObjectBoundingBox);
        builder.setGradientVector(
                coordinates.getFirst().getX(), coordinates.getFirst().getY(), coordinates.getFirst().getRadius(),
                coordinates.getSecond().getX(), coordinates.getSecond().getY(), coordinates.getSecond().getRadius());
        builder.setCurrentSpaceToGradientVectorSpaceTransformation(
                getGradientTransformToUserSpaceOnUse(objectBoundingBox, isObjectBoundingBox));
        return builder;
    }

    private Tuple2<RadialGradientPoint, RadialGradientPoint> getCoordinates(SvgDrawContext context,
            boolean isObjectBoundingBox) {
        if (isObjectBoundingBox) {
            return getObjectBoundingBoxCoordinates();
        } else {
            return getUserSpaceOnUseCoordinates(context);
        }
    }

    private Tuple2<RadialGradientPoint, RadialGradientPoint> getUserSpaceOnUseCoordinates(SvgDrawContext context) {
        Rectangle currentViewPort = this.getCurrentViewBox(context);
        double x = currentViewPort.getX();
        double y = currentViewPort.getY();
        double width = currentViewPort.getWidth();
        double height = currentViewPort.getHeight();
        float em = getCurrentFontSize(context);
        float rem = context.getCssContext().getRootFontSize();

        double cx = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                getAttribute(Attributes.CX), x + width / 2, x, width, em, rem);
        double cy = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                getAttribute(Attributes.CY), y + height / 2, y, height, em, rem);

        double fx = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                getAttribute(Attributes.FX), cx, x, width, em, rem);
        double fy = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                getAttribute(Attributes.FY), cy, y, height, em, rem);

        double r = parseGradientRadiusOnUserSpaceOnUse(getAttribute(Attributes.R), 0.5F, context);
        double fr = Math.max(0, parseGradientRadiusOnUserSpaceOnUse(getAttribute(Attributes.FR), 0F, context));

        return new Tuple2<>(
                new RadialGradientPoint(new Point(fx, fy), fr),
                new RadialGradientPoint(new Point(cx, cy), r)
        );
    }

    private Tuple2<RadialGradientPoint, RadialGradientPoint> getObjectBoundingBoxCoordinates() {
        double originalCx = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(getAttribute(Attributes.CX), 0.5);
        double originalCy = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(getAttribute(Attributes.CY), 0.5);
        double cx = originalCx * CONVERT_COEFF;
        double cy = originalCy * CONVERT_COEFF;

        double fx = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                getAttribute(Attributes.FX), originalCx) * CONVERT_COEFF;
        double fy = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                getAttribute(Attributes.FY), originalCy) * CONVERT_COEFF;

        double r = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(getAttribute(Attributes.R), 0.5)
                * CONVERT_COEFF;
        double fr = Math.max(0, SvgCoordinateUtils.getCoordinateForObjectBoundingBox(getAttribute(Attributes.FR), 0)
                * CONVERT_COEFF);

        return new Tuple2<>(
                new RadialGradientPoint(new Point(fx, fy), fr),
                new RadialGradientPoint(new Point(cx, cy), r)
        );
    }

    private float parseGradientRadiusOnUserSpaceOnUse(String radiusValue, float defaultPercent,
            SvgDrawContext context) {
        float percentBaseValue = SvgCoordinateUtils.calculateNormalizedDiagonalLength(context);
        return SvgCssUtils.parseAbsoluteLength(this, radiusValue, percentBaseValue,
                defaultPercent * percentBaseValue, context);
    }

}

