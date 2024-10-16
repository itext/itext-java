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
package com.itextpdf.layout.minmaxwidth;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinMaxWidthUtils {

    private static final float eps = 0.01f;
    private static final float max = 32760f;

    public static float getEps() {
        return eps;
    }

    public static float getInfWidth() {
        return max;
    }

    public static float getInfHeight() { return 1e6f; }

    public static boolean isEqual(double x, double y) {
        return Math.abs(x - y) < eps;
    }

    public static MinMaxWidth countDefaultMinMaxWidth(IRenderer renderer) {
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(getInfWidth(), getInfHeight()))));
        return result.getStatus() == LayoutResult.NOTHING ? new MinMaxWidth() :
                new MinMaxWidth(0, result.getOccupiedArea().getBBox().getWidth(), 0);
    }
    
    public static float getBorderWidth(IPropertyContainer element) {
        Border rightBorder = element.<Border>getProperty(Property.BORDER_RIGHT);
        Border leftBorder = element.<Border>getProperty(Property.BORDER_LEFT);

        float rightBorderWidth = rightBorder != null ? rightBorder.getWidth() : 0;
        float leftBorderWidth = leftBorder != null ? leftBorder.getWidth() : 0;
        return rightBorderWidth + leftBorderWidth;
    }
    
    public static float getMarginsWidth(IPropertyContainer element) {
        UnitValue rightMargin = element.<UnitValue>getProperty(Property.MARGIN_RIGHT);
        if (null != rightMargin && !rightMargin.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MinMaxWidthUtils.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_RIGHT));
        }
        UnitValue leftMargin = element.<UnitValue>getProperty(Property.MARGIN_LEFT);
        if (null != leftMargin && !leftMargin.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MinMaxWidthUtils.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_LEFT));
        }

        float rightMarginWidth = rightMargin != null ? rightMargin.getValue() : 0;
        float leftMarginWidth = leftMargin != null ? leftMargin.getValue() : 0;
        
        return  rightMarginWidth + leftMarginWidth;
    }
    
    public static float getPaddingWidth(IPropertyContainer element) {
        UnitValue rightPadding = element.<UnitValue>getProperty(Property.PADDING_RIGHT);
        if (null != rightPadding && !rightPadding.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MinMaxWidthUtils.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.PADDING_RIGHT));
        }
        UnitValue leftPadding = element.<UnitValue>getProperty(Property.PADDING_LEFT);
        if (null != leftPadding && !leftPadding.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MinMaxWidthUtils.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.PADDING_LEFT));
        }

        float rightPaddingWidth = rightPadding != null ? rightPadding.getValue() : 0;
        float leftPaddingWidth = leftPadding != null ? leftPadding.getValue() : 0;

        return  rightPaddingWidth + leftPaddingWidth;
    }
}
