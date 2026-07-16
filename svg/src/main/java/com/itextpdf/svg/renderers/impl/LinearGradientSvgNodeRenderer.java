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

import com.itextpdf.kernel.colors.gradients.IGradientBuilder;
import com.itextpdf.kernel.colors.gradients.LinearGradientBuilder;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;linearGradient&gt; tag.
 */
public class LinearGradientSvgNodeRenderer extends AbstractGradientSvgNodeRenderer {

    /**
     * {@inheritDoc}
     */
    @Override
    public ISvgNodeRenderer createDeepCopy() {
        LinearGradientSvgNodeRenderer copy = new LinearGradientSvgNodeRenderer();
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
        LinearGradientBuilder builder = new LinearGradientBuilder();
        boolean isObjectBoundingBox = isObjectBoundingBoxUnits();
        Point[] coordinates = getCoordinates(context, isObjectBoundingBox);
        builder.setGradientVector(coordinates[0].getX(), coordinates[0].getY(),
                coordinates[1].getX(), coordinates[1].getY());
        builder.setCurrentSpaceToGradientVectorSpaceTransformation(
                getGradientTransformToUserSpaceOnUse(objectBoundingBox, isObjectBoundingBox));
        return builder;
    }

    private Point[] getCoordinates(SvgDrawContext context, boolean isObjectBoundingBox) {
        Point start;
        Point end;
        if (isObjectBoundingBox) {
            // need to multiply by 0.75 as further the (top, right) coordinates of the object bbox
            // would be transformed into (0.75, 0.75) point instead of (1, 1). The reason described
            // as a comment inside the method constructing the gradient transformation
            start = new Point(SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(Attributes.X1), 0) * CONVERT_COEFF,
                    SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                            getAttribute(Attributes.Y1), 0) * CONVERT_COEFF);
            end = new Point(SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(Attributes.X2), 1) * CONVERT_COEFF,
                    SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                            getAttribute(Attributes.Y2), 0) * CONVERT_COEFF);
        } else {
            Rectangle currentViewPort = this.getCurrentViewBox(context);
            double x = currentViewPort.getX();
            double y = currentViewPort.getY();
            double width = currentViewPort.getWidth();
            double height = currentViewPort.getHeight();
            float em = getCurrentFontSize(context);
            float rem = context.getCssContext().getRootFontSize();
            start = new Point(
                    SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                            getAttribute(Attributes.X1), x, x, width, em, rem),
                    SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                            getAttribute(Attributes.Y1), y, y, height, em, rem));
            end = new Point(
                    SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                            getAttribute(Attributes.X2), x + width, x, width, em, rem),
                    SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                            getAttribute(Attributes.Y2), y, y, height, em, rem));
        }

        return new Point[] {start, end};
    }
}
