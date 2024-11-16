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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.DrawUtils;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;circle&gt; tag.
 */
public class EllipseSvgNodeRenderer extends AbstractSvgNodeRenderer {

    float cx, cy, rx, ry;

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas cv = context.getCurrentCanvas();
        cv.writeLiteral("% ellipse\n");
        if (setParameters(context)) {
            // Use double type locally to have better precision of the result after applying arithmetic operations
            cv.moveTo((double) cx + (double) rx, cy);
            DrawUtils.arc((double) cx - (double) rx, (double) cy - (double) ry, (double) cx + (double) rx,
                    (double) cy + (double) ry, 0, 360, cv);
        }
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        if (setParameters(context)) {
            return new Rectangle(cx - rx, cy - ry, rx + rx, ry + ry);
        } else {
            return null;
        }
    }

    /**
     * Fetches a map of String values by calling getAttribute(String s) method
     * and maps it's values to arc parameter cx, cy , rx, ry respectively
     * <p>
     * This method is deprecated in favour of {@link EllipseSvgNodeRenderer#setParameters(SvgDrawContext)}, because
     * x/y/rx/ry can contain relative values which can't be resolved without {@link SvgDrawContext}.
     *
     * @return boolean values to indicate whether all values exit or not
     */
    @Deprecated
    protected boolean setParameters() {
        return setParameters(new SvgDrawContext(null, null));
    }

    /**
     * Fetches a map of String values by calling getAttribute(String s) method
     * and maps it's values to arc parameter cx, cy , rx, ry respectively
     *
     * @param context the SVG draw context
     *
     * @return boolean values to indicate whether all values exit or not
     */
    protected boolean setParameters(SvgDrawContext context) {
        initCenter(context);
        rx = parseHorizontalLength(getAttribute(SvgConstants.Attributes.RX), context);
        ry = parseVerticalLength(getAttribute(SvgConstants.Attributes.RY), context);
        return rx > 0.0F && ry > 0.0F;
    }

    /**
     * Initialize ellipse cx and cy.
     *
     * @param context svg draw context
     */
    protected void initCenter(SvgDrawContext context) {
        cx = 0;
        cy = 0;
        if(getAttribute(SvgConstants.Attributes.CX) != null){
            cx = parseHorizontalLength(getAttribute(SvgConstants.Attributes.CX), context);
        }
        if(getAttribute(SvgConstants.Attributes.CY) != null){
            cy = parseVerticalLength(getAttribute(SvgConstants.Attributes.CY), context);
        }
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        EllipseSvgNodeRenderer copy = new EllipseSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

}
