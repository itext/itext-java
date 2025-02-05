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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for CSS dimension parsing operations.
 */
public final class CssDimensionParsingUtils {
    private static final Logger logger = LoggerFactory.getLogger(CssDimensionParsingUtils.class);

    /**
     * Creates a new {@link CssDimensionParsingUtils} instance.
     */
    private CssDimensionParsingUtils() {
        // Empty constructor
    }

    /**
     * Parses an integer without throwing an exception if something goes wrong.
     *
     * @param str a string that might be an integer value
     * @return the integer value, or null if something went wrong
     */
    public static Integer parseInteger(String str) {
        if (str == null) {
            return null;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    /**
     * Parses a float without throwing an exception if something goes wrong.
     *
     * @param str a string that might be a float value
     * @return the float value, or null if something went wrong
     */
    public static Float parseFloat(String str) {
        if (str == null) {
            return null;
        }
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    /**
     * Parses a double without throwing an exception if something goes wrong.
     *
     * @param str a string that might be a double value
     * @return the double value, or null if something went wrong
     */
    public static Double parseDouble(String str) {
        if (str == null) {
            return null;
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    /**
     * Parses an angle with an allowed metric unit (deg, grad, rad) or numeric value (e.g. 123, 1.23,
     * .123) to rad.
     *
     * @param angle         String containing the angle to parse
     * @param defaultMetric default metric to use in case the input string does not specify a metric
     * @return the angle in radians
     */
    public static float parseAngle(String angle, String defaultMetric) {
        int pos = CssDimensionParsingUtils.determinePositionBetweenValueAndUnit(angle);

        if (pos == 0) {
            if (angle == null) {
                angle = "null";
            }
            throw new StyledXMLParserException(MessageFormatUtil.format(StyledXMLParserException.NAN, angle));
        }

        float floatValue  = Float.parseFloat(angle.substring(0, pos));
        String unit = angle.substring(pos);

        // Degrees
        if (unit.startsWith(CommonCssConstants.DEG) || unit.equals("") && CommonCssConstants.DEG
                .equals(defaultMetric)) {
            return (float) Math.PI * floatValue / 180f;
        }
        // Grads
        if (unit.startsWith(CommonCssConstants.GRAD) || unit.equals("") && CommonCssConstants.GRAD
                .equals(defaultMetric)) {
            return (float) Math.PI * floatValue / 200f;
        }
        // Radians
        if (unit.startsWith(CommonCssConstants.RAD) || unit.equals("") && CommonCssConstants.RAD
                .equals(defaultMetric)) {
            return floatValue;
        }

        logger.error(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.UNKNOWN_METRIC_ANGLE_PARSED,
                unit.equals("") ? defaultMetric : unit));
        return floatValue ;
    }

    /**
     * Parses a angle with an allowed metric unit (deg, grad, rad) or numeric value (e.g. 123, 1.23,
     * .123) to rad. Default metric is degrees
     *
     * @param angle String containing the angle to parse
     * @return the angle in radians
     */
    public static float parseAngle(String angle) {
        return parseAngle(angle, CommonCssConstants.DEG);
    }

    /**
     * Parses an aspect ratio into an array with two integers.
     *
     * @param str a string that might contain two integer values
     * @return the aspect ratio as an array of two integer values
     */
    public static int[] parseAspectRatio(String str) {
        int indexOfSlash = str.indexOf('/');
        try {
            int first = Integer.parseInt(str.substring(0, indexOfSlash));
            int second = Integer.parseInt(str.substring(indexOfSlash + 1));
            return new int[]{first, second};
        } catch (NumberFormatException | NullPointerException exc) {
            return null;
        }
    }

    /**
     * Parses a length with an allowed metric unit (px, pt, in, cm, mm, pc, q) or numeric value (e.g. 123, 1.23,
     * .123) to pt.<br>
     * A numeric value (without px, pt, etc in the given length string) is considered to be in the default metric that
     * was given.
     *
     * @param length        the string containing the length
     * @param defaultMetric the string containing the metric if it is possible that the length string does not contain
     *                      one. If null the length is considered to be in px as is default in HTML/CSS
     * @return parsed value
     */
    public static float parseAbsoluteLength(String length, String defaultMetric) {
        int pos = CssDimensionParsingUtils.determinePositionBetweenValueAndUnit(length);

        if (pos == 0) {
            if (length == null) {
                length = "null";
            }
            throw new StyledXMLParserException(MessageFormatUtil.format(StyledXMLParserException.NAN, length));
        }

        // Use double type locally to have better precision of the result after applying arithmetic operations
        double f = Double.parseDouble(length.substring(0, pos));
        String unit = length.substring(pos);

        //points
        if (unit.startsWith(CommonCssConstants.PT) || unit.equals("") && defaultMetric.equals(CommonCssConstants.PT)) {
            return (float) f;
        }
        // inches
        if (unit.startsWith(CommonCssConstants.IN) || (unit.equals("") && defaultMetric
                .equals(CommonCssConstants.IN))) {
            return (float) (f * 72);
        }
        // centimeters
        else if (unit.startsWith(CommonCssConstants.CM) || (unit.equals("") && defaultMetric
                .equals(CommonCssConstants.CM))) {
            return (float) ((f / 2.54) * 72);
        }
        // quarter of a millimeter (1/40th of a centimeter).
        else if (unit.startsWith(CommonCssConstants.Q) || (unit.equals("") && defaultMetric
                .equals(CommonCssConstants.Q))) {
            return (float) ((f / 2.54) * 72 / 40);
        }
        // millimeters
        else if (unit.startsWith(CommonCssConstants.MM) || (unit.equals("") && defaultMetric
                .equals(CommonCssConstants.MM))) {
            return (float) ((f / 25.4) * 72);
        }
        // picas
        else if (unit.startsWith(CommonCssConstants.PC) || (unit.equals("") && defaultMetric
                .equals(CommonCssConstants.PC))) {
            return (float) (f * 12);
        }
        // pixels (1px = 0.75pt).
        else if (unit.startsWith(CommonCssConstants.PX) || (unit.equals("") && defaultMetric
                .equals(CommonCssConstants.PX))) {
            return (float) (f * 0.75);
        }

        logger.error(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED,
                unit.equals("") ? defaultMetric : unit));
        return (float) f;
    }

    /**
     * Parses the absolute length.
     *
     * @param length the length as a string
     * @return the length as a float
     */
    public static float parseAbsoluteLength(String length) {
        return parseAbsoluteLength(length, CommonCssConstants.PX);
    }

    /**
     * Parses an relative value based on the base value that was given, in the metric unit of the base value.<br>
     * (e.g. margin=10% should be based on the page width, so if an A4 is used, the margin = 0.10*595.0 = 59.5f)
     *
     * @param relativeValue in %, em or ex
     * @param baseValue     the value the returned float is based on
     * @return the parsed float in the metric unit of the base value
     */
    public static float parseRelativeValue(final String relativeValue, final float baseValue) {
        int pos = CssDimensionParsingUtils.determinePositionBetweenValueAndUnit(relativeValue);
        if (pos == 0) {
            return 0f;
        }
        // Use double type locally to have better precision of the result after applying arithmetic operations
        double f = Double.parseDouble(relativeValue.substring(0, pos));
        String unit = relativeValue.substring(pos);
        if (unit.startsWith(CommonCssConstants.PERCENTAGE)) {
            f = baseValue * f / 100;
        } else if (unit.startsWith(CommonCssConstants.EM) || unit.startsWith(CommonCssConstants.REM)) {
            f = baseValue * f;
        } else if (unit.startsWith(CommonCssConstants.EX)) {
            f = baseValue * f / 2;
        }
        return (float) f;
    }

    /**
     * Convenience method for parsing a value to pt. Possible values are: <ul>
     * <li>a numeric value in pixels (e.g. 123, 1.23, .123),
     * <li>a value with a metric unit (px, in, cm, mm, pc or pt) attached to it,
     * <li>or a value with a relative value (%, em, ex).
     * </ul>
     *
     * @param value    the value
     * @param emValue  the em value
     * @param remValue the root em value
     * @return the unit value
     */
    public static UnitValue parseLengthValueToPt(final String value, final float emValue, final float remValue) {
        // TODO (DEVSIX-3596) Add support of 'lh' 'ch' units and viewport-relative units
        if (CssTypesValidationUtils.isMetricValue(value) || CssTypesValidationUtils.isNumber(value)) {
            return new UnitValue(UnitValue.POINT, parseAbsoluteLength(value));
        } else if (value != null && value.endsWith(CommonCssConstants.PERCENTAGE)) {
            return new UnitValue(UnitValue.PERCENT, Float.parseFloat(value.substring(0, value.length() - 1)));
        } else if (CssTypesValidationUtils.isRemValue(value)) {
            return new UnitValue(UnitValue.POINT, parseRelativeValue(value, remValue));
        } else if (CssTypesValidationUtils.isRelativeValue(value)) {
            return new UnitValue(UnitValue.POINT, parseRelativeValue(value, emValue));
        }
        return null;
    }

    /**
     * Parses a flex value "xfr" to x.
     *
     * @param value String containing the flex value to parse
     *
     * @return the flex value as a float
     */
    public static Float parseFlex(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();

        if (value.endsWith(CommonCssConstants.FR)) {
            value = value.substring(0, value.length() - CommonCssConstants.FR.length());
            if (CssTypesValidationUtils.isNumber(value)) {
                return Float.parseFloat(value);
            }
        }
        return null;
    }

    /**
     * Parse length attributes.
     *
     * @param length {@link String} for parsing
     * @param percentBaseValue the value on which percent length is based on
     * @param defaultValue default value if length is not recognized
     * @param fontSize font size of the current element
     * @param rootFontSize root element font size
     * @return absolute value in points
     */
    public static float parseLength(String length, float percentBaseValue, float defaultValue,
            float fontSize, float rootFontSize) {
        if (CssTypesValidationUtils.isPercentageValue(length)) {
            return CssDimensionParsingUtils.parseRelativeValue(length, percentBaseValue);
        } else {
            UnitValue unitValue = CssDimensionParsingUtils.parseLengthValueToPt(length, fontSize, rootFontSize);
            if (unitValue != null && unitValue.isPointValue()) {
                return unitValue.getValue();
            } else {
                return defaultValue;
            }
        }
    }

    /**
     * Parses the absolute font size.
     * <p>
     * A numeric value (without px, pt, etc in the given length string) is considered to be in the default metric that
     * was given.
     *
     * @param fontSizeValue the font size value as a {@link String}
     * @param defaultMetric the string containing the metric if it is possible that the length string does not contain
     *                      one. If null the length is considered to be in px as is default in HTML/CSS.
     * @return the font size value as a {@code float}
     */
    public static float parseAbsoluteFontSize(String fontSizeValue, String defaultMetric) {
        if (null != fontSizeValue && CommonCssConstants.FONT_ABSOLUTE_SIZE_KEYWORDS_VALUES.containsKey(fontSizeValue)) {
            fontSizeValue = CommonCssConstants.FONT_ABSOLUTE_SIZE_KEYWORDS_VALUES.get(fontSizeValue);
        }
        try {
            /* Styled XML Parser will throw an exception when it can't parse the given value
               but in html2pdf, we want to fall back to the default value of 0
             */
            return CssDimensionParsingUtils.parseAbsoluteLength(fontSizeValue, defaultMetric);
        } catch (StyledXMLParserException sxpe) {
            return 0f;
        }
    }

    /**
     * Parses the absolute font size.
     * <p>
     * A numeric value (without px, pt, etc in the given length string) is considered to be in the px.
     *
     * @param fontSizeValue the font size value as a {@link String}
     * @return the font size value as a {@code float}
     */
    public static float parseAbsoluteFontSize(String fontSizeValue) {
        return parseAbsoluteFontSize(fontSizeValue, CommonCssConstants.PX);
    }

    /**
     * Parses the relative font size.
     *
     * @param relativeFontSizeValue the relative font size value as a {@link String}
     * @param baseValue             the base value
     * @return the relative font size value as a {@code float}
     */
    public static float parseRelativeFontSize(final String relativeFontSizeValue, final float baseValue) {
        if (CommonCssConstants.SMALLER.equals(relativeFontSizeValue)) {
            return (float) (baseValue / 1.2);
        } else if (CommonCssConstants.LARGER.equals(relativeFontSizeValue)) {
            return (float) (baseValue * 1.2);
        }
        return CssDimensionParsingUtils.parseRelativeValue(relativeFontSizeValue, baseValue);
    }

    /**
     * Parses the border radius of specific corner.
     *
     * @param specificBorderRadius string that defines the border radius of specific corner
     * @param emValue              the em value
     * @param remValue             the root em value
     * @return an array of {@link UnitValue UnitValues} that define horizontal and vertical border radius values
     */
    public static UnitValue[] parseSpecificCornerBorderRadius(String specificBorderRadius, final float emValue, final float remValue) {
        if (null == specificBorderRadius) {
            return null;
        }
        UnitValue[] cornerRadii = new UnitValue[2];
        String[] props = specificBorderRadius.split("\\s+");
        cornerRadii[0] = parseLengthValueToPt(props[0], emValue, remValue);
        cornerRadii[1] = 2 == props.length ? parseLengthValueToPt(props[1], emValue, remValue) : cornerRadii[0];

        return cornerRadii;
    }

    /**
     * Parses the resolution.
     *
     * @param resolutionStr the resolution as a string
     * @return a value in dpi
     */
    public static float parseResolution(String resolutionStr) {
        int pos = CssDimensionParsingUtils.determinePositionBetweenValueAndUnit(resolutionStr);
        if (pos == 0) {
            return 0f;
        }
        double f = Double.parseDouble(resolutionStr.substring(0, pos));
        String unit = resolutionStr.substring(pos);
        if (unit.startsWith(CommonCssConstants.DPCM)) {
            f *= 2.54;
        } else if (unit.startsWith(CommonCssConstants.DPPX)) {
            f *= 96;
        } else if (!unit.startsWith(CommonCssConstants.DPI)) {
            throw new StyledXMLParserException(StyledXmlParserLogMessageConstant.INCORRECT_RESOLUTION_UNIT_VALUE);
        }

        return (float) f;
    }

    /**
     * Parses either RGBA or CMYK color.
     *
     * @param colorValue the color value
     * @return an RGBA or CMYK value expressed as an array with four float values
     */
    public static TransparentColor parseColor(String colorValue) {
        Color device = null;
        float opacity = 1;
        float[] color = WebColors.getRGBAColor(colorValue);
        if (color == null) {
            color = WebColors.getCMYKArray(colorValue);
        } else {
            device = new DeviceRgb(color[0], color[1], color[2]);
            if (color.length == 4) {
                opacity = color[3];
            }
        }
        if (color == null) {
            color = new float[] {0, 0, 0, 1};
            device = new DeviceRgb(0, 0, 0);
        } else if (device == null) {
            device = new DeviceCmyk(color[0], color[1], color[2], color[3]);
            if (color.length == 5) {
                opacity = color[4];
            }
        }
        return new TransparentColor(device, opacity);
    }


    /**
     * Parses the RGBA color.
     *
     * @param colorValue the color value
     * @return an RGBA value expressed as an array with four float values
     */
    public static float[] parseRgbaColor(String colorValue) {
        float[] rgbaColor = WebColors.getRGBAColor(colorValue);
        if (rgbaColor == null) {
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.COLOR_NOT_PARSED, colorValue));
            rgbaColor = new float[] {0, 0, 0, 1};
        }
        return rgbaColor;
    }

    /**
     * Method used in preparation of splitting a string containing a numeric value with a metric unit (e.g. 18px, 9pt,
     * 6cm, etc).<br><br>
     * Determines the position between digits and affiliated characters ('+','-','0-9' and '.') and all other
     * characters.<br>
     * e.g. string "16px" will return 2, string "0.5em" will return 3 and string '-8.5mm' will return 4.
     *
     * @param string containing a numeric value with a metric unit
     * @return int position between the numeric value and unit or 0 if string is null or string started with a
     * non-numeric value.
     */
    public static int determinePositionBetweenValueAndUnit(String string) {
        if (string == null) {
            return 0;
        }
        int pos = 0;
        while (pos < string.length()) {
            if (string.charAt(pos) == '+' ||
                    string.charAt(pos) == '-' ||
                    string.charAt(pos) == '.' ||
                    isDigit(string.charAt(pos)) ||
                    isExponentNotation(string, pos)) {
                pos++;
            } else {
                break;
            }
        }
        return pos;
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isExponentNotation(String s, int index) {
        return index < s.length() && Character.toLowerCase(s.charAt(index)) == 'e' &&
                // e.g. 12e5
                (index + 1 < s.length() && isDigit(s.charAt(index + 1)) ||
                        // e.g. 12e-5, 12e+5
                        index + 2 < s.length() && (s.charAt(index + 1) == '-' || s.charAt(index + 1) == '+') && isDigit(s.charAt(index + 2)));
    }
}
