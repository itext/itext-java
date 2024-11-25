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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.layout.font.Range;
import com.itextpdf.layout.font.RangeBuilder;
import com.itextpdf.layout.properties.BlendMode;
import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.Token;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.TokenType;
import com.itextpdf.styledxmlparser.node.IElementNode;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for CSS operations.
 */
public class CssUtils {
    private static final float EPSILON = 1e-6f;

    private static final  Logger logger = LoggerFactory.getLogger(CssUtils.class);

    private static final int QUANTITY_OF_PARAMS_WITH_FALLBACK_OR_TYPE = 2;

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
                resultList.add(value.substring(lastSplitChar, i).trim());
                lastSplitChar = i + 1;
            }
        }
        final String lastToken = value.substring(lastSplitChar);
        if (!lastToken.isEmpty()) {
            resultList.add(lastToken.trim());
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
     * Parses {@code url("file.jpg")} to {@code file.jpg}.
     *
     * @param url the url attribute to parse
     *
     * @return the parsed url. Or original url if not wrappend in url()
     */
    public static String extractUrl(String url) {
        if (url.startsWith(CommonCssConstants.URL)) {
            String urlString = url.substring(CommonCssConstants.URL.length()).trim();
            if (!urlString.startsWith("(") || !urlString.endsWith(")")) {
                return url;
            }
            urlString = urlString.substring(1, urlString.length() - 1).trim();
            return extractUnquotedString(urlString);
        } else {
            // assume it's an url without wrapping in "url()"
            return extractUnquotedString(url);
        }
    }

    /**
     * Unquotes the passed string, e.g. parse {@code "text"} to {@code text}.
     *
     * @param str the quotes string
     *
     * @return the unquoted string, or original {@code str} if not wrapped in quotes
     */
    public static String extractUnquotedString(String str) {
        if ((str.startsWith("'") && str.endsWith("'")) || (str.startsWith("\"") && str.endsWith("\""))) {
            return str.substring(1, str.length() - 1).trim();
        } else {
            return str;
        }
    }

    /**
     * Parses string and return attribute value.
     *
     * @param attrStr the string contains attr() to extract attribute value
     * @param element the parentNode from which we extract information
     * @return the value of attribute
     */
    public static String extractAttributeValue(final String attrStr, IElementNode element) {
        String attrValue = null;
        if (attrStr.startsWith(CommonCssConstants.ATTRIBUTE + '(')
                && attrStr.length() > CommonCssConstants.ATTRIBUTE.length() + 2 && attrStr.endsWith(")")) {
            String fallback = null;
            String typeOfAttribute = null;
            final String stringToSplit = attrStr.substring(5, attrStr.length() - 1);
            final List<String> paramsWithFallback = splitString(stringToSplit, ',', new EscapeGroup('\"'),
                    new EscapeGroup('\''));
            if (paramsWithFallback.size() > QUANTITY_OF_PARAMS_WITH_FALLBACK_OR_TYPE) {
                return null;
            }
            if (paramsWithFallback.size() == QUANTITY_OF_PARAMS_WITH_FALLBACK_OR_TYPE) {
                fallback = extractFallback(paramsWithFallback.get(1));
            }
            final List<String> params = splitString(paramsWithFallback.get(0), ' ');
            if (params.size() > QUANTITY_OF_PARAMS_WITH_FALLBACK_OR_TYPE) {
                return null;
            }
            if (params.size() == QUANTITY_OF_PARAMS_WITH_FALLBACK_OR_TYPE) {
                typeOfAttribute = extractTypeOfAttribute(params.get(1));
                if (typeOfAttribute == null) {
                    return null;
                }
            }
            String attributeName = params.get(0);
            if (isAttributeNameValid(attributeName)) {
                attrValue = getAttributeValue(attributeName, typeOfAttribute, fallback, element);
            }
        }
        return attrValue;
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

    private static boolean isAttributeNameValid(String attributeName) {
        return !(attributeName.contains("'") || attributeName.contains("\"") || attributeName.contains("(")
                || attributeName.contains(")"));
    }

    private static String extractFallback(String fallbackString) {
        String tmpString;
        if ((fallbackString.startsWith("'") && fallbackString.endsWith("'")) || (fallbackString.startsWith("\"")
                && fallbackString.endsWith("\""))) {
            tmpString = fallbackString.substring(1, fallbackString.length() - 1);
        } else {
            tmpString = fallbackString;
        }
        return extractUrl(tmpString);
    }

    private static String extractTypeOfAttribute(String typeString) {
        if (typeString.equals(CommonCssConstants.URL) || typeString.equals(CommonCssConstants.STRING)) {
            return typeString;
        }
        return null;
    }

    private static String getAttributeValue(final String attributeName, final String typeOfAttribute,
            final String fallback,
            IElementNode elementNode) {
        String returnString = elementNode.getAttribute(attributeName);
        if (CommonCssConstants.URL.equals(typeOfAttribute)) {
            returnString = returnString == null ? null : extractUrl(returnString);
        } else {
            returnString = returnString == null ? "" : returnString;
        }
        if (fallback != null && (returnString == null || returnString.isEmpty())) {
            returnString = fallback;
        }
        return returnString;
    }
}
