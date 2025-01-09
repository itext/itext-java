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

import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.svg.SvgConstants;

import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;circle&gt; tag.
 */
public class CircleSvgNodeRenderer extends EllipseSvgNodeRenderer {

    @Override
    protected boolean setParameters(SvgDrawContext context) {
        initCenter(context);
        String r = getAttribute(SvgConstants.Attributes.R);
        float percentBaseValue = 0.0F;
        if (CssTypesValidationUtils.isPercentageValue(r)) {
            if (context.getCurrentViewPort() == null) {
                throw new SvgProcessingException(SvgExceptionMessageConstant.ILLEGAL_RELATIVE_VALUE_NO_VIEWPORT_IS_SET);
            }
            percentBaseValue = SvgCoordinateUtils.calculateNormalizedDiagonalLength(context);
        }
        rx = SvgCssUtils.parseAbsoluteLength(this, r, percentBaseValue, 0.0F, context);
        ry = rx;
        return rx > 0.0F;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        CircleSvgNodeRenderer copy = new CircleSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

}
