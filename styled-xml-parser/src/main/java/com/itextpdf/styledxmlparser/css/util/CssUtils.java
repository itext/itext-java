/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

import com.itextpdf.layout.font.RangeBuilder;
import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.layout.font.Range;
import com.itextpdf.layout.property.BlendMode;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.Token;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for CSS operations.
 */
public class CssUtils {
    // TODO (DEVSIX-3596) The list of the font-relative measurements is not full.
    //  Add 'ch' units to array and move this array to the CommonCssConstants
    @Deprecated
    private static final String[] FONT_RELATIVE_MEASUREMENTS_VALUES = new String[] {CommonCssConstants.EM, CommonCssConstants.EX, CommonCssConstants.REM};

    private static final float EPSILON = 1e-6f;

    private static final  Logger logger = LoggerFactory.getLogger(CssUtils.class);

    /**
     * Creates a new {@link CssUtils} instance.
     */
    private CssUtils() {
        // Empty constructor
    }

    /**
     * Splits the provided {@link String} by comma with respect of brackets.
     *
     * @param value to split
     * @return the {@link List} of split result
     */
    public static List<String> splitStringWithComma(final String value) {
        return splitString(value, ',', new EscapeGroup('(', ')'));
    }

    /**
     * Splits the provided {@link String} by split character with respect of escape characters.
     *
     * @param value value to split
     * @param splitChar character to split the String
     * @param escapeCharacters escape characters
     * @return the {@link List} of split result
     */
    public static List<String> splitString(String value, char splitChar, EscapeGroup... escapeCharacters) {
        if (value == null) {
            return new ArrayList<>();
        }
        final List<String> resultList = new ArrayList<>();
        int lastSplitChar = 0;
        for (int i = 0; i < value.length(); ++i) {
            final char currentChar = value.charAt(i);
            boolean isEscaped = false;
            for (final EscapeGroup character : escapeCharacters) {
                if (currentChar == splitChar) {
                    isEscaped = isEscaped || character.isEscaped();
                } else {
                    character.processCharacter(currentChar);
                }
            }
            if (currentChar == splitChar && !isEscaped) {
                resultList.add(value.substring(lastSplitChar, i));
                lastSplitChar = i + 1;
            }
        }
        final String lastToken = value.substring(lastSplitChar);
        if (!lastToken.isEmpty()) {
            resultList.add(lastToken);
        }
        return resultList;
    }

    /**
     * Parses the given css blend mode value. If the argument is {@code null} or an unknown blend
     * mode, then the default css {@link BlendMode#NORMAL} value would be returned.
     *
     * @param cssValue the value to parse
     * @return the {@link BlendMode} instance representing the parsed value
     */
    public static BlendMode parseBlendMode(String cssValue) {
        if (cssValue == null) {
            return BlendMode.NORMAL;
        }

        switch (cssValue) {
            case CommonCssConstants.MULTIPLY:
                return BlendMode.MULTIPLY;
            case CommonCssConstants.SCREEN:
                return BlendMode.SCREEN;
            case CommonCssConstants.OVERLAY:
                return BlendMode.OVERLAY;
            case CommonCssConstants.DARKEN:
                return BlendMode.DARKEN;
            case CommonCssConstants.LIGHTEN:
                return BlendMode.LIGHTEN;
            case CommonCssConstants.COLOR_DODGE:
                return BlendMode.COLOR_DODGE;
            case CommonCssConstants.COLOR_BURN:
                return BlendMode.COLOR_BURN;
            case CommonCssConstants.HARD_LIGHT:
                return BlendMode.HARD_LIGHT;
            case CommonCssConstants.SOFT_LIGHT:
                return BlendMode.SOFT_LIGHT;
            case CommonCssConstants.DIFFERENCE:
                return BlendMode.DIFFERENCE;
            case CommonCssConstants.EXCLUSION:
                return BlendMode.EXCLUSION;
            case CommonCssConstants.HUE:
                return BlendMode.HUE;
            case CommonCssConstants.SATURATION:
                return BlendMode.SATURATION;
            case CommonCssConstants.COLOR:
                return BlendMode.COLOR;
            case CommonCssConstants.LUMINOSITY:
                return BlendMode.LUMINOSITY;
            case CommonCssConstants.NORMAL:
            default:
                return BlendMode.NORMAL;
        }
    }

