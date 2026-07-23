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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TransformUtils;

import java.util.Arrays;

/**
 * This renderer represents a branch in an SVG tree. It doesn't do anything aside from calling the superclass doDraw.
 */
public class GroupSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas currentCanvas = context.getCurrentCanvas();

        for (ISvgNodeRenderer child : getChildren()) {
            currentCanvas.saveState();
            child.draw(context);
            currentCanvas.restoreState();
        }
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        GroupSvgNodeRenderer copy = new GroupSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        if (isHidden()) {
            return null;
        }
        Rectangle commonRectangle = null;
        for (ISvgNodeRenderer child : getChildren()) {
            if (child instanceof AbstractSvgNodeRenderer && ((AbstractSvgNodeRenderer) child).isHidden()) {
                continue;
            }
            Rectangle childBoundingBox = child.getObjectBoundingBox(context);
            String transformString = child.getAttribute(SvgConstants.Attributes.TRANSFORM);
            if (childBoundingBox != null && transformString != null && !transformString.isEmpty()) {
                AffineTransform transformation = TransformUtils.parseTransform(transformString);
                Point[] points = childBoundingBox.toPointsArray();
                transformation.transform(points, 0, points, 0, points.length);
                childBoundingBox = Rectangle.calculateBBox(Arrays.asList(points));
            }
            commonRectangle = Rectangle.getCommonRectangle(commonRectangle, childBoundingBox);
        }
        return commonRectangle;
    }
}
