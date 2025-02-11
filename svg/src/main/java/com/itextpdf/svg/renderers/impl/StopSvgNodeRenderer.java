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

import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Tags;
import com.itextpdf.svg.renderers.INoDrawSvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * {@link ISvgNodeRenderer} implementation for the gradient &lt;stop&gt; tag.
 */
public class StopSvgNodeRenderer extends AbstractBranchSvgNodeRenderer implements INoDrawSvgNodeRenderer {

    /**
     * Evaluates the stop color offset value.
     *
     * @return the stop color offset value in [0, 1] range
     */
    public double getOffset() {
        Double offset = null;
        String offsetAttribute = getAttribute(Attributes.OFFSET);
        if (CssTypesValidationUtils.isPercentageValue(offsetAttribute)) {
            offset = (double) CssDimensionParsingUtils.parseRelativeValue(offsetAttribute, 1);
        } else if (CssTypesValidationUtils.isNumber(offsetAttribute)) {
            offset = CssDimensionParsingUtils.parseDouble(offsetAttribute);
        }
        double result = offset != null ? offset.doubleValue() : 0d;
        return result > 1d ? 1d : result > 0d ? result : 0d;
    }

    /**
     * Evaluates the rgba array of the specified stop color.
     *
     * @return the array of 4 floats which contains the rgba value corresponding
     * to the specified stop color
     */
    public float[] getStopColor() {
        float[] color = null;
        String colorValue = getAttribute(Tags.STOP_COLOR);
        if (colorValue != null) {
            color = WebColors.getRGBAColor(colorValue);
        }
        if (color == null) {
            color = WebColors.getRGBAColor("black");
        }
        return color;
    }

    /**
     * Evaluates the stop opacity of the specified stop color.
     *
     * @return the stop opacity value specified in the stop color
     */
    public float getStopOpacity() {
        Float result = null;
        String opacityValue = getAttribute(Tags.STOP_OPACITY);
        if (opacityValue != null && !SvgConstants.Values.NONE.equalsIgnoreCase(opacityValue)) {
            result = CssDimensionParsingUtils.parseFloat(opacityValue);
        }
        return result != null ? result.floatValue() : 1f;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        StopSvgNodeRenderer copy = new StopSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgExceptionMessageConstant.DRAW_NO_DRAW);
    }
}
