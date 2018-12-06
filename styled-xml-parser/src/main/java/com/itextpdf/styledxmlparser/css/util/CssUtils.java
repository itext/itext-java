/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.layout.font.Range;
import com.itextpdf.layout.font.RangeBuilder;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for CSS operations.
 */
public class CssUtils {

    private static final String[] METRIC_MEASUREMENTS = new String[]{CommonCssConstants.PX, CommonCssConstants.IN, CommonCssConstants.CM, CommonCssConstants.MM, CommonCssConstants.PC, CommonCssConstants.PT};
    private static final String[] RELATIVE_MEASUREMENTS = new String[]{CommonCssConstants.PERCENTAGE, CommonCssConstants.EM, CommonCssConstants.EX, CommonCssConstants.REM};
    private static final float EPSILON = 1e-6f;

    /**
     * Creates a new {@link CssUtils} instance.
     */
    private CssUtils() {
    }

    /**
     * Normalizes a CSS property.
     *
     * @param str the property
     * @return the normalized property
     */
    public static String normalizeCssProperty(String str) {
        return str == null ? null : CssPropertyNormalizer.normalize(str);
    }

    /**
     * Removes double spaces and trims a string.
     *
     * @param str the string
     * @return the string without the unnecessary spaces
     */
    public static String removeDoubleSpacesAndTrim(String str) {
        String[] parts = str.split("\\s");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                if (sb.length() != 0) {
                    sb.append(" ");
                }
                sb.append(part);
            }
        }
        return sb.toString();
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
     * @param length        the string containing the length.
     * @param defaultMetric the string containing the metric if it is possible that the length string does not contain
     *                      one. If null the length is considered to be in px as is default in HTML/CSS.
     * @return parsed value
     */
    public static float parseAbsoluteLength(String length, String defaultMetric) {
        int pos = determinePositionBetweenValueAndUnit(length);

        if (pos == 0) {
            if (length == null) {
                length = "null";
            }
            throw new StyledXMLParserException(MessageFormatUtil.format(LogMessageConstant.NAN, length));
        }

        float f = Float.parseFloat(length.substring(0, pos));
        String unit = length.substring(pos);

        //points
        if (unit.startsWith(CommonCssConstants.PT) || unit.equals("") && defaultMetric.equals(CommonCssConstants.PT)) {
            return f;
        }
        // inches
        if (unit.startsWith(CommonCssConstants.IN) || (unit.equals("") && defaultMetric.equals(CommonCssConstants.IN))) {
            return f * 72f;
        }
        // centimeters
        else if (unit.startsWith(CommonCssConstants.CM) || (unit.equals("") && defaultMetric.equals(CommonCssConstants.CM))) {
            return (f / 2.54f) * 72f;
        }
        // quarter of a millimeter (1/40th of a centimeter).
        else if (unit.startsWith(CommonCssConstants.Q) || (unit.equals("") && defaultMetric.equals(CommonCssConstants.Q))) {
            return (f / 2.54f) * 72f / 40;
        }
        // millimeters
        else if (unit.startsWith(CommonCssConstants.MM) || (unit.equals("") && defaultMetric.equals(CommonCssConstants.MM))) {
            return (f / 25.4f) * 72f;
        }
        // picas
        else if (unit.startsWith(CommonCssConstants.PC) || (unit.equals("") && defaultMetric.equals(CommonCssConstants.PC))) {
            return f * 12f;
        }
        // pixels (1px = 0.75pt).
        else if (unit.startsWith(CommonCssConstants.PX) || (unit.equals("") && defaultMetric.equals(CommonCssConstants.PX))) {
            return f * 0.75f;
        }

        Logger logger = LoggerFactory.getLogger(CssUtils.class);
        logger.error(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, unit.equals("") ? defaultMetric : unit));
        return f;
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
     * @param relativeValue in %, em or ex.
     * @param baseValue     the value the returned float is based on.
     * @return the parsed float in the metric unit of the base value.
     */
    public static float parseRelativeValue(final String relativeValue, final float baseValue) {
        int pos = determinePositionBetweenValueAndUnit(relativeValue);
        if (pos == 0)
            return 0f;
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
     * <li>a numeric value in pixels (e.g. 123, 1.23, .123),</li>
     * <li>a value with a metric unit (px, in, cm, mm, pc or pt) attached to it,</li>
     * <li>or a value with a relative value (%, em, ex).</li>
     * </ul>
     *
     * @param value    the value
     * @param emValue  the em value
     * @param remValue the root em value
     * @return the unit value
     */
    public static UnitValue parseLengthValueToPt(final String value, final float emValue, final float remValue) {
        if (isMetricValue(value) || isNumericValue(value)) {
            return new UnitValue(UnitValue.POINT, parseAbsoluteLength(value));
        } else if (value != null && value.endsWith(CommonCssConstants.PERCENTAGE)) {
            return new UnitValue(UnitValue.PERCENT, Float.parseFloat(value.substring(0, value.length() - 1)));
        } else if (isRemValue(value)) {
            return new UnitValue(UnitValue.POINT, parseRelativeValue(value, remValue));
        } else if (isRelativeValue(value)) {
            return new UnitValue(UnitValue.POINT, parseRelativeValue(value, emValue));
        }
        return null;
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
            return CssUtils.parseAbsoluteLength(fontSizeValue, defaultMetric);
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
        return CssUtils.parseRelativeValue(relativeFontSizeValue, baseValue);
    }

    /**
     * Parses the border radius of specific corner.
     *
     * @param specificBorderRadius string that defines the border radius of specific corner.
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
     * @return a value in dpi (currently)
     */
    // TODO change default units? If so, change MediaDeviceDescription#resolutoin as well
    public static float parseResolution(String resolutionStr) {
        int pos = determinePositionBetweenValueAndUnit(resolutionStr);
        if (pos == 0)
            return 0f;
        float f = Float.parseFloat(resolutionStr.substring(0, pos));
        String unit = resolutionStr.substring(pos);
        if (unit.startsWith(CommonCssConstants.DPCM)) {
            f *= 2.54f;
        } else if (unit.startsWith(CommonCssConstants.DPPX)) {
            f *= 96;
        }
        return f;
    }

    /**
     * Method used in preparation of splitting a string containing a numeric value with a metric unit (e.g. 18px, 9pt, 6cm, etc).<br><br>
     * Determines the position between digits and affiliated characters ('+','-','0-9' and '.') and all other characters.<br>
     * e.g. string "16px" will return 2, string "0.5em" will return 3 and string '-8.5mm' will return 4.
     *
     * @param string containing a numeric value with a metric unit
     * @return int position between the numeric value and unit or 0 if string is null or string started with a non-numeric value.
     */
    private static int determinePositionBetweenValueAndUnit(String string) {
        if (string == null)
            return 0;
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

    /**
     * /**
     * Checks whether a string contains an allowed metric unit in HTML/CSS; px, in, cm, mm, pc or pt.
     *
     * @param value the string that needs to be checked.
     * @return boolean true if value contains an allowed metric value.
     */
    public static boolean isMetricValue(final String value) {
        if (value == null) {
            return false;
        }
        for (String metricPostfix : METRIC_MEASUREMENTS) {
            if (value.endsWith(metricPostfix) && isNumericValue(value.substring(0, value.length() - metricPostfix.length()).trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string contains an allowed value relative to previously set value.
     *
     * @param value the string that needs to be checked.
     * @return boolean true if value contains an allowed metric value.
     */
    public static boolean isRelativeValue(final String value) {
        if (value == null) {
            return false;
        }
        for (String relativePostfix : RELATIVE_MEASUREMENTS) {
            if (value.endsWith(relativePostfix) && isNumericValue(value.substring(0, value.length() - relativePostfix.length()).trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string contains an allowed value relative to previously set root value.
     *
     * @param value the string that needs to be checked.
     * @return boolean true if value contains an allowed metric value.
     */
    public static boolean isRemValue(final String value) {
        return value != null && value.endsWith(CommonCssConstants.REM) && isNumericValue(value.substring(0, value.length() - CommonCssConstants.REM.length()).trim());
    }

    /**
     * Checks whether a string matches a numeric value (e.g. 123, 1.23, .123). All these metric values are allowed in HTML/CSS.
     *
     * @param value the string that needs to be checked.
     * @return boolean true if value contains an allowed metric value.
     */
    public static boolean isNumericValue(final String value) {
        return value != null && (value.matches("^[-+]?\\d\\d*\\.\\d*$") || value.matches("^[-+]?\\d\\d*$") || value.matches("^[-+]?\\.\\d\\d*$"));
    }

    /**
     * Parses {@code url("file.jpg")} to {@code file.jpg}.
     *
     * @param url the url attribute to parse
     * @return the parsed url. Or original url if not wrappend in url()
     */
    public static String extractUrl(final String url) {
        String str = null;
        if (url.startsWith("url")) {
            String urlString = url.substring(3).trim().replace("(", "").replace(")", "").trim();
            if (urlString.startsWith("'") && urlString.endsWith("'")) {
                str = urlString.substring(urlString.indexOf("'") + 1, urlString.lastIndexOf("'"));
            } else if (urlString.startsWith("\"") && urlString.endsWith("\"")) {
                str = urlString.substring(urlString.indexOf('"') + 1, urlString.lastIndexOf('"'));
            } else {
                str = urlString;
            }
        } else {
            // assume it's an url without wrapping in "url()"
            str = url;
        }
        return str;
    }

    /**
     * Checks if a data is base 64 encoded.
     *
     * @param data the data
     * @return true, if the data is base 64 encoded
     */
    public static boolean isBase64Data(String data) {
        return data.matches("^data:([^\\s]*);base64,([^\\s]*)");
    }

    /**
     * Find the next unescaped character.
     *
     * @param source     a source
     * @param ch         the character to look for
     * @param startIndex where to start looking
     * @return the position of the next unescaped character
     */
    public static int findNextUnescapedChar(String source, char ch, int startIndex) {
        int symbolPos = source.indexOf(ch, startIndex);
        if (symbolPos == -1) {
            return -1;
        }
        int afterNoneEscapePos = symbolPos;
        while (afterNoneEscapePos > 0 && source.charAt(afterNoneEscapePos - 1) == '\\') {
            --afterNoneEscapePos;
        }
        return (symbolPos - afterNoneEscapePos) % 2 == 0 ? symbolPos : findNextUnescapedChar(source, ch, symbolPos + 1);
    }

    /**
     * Checks if a value is a color property.
     *
     * @param value the value
     * @return true, if the value contains a color property
     */
    public static boolean isColorProperty(String value) {
        return value.contains("rgb(") || value.contains("rgba(") || value.contains("#")
                || WebColors.NAMES.containsKey(value.toLowerCase()) || CommonCssConstants.TRANSPARENT.equals(value);
    }

    /**
     * Helper method for comparing floating point numbers
     *
     * @param d1 first float to compare
     * @param d2 second float to compare
     * @return True if both floats are equal within a Epsilon defined in this class, false otherwise
     */
    public static boolean compareFloats(double d1, double d2) {
        return (Math.abs(d1 - d2) < EPSILON);
    }

    /**
     * Helper method for comparing floating point numbers
     *
     * @param f1 first float to compare
     * @param f2 second float to compare
     * @return True if both floats are equal within a Epsilon defined in this class, false otherwise
     */
    public static boolean compareFloats(float f1, float f2) {
        return (Math.abs(f1 - f2) < EPSILON);
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
            Logger logger = LoggerFactory.getLogger(CssUtils.class);
            logger.error(MessageFormatUtil.format(com.itextpdf.io.LogMessageConstant.COLOR_NOT_PARSED, colorValue));
            rgbaColor = new float[]{0, 0, 0, 1};
        }
        return rgbaColor;
    }

    /**
     * Parses the unicode range.
     *
     * @param unicodeRange the string which stores the unicode range
     * @return the unicode range as a {@link Range} object
     */
    public static Range parseUnicodeRange(String unicodeRange) {
        String[] ranges = unicodeRange.split(",");
        RangeBuilder builder = new RangeBuilder();
        for (String range : ranges) {
            if (!addRange(builder, range)) {
                return null;
            }
        }
        return builder.create();
    }

    private static boolean addRange(RangeBuilder builder, String range) {
        range = range.trim();
        if (range.matches("[uU]\\+[0-9a-fA-F?]{1,6}(-[0-9a-fA-F]{1,6})?")) {
            String[] parts = range.substring(2, range.length()).split("-");
            if (1 == parts.length) {
                if (parts[0].contains("?")) {
                    return addRange(builder, parts[0].replace('?', '0'), parts[0].replace('?', 'F'));
                } else {
                    return addRange(builder, parts[0], parts[0]);
                }
            } else {
                return addRange(builder, parts[0], parts[1]);
            }
        }
        return false;
    }

    private static boolean addRange(RangeBuilder builder, String left, String right) {
        int l = Integer.parseInt(left, 16);
        int r = Integer.parseInt(right, 16);
        if (l > r || r > 1114111) { // Although Firefox follows the spec (and therefore the second condition), it seems it's ignored in Chrome or Edge
            return false;
        }
        builder.addRange(l, r);
        return true;
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isExponentNotation(String s, int index) {
        return index < s.length() && s.charAt(index) == 'e' &&
                // e.g. 12e5
                (index + 1 < s.length() && isDigit(s.charAt(index + 1)) ||
                // e.g. 12e-5, 12e+5
                 index + 2 < s.length() && (s.charAt(index + 1) == '-' || s.charAt(index + 1) == '+') && isDigit(s.charAt(index + 2)));
    }
}
