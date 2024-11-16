/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.svg.MarkerVertexType;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.IMarkerCapable;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;

import java.util.Map;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;line&gt; tag.
 */
public class LineSvgNodeRenderer extends AbstractSvgNodeRenderer implements IMarkerCapable {

    private float x1 = 0f;
    private float y1 = 0f;
    private float x2 = 0f;
    private float y2 = 0f;

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();
        canvas.writeLiteral("% line\n");

        if (setParameters(context)) {
            canvas.moveTo(x1, y1).lineTo(x2, y2);
        }
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        if (setParameters(context)) {
            float x = Math.min(x1, x2);
            float y = Math.min(y1, y2);

            float width = Math.abs(x1 - x2);
            float height = Math.abs(y1 - y2);

            return new Rectangle(x, y, width, height);
        } else {
            return null;
        }
    }

    @Override
    protected boolean canElementFill() {
        return false;
    }

    float getAttribute(Map<String, String> attributes, String key) {
        String value = attributes.get(key);
        if (value != null && !value.isEmpty()) {
            return CssDimensionParsingUtils.parseAbsoluteLength(attributes.get(key));
        }
        return 0;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        LineSvgNodeRenderer copy = new LineSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

    @Override
    public void drawMarker(SvgDrawContext context, final MarkerVertexType markerVertexType) {
        String moveX = null;
        String moveY = null;
        if (MarkerVertexType.MARKER_START.equals(markerVertexType)) {
            moveX = this.attributesAndStyles.get(SvgConstants.Attributes.X1);
            moveY = this.attributesAndStyles.get(SvgConstants.Attributes.Y1);
        } else if (MarkerVertexType.MARKER_END.equals(markerVertexType)) {
            moveX = this.attributesAndStyles.get(SvgConstants.Attributes.X2);
            moveY = this.attributesAndStyles.get(SvgConstants.Attributes.Y2);
        }
        if (moveX != null && moveY != null) {
            MarkerSvgNodeRenderer.drawMarker(context, moveX, moveY, markerVertexType, this);
        }
    }

    @Override
    public double getAutoOrientAngle(MarkerSvgNodeRenderer marker, boolean reverse) {
        Vector v = new Vector(this.x2 - this.x1, this.y2 - this.y1, 0.0F);
        Vector xAxis = new Vector(1, 0, 0);
        double rotAngle = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(xAxis, v);
        return v.get(1) >= 0 && !reverse ? rotAngle : rotAngle * -1f;
    }

    private boolean setParameters(SvgDrawContext context) {
        if (attributesAndStyles.size() > 0) {
            if (attributesAndStyles.containsKey(SvgConstants.Attributes.X1)) {
                this.x1 = parseHorizontalLength(attributesAndStyles.get(SvgConstants.Attributes.X1), context);
            }

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.Y1)) {
                this.y1 = parseVerticalLength(attributesAndStyles.get(SvgConstants.Attributes.Y1), context);
            }

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.X2)) {
                this.x2 = parseHorizontalLength(attributesAndStyles.get(SvgConstants.Attributes.X2), context);
            }

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.Y2)) {
                this.y2 = parseVerticalLength(attributesAndStyles.get(SvgConstants.Attributes.Y2), context);
            }
            return true;
        }
        return false;
    }
}
