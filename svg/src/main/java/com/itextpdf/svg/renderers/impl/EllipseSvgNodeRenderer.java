/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
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
        if (setParameters()) {
            // Use double type locally to have better precision of the result after applying arithmetic operations
            cv.moveTo((double) cx + (double) rx, cy);
            DrawUtils.arc((double) cx - (double) rx, (double) cy - (double) ry, (double) cx + (double) rx,
                    (double) cy + (double) ry, 0, 360, cv);
        }
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        if (setParameters()) {
            return new Rectangle(cx - rx, cy - ry, rx + rx, ry + ry);
        } else {
            return null;
        }
    }

    /**
     * Fetches a map of String values by calling getAttribute(String s) method
     * and maps it's values to arc parameter cx, cy , rx, ry respectively
     *
     * @return boolean values to indicate whether all values exit or not
     */
    protected boolean setParameters() {
        cx = 0;
        cy = 0;
        if (getAttribute(SvgConstants.Attributes.CX) != null) {
            cx = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CX));
        }
        if (getAttribute(SvgConstants.Attributes.CY) != null) {
            cy = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CY));
        }

        if (getAttribute(SvgConstants.Attributes.RX) != null
                && CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RX)) > 0) {
            rx = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RX));
        } else {
            //No drawing if rx is absent
            return false;
        }
        if (getAttribute(SvgConstants.Attributes.RY) != null
                && CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RY)) > 0) {
            ry = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.RY));
        } else {
            //No drawing if ry is absent
            return false;
        }
        return true;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        EllipseSvgNodeRenderer copy = new EllipseSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

}
