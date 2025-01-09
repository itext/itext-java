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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.LinearGradientBuilder;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.TemplateResolveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;linearGradient&gt; tag.
 */
public class LinearGradientSvgNodeRenderer extends AbstractGradientSvgNodeRenderer {


    private static final double CONVERT_COEFF = 0.75;

    @Override
    public Color createColor(SvgDrawContext context, Rectangle objectBoundingBox, float objectBoundingBoxMargin,
            float parentOpacity) {
        if (objectBoundingBox == null) {
            return null;
        }

        //create color is an entry point method for linear gradient when drawing svg, so resolving href values here
        TemplateResolveUtils.resolve(this, context);

        LinearGradientBuilder builder = new LinearGradientBuilder();

        for (GradientColorStop stopColor : parseStops(parentOpacity)) {
            builder.addColorStop(stopColor);
        }
        builder.setSpreadMethod(parseSpreadMethod());

        boolean isObjectBoundingBox = isObjectBoundingBoxUnits();

        Point[] coordinates = getCoordinates(context, isObjectBoundingBox);

        builder.setGradientVector(coordinates[0].getX(), coordinates[0].getY(),
                coordinates[1].getX(), coordinates[1].getY());

        AffineTransform gradientTransform = getGradientTransformToUserSpaceOnUse(objectBoundingBox,
                isObjectBoundingBox);

        builder.setCurrentSpaceToGradientVectorSpaceTransformation(gradientTransform);

        return builder.buildColor(
                objectBoundingBox.applyMargins(objectBoundingBoxMargin, objectBoundingBoxMargin, objectBoundingBoxMargin, objectBoundingBoxMargin, true),
                context.getCurrentCanvasTransform(), context.getCurrentCanvas().getDocument()
        );
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        LinearGradientSvgNodeRenderer copy = new LinearGradientSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    // TODO: DEVSIX-4136 opacity is not supported now.
    //  The opacity should be equal to 'parentOpacity * stopRenderer.getStopOpacity() * stopColor[3]'
    private List<GradientColorStop> parseStops(float parentOpacity) {
        List<GradientColorStop> stopsList = new ArrayList<>();
        for (StopSvgNodeRenderer stopRenderer : getChildStopRenderers()) {
            float[] stopColor = stopRenderer.getStopColor();
            double offset = stopRenderer.getOffset();
            stopsList.add(new GradientColorStop(stopColor, offset, OffsetType.RELATIVE));
        }

        if (!stopsList.isEmpty()) {
            GradientColorStop firstStop = stopsList.get(0);
            if (firstStop.getOffset() > 0) {
                stopsList.add(0, new GradientColorStop(firstStop, 0f, OffsetType.RELATIVE));
            }

            GradientColorStop lastStop = stopsList.get(stopsList.size() - 1);
            if (lastStop.getOffset() < 1) {
                stopsList.add(new GradientColorStop(lastStop, 1f, OffsetType.RELATIVE));
            }
        }
        return stopsList;
    }

    private AffineTransform getGradientTransformToUserSpaceOnUse(Rectangle objectBoundingBox,
            boolean isObjectBoundingBox) {
        AffineTransform gradientTransform = new AffineTransform();
        if (isObjectBoundingBox) {
            gradientTransform.translate(objectBoundingBox.getX(), objectBoundingBox.getY());
            // We need to scale with dividing the lengths by 0.75 as further we should
            // concatenate gradient transformation matrix which has no absolute parsing.
            // For example, if gradientTransform is set to translate(1, 1) and gradientUnits
            // is set to "objectBoundingBox" then the gradient should be shifted horizontally
            // and vertically exactly by the size of the element bounding box. So, again,
            // as we parse translate(1, 1) to translation(0.75, 0.75) the bounding box in
            // the gradient vector space should be 0.75x0.75 in order for such translation
            // to shift by the complete size of bounding box.
            gradientTransform
                    .scale(objectBoundingBox.getWidth() / CONVERT_COEFF, objectBoundingBox.getHeight() / CONVERT_COEFF);
        }

        AffineTransform svgGradientTransformation = getGradientTransform();
        if (svgGradientTransformation != null) {
            gradientTransform.concatenate(svgGradientTransformation);
        }
        return gradientTransform;
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