    /**
     * Extracts shorthand properties as list of string lists from a string, where the top level
     * list is shorthand property and the lower level list is properties included in shorthand property.
     *
     * @param str the source string with shorthand properties
     * @return the list of string lists
     */
    public static List<List<String>> extractShorthandProperties(String str) {
        List<List<String>> result = new ArrayList<>();
        List<String> currentLayer = new ArrayList<>();
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(str);

        Token currentToken = tokenizer.getNextValidToken();
        while (currentToken != null) {
            if (currentToken.getType() == TokenType.COMMA) {
                result.add(currentLayer);
                currentLayer = new ArrayList<>();
            } else {
                currentLayer.add(currentToken.getValue());
            }
            currentToken = tokenizer.getNextValidToken();
        }
        result.add(currentLayer);

        return result;
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
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseInteger(String)} instead
     */
    @Deprecated
    public static Integer parseInteger(String str) {
        return CssDimensionParsingUtils.parseInteger(str);
    }

    /**
     * Parses a float without throwing an exception if something goes wrong.
     *
     * @param str a string that might be a float value
     * @return the float value, or null if something went wrong
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseFloat(String)} instead
     */
    @Deprecated
    public static Float parseFloat(String str) {
        return CssDimensionParsingUtils.parseFloat(str);
    }

    /**
     * Parses a double without throwing an exception if something goes wrong.
     *
     * @param str a string that might be a double value
     * @return the double value, or null if something went wrong
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseDouble(String)} instead
     */
    @Deprecated
    public static Double parseDouble(String str) {
        return CssDimensionParsingUtils.parseDouble(str);
    }

    /**
     * Parses an angle with an allowed metric unit (deg, grad, rad) or numeric value (e.g. 123, 1.23,
     * .123) to rad.
     *
     * @param angle         String containing the angle to parse
     * @param defaultMetric default metric to use in case the input string does not specify a metric
     * @return the angle in radians
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseAngle(String, String)} instead
     */
    @Deprecated
    public static float parseAngle(String angle, String defaultMetric) {
        return CssDimensionParsingUtils.parseAngle(angle, defaultMetric);
    }

    /**
     * Parses a angle with an allowed metric unit (deg, grad, rad) or numeric value (e.g. 123, 1.23,
     * .123) to rad. Default metric is degrees
     *
     * @param angle String containing the angle to parse
     * @return the angle in radians
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseAngle(String)} instead
     */
    @Deprecated
    public static float parseAngle(String angle) {
        return CssDimensionParsingUtils.parseAngle(angle);
    }

    /**
     * Parses an aspect ratio into an array with two integers.
     *
     * @param str a string that might contain two integer values
     * @return the aspect ratio as an array of two integer values
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseAspectRatio(String)} instead
     */
    @Deprecated
    public static int[] parseAspectRatio(String str) {
        return CssDimensionParsingUtils.parseAspectRatio(str);
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
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseAbsoluteLength(String, String)} instead
     */
    @Deprecated
    public static float parseAbsoluteLength(String length, String defaultMetric) {
        return CssDimensionParsingUtils.parseAbsoluteLength(length, defaultMetric);
    }

    /**
     * Parses the absolute length.
     *
     * @param length the length as a string
     * @return the length as a float
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseAbsoluteLength(String)} instead
     */
    @Deprecated
    public static float parseAbsoluteLength(String length) {
        return CssDimensionParsingUtils.parseAbsoluteLength(length);
    }

    /**
     * Parses an relative value based on the base value that was given, in the metric unit of the base value.<br>
     * (e.g. margin=10% should be based on the page width, so if an A4 is used, the margin = 0.10*595.0 = 59.5f)
     *
     * @param relativeValue in %, em or ex
     * @param baseValue     the value the returned float is based on
     * @return the parsed float in the metric unit of the base value
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseRelativeValue(String, float)} instead
     */
    @Deprecated
    public static float parseRelativeValue(final String relativeValue, final float baseValue) {
        return CssDimensionParsingUtils.parseRelativeValue(relativeValue, baseValue);
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
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseLengthValueToPt(String, float, float)} instead
     */
    @Deprecated
    public static UnitValue parseLengthValueToPt(final String value, final float emValue, final float remValue) {
        return CssDimensionParsingUtils.parseLengthValueToPt(value, emValue, remValue);
    }

