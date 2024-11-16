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
package com.itextpdf.svg.utils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.AbstractSvgNodeRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that facilitates parsing values from CSS.
 */
// TODO DEVSIX-2266

public final class SvgCssUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SvgCssUtils.class);

    private SvgCssUtils() {}

    /**
     * Splits a given String into a list of substrings.
     * The string is split up by commas and whitespace characters (\t, \n, \r, \f).
     *
     * @param value the string to be split
     * @return a list containing the split strings, an empty list if the value is null or empty
     */
    public static List<String> splitValueList(String value) {
        List<String> result = new ArrayList<>();

        if (value != null && value.length() > 0) {
            value = value.trim();

            String[] list = value.split("[,|\\s]");
            for (String element: list) {
                if (!element.isEmpty()) {
                    result.add(element);
                }
            }
        }

        return result;
    }

    /**
     * Converts a float to a String.
     *
     * @param value to be converted float value
     * @return the value in a String representation
     */
    public static String convertFloatToString(float value) {
        return String.valueOf(value);
    }

    /**
     * Converts a double to a String.
     *
     * @param value to be converted double value
     * @return the value in a String representation
     */
    public static String convertDoubleToString(double value) {
        return String.valueOf(value);
    }

    /**
     * Parse length attribute and convert it to an absolute value.
     *
     * @param svgNodeRenderer renderer for which length should be parsed
     * @param length {@link String} for parsing
     * @param percentBaseValue the value on which percent length is based on
     * @param defaultValue default value if length is not recognized
     * @param context current {@link SvgDrawContext}
     * @return absolute value in points
     */
    public static float parseAbsoluteLength(AbstractSvgNodeRenderer svgNodeRenderer, String length,
                                            float percentBaseValue, float defaultValue, SvgDrawContext context) {
        final float em = svgNodeRenderer.getCurrentFontSize(context);
        final float rem = context.getCssContext().getRootFontSize();
        return CssDimensionParsingUtils.parseLength(length, percentBaseValue, defaultValue, em, rem);
    }

    /**
     * Extract svg viewbox values.
     *
     * @param svgRenderer the {@link ISvgNodeRenderer} instance that contains
     *                    the renderer tree
     *
     * @return float[4] or null, if no correct viewbox property is present.
     */
    public static float[] parseViewBox(ISvgNodeRenderer svgRenderer) {
        String vbString = svgRenderer.getAttribute(SvgConstants.Attributes.VIEWBOX);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (vbString == null) {
            vbString = svgRenderer.getAttribute(SvgConstants.Attributes.VIEWBOX.toLowerCase());
        }
        float[] values = null;
        if (vbString != null) {
            List<String> valueStrings = SvgCssUtils.splitValueList(vbString);
            values = new float[valueStrings.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = CssDimensionParsingUtils.parseAbsoluteLength(valueStrings.get(i));
            }
        }
        if (values != null) {
            // the value for viewBox should be 4 numbers according to the viewBox documentation
            if (values.length != SvgConstants.Values.VIEWBOX_VALUES_NUMBER) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(MessageFormatUtil.format(
                            SvgLogMessageConstant.VIEWBOX_VALUE_MUST_BE_FOUR_NUMBERS, vbString));
                }
                return null;
            }
            // in case when viewBox width or height is negative value is an error and
            // invalidates the ‘viewBox’ attribute (according to the viewBox documentation)
            if (values[2] < 0 || values[3] < 0) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(MessageFormatUtil.format(
                            SvgLogMessageConstant.VIEWBOX_WIDTH_AND_HEIGHT_CANNOT_BE_NEGATIVE, vbString));
                }
                return null;
            }
        }
        return values;
    }

    /**
     * Extract width and height of the passed SVGNodeRenderer,
     * defaulting to respective viewbox values if either one is not present or
     * to browser default if viewbox is missing as well.
     *
     * @param svgRenderer the {@link ISvgNodeRenderer} instance that contains
     *                       the renderer tree
     * @param em em value in pt
     * @param rem rem value in pt
     * @return Rectangle, where x,y = 0 and width and height are extracted ones by this method.
     */
    public static Rectangle extractWidthAndHeight(ISvgNodeRenderer svgRenderer, float em, float rem) {
        float[] values = SvgCssUtils.parseViewBox(svgRenderer);
        float defaultWidth = values == null ?
                CssDimensionParsingUtils.parseAbsoluteLength(SvgConstants.Values.DEFAULT_VIEWBOX_WIDTH) : values[2];
        float defaultHeight = values == null ?
                CssDimensionParsingUtils.parseAbsoluteLength(SvgConstants.Values.DEFAULT_VIEWBOX_HEIGHT) : values[3];
        Rectangle result = new Rectangle(defaultWidth, defaultHeight);

        String width = svgRenderer.getAttribute(SvgConstants.Attributes.WIDTH);
        if (CssTypesValidationUtils.isRemValue(width)) {
            result.setWidth(CssDimensionParsingUtils.parseRelativeValue(width, rem));
        } else if (CssTypesValidationUtils.isEmValue(width)) {
            result.setWidth(CssDimensionParsingUtils.parseRelativeValue(width, em));
        } else if (width != null) {
            result.setWidth(CssDimensionParsingUtils.parseAbsoluteLength(width));
        } else if (values == null) {
            LOGGER.warn(SvgLogMessageConstant.MISSING_WIDTH);
        }

        String height = svgRenderer.getAttribute(SvgConstants.Attributes.HEIGHT);
        if (CssTypesValidationUtils.isRemValue(height)) {
            result.setHeight(CssDimensionParsingUtils.parseRelativeValue(height, rem));
        } else if (CssTypesValidationUtils.isEmValue(height)) {
            result.setHeight(CssDimensionParsingUtils.parseRelativeValue(height, em));
        } else if (height != null) {
            result.setHeight(CssDimensionParsingUtils.parseAbsoluteLength(height));
        } else if (values == null) {
            LOGGER.warn(SvgLogMessageConstant.MISSING_HEIGHT);
        }
        return result;
    }
}
