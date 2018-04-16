/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CssConstants;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for CSS operations.
 */
public class CssUtils {

    private static final String[] METRIC_MEASUREMENTS = new String[] {CssConstants.PX, CssConstants.IN, CssConstants.CM, CssConstants.MM, CssConstants.PC, CssConstants.PT};
    private static final String[] RELATIVE_MEASUREMENTS = new String[] {CssConstants.PERCENTAGE, CssConstants.EM, CssConstants.EX, CssConstants.REM};

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
            if ( length == null ) {
                length = "null";
            }
            throw new StyledXMLParserException(MessageFormatUtil.format(LogMessageConstant.NAN, length));
        }

        float f = Float.parseFloat(length.substring(0, pos));
        String unit = length.substring(pos);

        //points
        if (unit.startsWith(CssConstants.PT) || unit.equals("") && defaultMetric.equals(CssConstants.PT)) {
            return f;
        }
        // inches
        if (unit.startsWith(CssConstants.IN) || (unit.equals("") && defaultMetric.equals(CssConstants.IN))) {
            return f * 72f;
        }
        // centimeters
        else if (unit.startsWith(CssConstants.CM) || (unit.equals("") && defaultMetric.equals(CssConstants.CM))) {
            return (f / 2.54f) * 72f;
        }
        // quarter of a millimeter (1/40th of a centimeter).
        else if (unit.startsWith(CssConstants.Q) || (unit.equals("") && defaultMetric.equals(CssConstants.Q))) {
            return (f / 2.54f) * 72f / 40;
        }
        // millimeters
        else if (unit.startsWith(CssConstants.MM) || (unit.equals("") && defaultMetric.equals(CssConstants.MM))) {
            return (f / 25.4f) * 72f;
        }
        // picas
        else if (unit.startsWith(CssConstants.PC) || (unit.equals("") && defaultMetric.equals(CssConstants.PC))) {
            return f * 12f;
        }
        // pixels (1px = 0.75pt).
        else if (unit.startsWith(CssConstants.PX) || (unit.equals("") && defaultMetric.equals(CssConstants.PX))) {
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
        return parseAbsoluteLength(length, CssConstants.PX);
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
        if (unit.startsWith(CssConstants.PERCENTAGE)) {
            f = baseValue * f / 100;
        } else if (unit.startsWith(CssConstants.EM) || unit.startsWith(CssConstants.REM)) {
            f = baseValue * f;
        } else if (unit.startsWith(CssConstants.EX)) {
            f = baseValue * f / 2;
        }
        return (float)f;
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
        if (unit.startsWith(CssConstants.DPCM)) {
            f *= 2.54f;
        } else if (unit.startsWith(CssConstants.DPPX)) {
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
                    string.charAt(pos) >= '0' && string.charAt(pos) <= '9') {
                pos++;
            } else {
                break;
            }
        }
        return pos;
    }

    /**

    /**
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
        return value != null && value.endsWith(CssConstants.REM) && isNumericValue(value.substring(0, value.length() - CssConstants.REM.length()).trim());
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
     * @param source a source
     * @param ch the character to look for
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
        /*
        return value.contains("rgb(") || value.contains("rgba(") || value.contains("#")
                || WebColors.NAMES.containsKey(value.toLowerCase()) || CssConstants.TRANSPARENT.equals(value);
*/
        //TODO re-add Webcolors by either creating a dependency on kernel or moving webcolors to io
        return value.contains("rgb(") || value.contains("rgba(") || value.contains("#")
                || CssConstants.TRANSPARENT.equals(value);
    }
}