    /**
     * Checks if a string is in a valid format.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value is in a valid format
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isValidNumericValue(String)} instead
     */
    @Deprecated
    public static boolean isValidNumericValue(final String value) {
        return CssTypesValidationUtils.isValidNumericValue(value);
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
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseAbsoluteFontSize(String, String)} instead
     */
    @Deprecated
    public static float parseAbsoluteFontSize(String fontSizeValue, String defaultMetric) {
        return CssDimensionParsingUtils.parseAbsoluteFontSize(fontSizeValue, defaultMetric);
    }

    /**
     * Parses the absolute font size.
     * <p>
     * A numeric value (without px, pt, etc in the given length string) is considered to be in the px.
     *
     * @param fontSizeValue the font size value as a {@link String}
     * @return the font size value as a {@code float}
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseAbsoluteFontSize(String)} instead
     */
    @Deprecated
    public static float parseAbsoluteFontSize(String fontSizeValue) {
        return CssDimensionParsingUtils.parseAbsoluteFontSize(fontSizeValue);
    }

    /**
     * Parses the relative font size.
     *
     * @param relativeFontSizeValue the relative font size value as a {@link String}
     * @param baseValue             the base value
     * @return the relative font size value as a {@code float}
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseRelativeFontSize(String, float)} instead
     */
    @Deprecated
    public static float parseRelativeFontSize(final String relativeFontSizeValue, final float baseValue) {
        return CssDimensionParsingUtils.parseRelativeFontSize(relativeFontSizeValue, baseValue);
    }

    /**
     * Parses the border radius of specific corner.
     *
     * @param specificBorderRadius string that defines the border radius of specific corner.
     * @param emValue              the em value
     * @param remValue             the root em value
     * @return an array of {@link UnitValue UnitValues} that define horizontal and vertical border radius values
     * @deprecated will be removed in 7.2, use
     * {@link CssDimensionParsingUtils#parseSpecificCornerBorderRadius(String, float, float)} instead
     */
    @Deprecated
    public static UnitValue[] parseSpecificCornerBorderRadius(String specificBorderRadius, final float emValue, final float remValue) {
        return CssDimensionParsingUtils.parseSpecificCornerBorderRadius(specificBorderRadius, emValue, remValue);
    }

