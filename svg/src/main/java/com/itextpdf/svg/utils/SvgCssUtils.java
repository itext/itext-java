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
package com.itextpdf.svg.utils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.AbstractSvgNodeRenderer;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that facilitates parsing values from CSS.
 */
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
     *
     * @return the value in a String representation
     *
     * @deprecated can be replaced by {@code Float.toString(float)}
     */
    @Deprecated
    public static String convertFloatToString(float value) {
        return String.valueOf(value);
    }

    /**
     * Converts a double to a String.
     *
     * @param value to be converted double value
     *
     * @return the value in a String representation
     *
     * @deprecated can be replaced by {@code Double.toString(float)}
     */
    @Deprecated
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
     * Parses vertical length attribute and converts it to an absolute value.
     *
     * @param svgNodeRenderer renderer for which length should be parsed
     * @param length {@link String} for parsing
     * @param defaultValue default value if length is not recognized
     * @param context current {@link SvgDrawContext}
     *
     * @return absolute value in points
     */
    public static float parseAbsoluteVerticalLength(AbstractSvgNodeRenderer svgNodeRenderer, String length,
            float defaultValue, SvgDrawContext context) {

        float percentBaseValue = calculatePercentBaseValueIfNeeded(svgNodeRenderer, context, length, false);
        return parseAbsoluteLength(svgNodeRenderer, length, percentBaseValue, defaultValue, context);
    }

    /**
     * Parses horizontal length attribute and converts it to an absolute value.
     *
     * @param svgNodeRenderer renderer for which length should be parsed
     * @param length {@link String} for parsing
     * @param defaultValue default value if length is not recognized
     * @param context current {@link SvgDrawContext}
     *
     * @return absolute value in points
     */
    public static float parseAbsoluteHorizontalLength(AbstractSvgNodeRenderer svgNodeRenderer, String length,
            float defaultValue, SvgDrawContext context) {

        float percentBaseValue = calculatePercentBaseValueIfNeeded(svgNodeRenderer, context, length, true);
        return parseAbsoluteLength(svgNodeRenderer, length, percentBaseValue, defaultValue, context);
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
     * Extract width and height of the passed SVGNodeRenderer, defaulting to {@link SvgDrawContext#getCustomViewport()}
     * if either one is not present. If {@link SvgDrawContext#getCustomViewport()} isn't specified, than respective
     * viewbox values or browser default (if viewbox is missing) will be used.
     *
     *
     * @param svgRenderer the {@link ISvgNodeRenderer} instance that contains the renderer tree
     * @param em em value in pt
     * @param context the svg draw context
     *                
     * @return rectangle, where x,y = 0 and width and height are extracted ones by this method.
     */
    public static Rectangle extractWidthAndHeight(ISvgNodeRenderer svgRenderer, float em, SvgDrawContext context) {
        float finalWidth = 0;
        float finalHeight = 0;

        float percentHorizontalBase;
        float percentVerticalBase;
        // Here we follow https://svgwg.org/specs/integration/#svg-css-sizing with one exception:
        // we use author specified width and height (SvgDrawContext#customViewport)
        // in any case regardless viewbox existing (it is how browsers work).
        if (context.getCustomViewport() == null) {
            float[] viewBox = SvgCssUtils.parseViewBox(svgRenderer);
            if (viewBox == null) {
                percentHorizontalBase = SvgConstants.Values.DEFAULT_VIEWPORT_WIDTH;
                percentVerticalBase = SvgConstants.Values.DEFAULT_VIEWPORT_HEIGHT;
            } else {
                percentHorizontalBase = viewBox[2];
                percentVerticalBase = viewBox[3];
            }
        } else {
            percentHorizontalBase = context.getCustomViewport().getWidth();
            percentVerticalBase = context.getCustomViewport().getHeight();
        }

        float rem = context.getCssContext().getRootFontSize();

        String width = svgRenderer.getAttribute(SvgConstants.Attributes.WIDTH);
        finalWidth = calculateFinalSvgRendererLength(width, em, rem, percentHorizontalBase);

        String height = svgRenderer.getAttribute(SvgConstants.Attributes.HEIGHT);
        finalHeight = calculateFinalSvgRendererLength(height, em, rem, percentVerticalBase);

        return new Rectangle(finalWidth, finalHeight);
    }

    private static float calculateFinalSvgRendererLength(String length, float em, float rem, float percentBase) {
        if (length == null) {
            length = SvgConstants.Values.DEFAULT_WIDTH_AND_HEIGHT_VALUE;
        }
        if (CssTypesValidationUtils.isRemValue(length)) {
            return CssDimensionParsingUtils.parseRelativeValue(length, rem);
        } else if (CssTypesValidationUtils.isEmValue(length)) {
            return CssDimensionParsingUtils.parseRelativeValue(length, em);
        } else if (CssTypesValidationUtils.isPercentageValue(length)) {
            return CssDimensionParsingUtils.parseRelativeValue(length, percentBase);
        } else {
            return CssDimensionParsingUtils.parseAbsoluteLength(length);
        }
    }

    private static float calculatePercentBaseValueIfNeeded(AbstractSvgNodeRenderer svgNodeRenderer, SvgDrawContext context, String length, boolean isXAxis) {
        float percentBaseValue = 0.0F;
        if (CssTypesValidationUtils.isPercentageValue(length)) {
            Rectangle viewBox = svgNodeRenderer.getCurrentViewBox(context);
            if (viewBox == null) {
                throw new SvgProcessingException(SvgExceptionMessageConstant.ILLEGAL_RELATIVE_VALUE_NO_VIEWPORT_IS_SET);
            }
            percentBaseValue = isXAxis ? viewBox.getWidth() : viewBox.getHeight();
        }
        return percentBaseValue;
    }
}