    /**
     * Parses the resolution.
     *
     * @param resolutionStr the resolution as a string
     * @return a value in dpi
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseResolution(String)} instead
     */
    @Deprecated
    public static float parseResolution(String resolutionStr) {
        return CssDimensionParsingUtils.parseResolution(resolutionStr);
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
     * @deprecated will be removed in 7.2, use
     * {@link CssDimensionParsingUtils#determinePositionBetweenValueAndUnit(String)} instead
     */
    @Deprecated
    public static int determinePositionBetweenValueAndUnit(String string) {
        return CssDimensionParsingUtils.determinePositionBetweenValueAndUnit(string);
    }

    /**
     * Checks whether a string contains an allowed metric unit in HTML/CSS; px, in, cm, mm, pc, Q or pt.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed metric value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isMetricValue(String)} instead
     */
    @Deprecated
    public static boolean isMetricValue(final String value) {
        return CssTypesValidationUtils.isMetricValue(value);
    }

    /**
     * Checks whether a string contains an allowed metric unit in HTML/CSS; rad, deg and grad.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed angle value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isAngleValue(String)} instead
     */
    @Deprecated
    public static boolean isAngleValue(final String value) {
        return CssTypesValidationUtils.isAngleValue(value);
    }

    /**
     * Checks whether a string contains an allowed value relative to previously set value.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed metric value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isRelativeValue(String)} instead
     */
    @Deprecated
    public static boolean isRelativeValue(final String value) {
        return CssTypesValidationUtils.isRelativeValue(value);
    }

    /**
     * Checks whether a string contains an allowed value relative to font.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed font relative value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isRelativeValue(String)} method instead
     */
    @Deprecated
    public static boolean isFontRelativeValue(final String value) {
        if (value == null) {
            return false;
        }
        for (String relativePostfix : FONT_RELATIVE_MEASUREMENTS_VALUES) {
            if (value.endsWith(relativePostfix) && CssTypesValidationUtils.isNumericValue(
                    value.substring(0, value.length() - relativePostfix.length()).trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string contains a percentage value
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed percentage value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isPercentageValue(String)} method instead
     */
    @Deprecated
    public static boolean isPercentageValue(final String value) {
        return CssTypesValidationUtils.isPercentageValue(value);
    }

    /**
     * Checks whether a string contains an allowed value relative to previously set root value.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains a rem value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isRemValue(String)} method instead
     */
    @Deprecated
    public static boolean isRemValue(final String value) {
        return CssTypesValidationUtils.isRemValue(value);
    }

    /**
     * Checks whether a string contains an allowed value relative to parent value.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains a em value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isEmValue(String)} method instead
     */
    @Deprecated
    public static boolean isEmValue(final String value) {
        return CssTypesValidationUtils.isEmValue(value);
    }

    /**
     * Checks whether a string contains an allowed value relative to element font height.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains a ex value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isExValue(String)} method instead
     */
    @Deprecated
    public static boolean isExValue(final String value) {
        return CssTypesValidationUtils.isExValue(value);
    }

    /**
     * Checks whether a string matches a numeric value (e.g. 123, 1.23, .123). All these metric values are allowed in
     * HTML/CSS.
     *
     * @param value the string that needs to be checked
     * @return boolean true if value contains an allowed metric value
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isNumericValue(String)} method instead
     */
    @Deprecated
    public static boolean isNumericValue(final String value) {
        return CssTypesValidationUtils.isNumericValue(value);
    }

    /**
     * Checks whether a string matches a negative value (e.g. -123, -2em, -0.123).
     * All these metric values are allowed in HTML/CSS.
     *
     * @param value the string that needs to be checked
     * @return true if value is negative
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isNegativeValue(String)} method instead
     */
    @Deprecated
    public static boolean isNegativeValue(final String value) {
        return CssTypesValidationUtils.isNegativeValue(value);
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
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isBase64Data(String)} method instead
     */
    @Deprecated
    public static boolean isBase64Data(String data) {
        return CssTypesValidationUtils.isBase64Data(data);
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
     * @deprecated will be removed in 7.2, use {@link CssTypesValidationUtils#isColorProperty(String)} method instead
     */
    @Deprecated
    public static boolean isColorProperty(String value) {
        return CssTypesValidationUtils.isColorProperty(value);
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
     * @deprecated will be removed in 7.2, use {@link CssDimensionParsingUtils#parseRgbaColor(String)} method instead
     */
    @Deprecated
    public static float[] parseRgbaColor(String colorValue) {
        return CssDimensionParsingUtils.parseRgbaColor(colorValue);
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

    /**
     * Convert given point value to a pixel value. 1 px is 0.75 pts.
     *
     * @param pts float value to be converted to pixels
     * @return float converted value pts/0.75f
     */
    public static float convertPtsToPx(float pts) {
        return pts / 0.75f;
    }

    /**
     * Convert given point value to a pixel value. 1 px is 0.75 pts.
     *
     * @param pts double value to be converted to pixels
     * @return double converted value pts/0.75
     */
    public static double convertPtsToPx(double pts) {
        return pts / 0.75;
    }

    /**
     * Convert given point value to a point value. 1 px is 0.75 pts.
     *
     * @param px float value to be converted to pixels
     * @return float converted value px*0.75
     */
    public static float convertPxToPts(float px) {
        return px * 0.75f;
    }

    /**
     * Convert given point value to a point value. 1 px is 0.75 pts.
     *
     * @param px double value to be converted to pixels
     * @return double converted value px*0.75
     */
    public static double convertPxToPts(double px) {
        return px * 0.75;
    }

    /**
     * Checks if an {@link IElementNode} represents a style sheet link.
     *
     * @param headChildElement the head child element
     * @return true, if the element node represents a style sheet link
     */
    public static boolean isStyleSheetLink(IElementNode headChildElement) {
        return CommonCssConstants.LINK.equals(headChildElement.name())
                && CommonAttributeConstants.STYLESHEET
                .equals(headChildElement.getAttribute(CommonAttributeConstants.REL));
    }

    /**
     * Checks if value is initial, inherit or unset.
     *
     * @param value value to check
     * @return true if value is initial, inherit or unset. false otherwise
     * @deprecated will be removed in 7.2, use
     * {@link CssTypesValidationUtils#isInitialOrInheritOrUnset(String)} method instead
     */
    @Deprecated
    public static boolean isInitialOrInheritOrUnset(String value) {
        return CssTypesValidationUtils.isInitialOrInheritOrUnset(value);
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
        if (l > r || r > 1114111) {
            // Although Firefox follows the spec (and therefore the second condition), it seems it's ignored in Chrome or Edge
            return false;
        }
        builder.addRange(l, r);
        return true;
    }
}
