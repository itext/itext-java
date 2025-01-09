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
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.PortUtil;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Utilities class with functionality to normalize CSS properties.
 */
class CssPropertyNormalizer {

    private static final Pattern URL_PATTERN = PortUtil.createRegexPatternWithDotMatchingNewlines("^[uU][rR][lL]\\(");

    /**
     * Normalize a property.
     *
     * @param str the property
     * @return the normalized property
     */
    static String normalize(String str) {
        StringBuilder sb = new StringBuilder();
        boolean isWhitespace = false;
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == '\\') {
                sb.append(str.charAt(i));
                ++i;
                if (i < str.length()) {
                    sb.append(str.charAt(i));
                    ++i;
                }
            } else if (Character.isWhitespace(str.charAt(i))) {
                isWhitespace = true;
                ++i;
            } else {
                if (isWhitespace) {
                    if (sb.length() > 0 && !trimSpaceAfter(sb.charAt(sb.length() - 1)) && !trimSpaceBefore(str.charAt(i))) {
                        sb.append(" ");
                    }
                    isWhitespace = false;
                }
                if (str.charAt(i) == '\'' || str.charAt(i) == '"') {
                    i = appendQuotedString(sb, str, i);
                } else if ((str.charAt(i) == 'u' || str.charAt(i) == 'U')
                        && URL_PATTERN.matcher(str.substring(i)).find()) {
                    sb.append(str.substring(i, i + 4).toLowerCase());
                    i = appendUrlContent(sb, str, i + 4);
                } else {
                    sb.append(Character.toLowerCase(str.charAt(i)));
                    ++i;
                }
            }
        }
        return sb.toString();
    }

    /**
     * Appends quoted string.
     *
     * @param buffer the current buffer
     * @param source a source
     * @param start  where to start in the source. Should point at quote symbol.
     * @return the new position in the source
     */
    private static int appendQuotedString(StringBuilder buffer, String source, int start) {
        char endQuoteSymbol = source.charAt(start);
        int end = CssUtils.findNextUnescapedChar(source, endQuoteSymbol, start + 1);
        if (end == -1) {
            end = source.length();
            LoggerFactory.getLogger(CssPropertyNormalizer.class).warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION, source));
        } else {
            ++end;
        }
        buffer.append(source, start, end);
        return end;
    }

    /**
     * Appends url content and end parenthesis if url is correct.
     *
     * @param buffer the current buffer
     * @param source a source
     * @param start  where to start in the source. Should point at first symbol after "url(".
     * @return the new position in the source
     */
    private static int appendUrlContent(StringBuilder buffer, String source, int start) {
        while (start < source.length() && Character.isWhitespace(source.charAt(start))) {
            ++start;
        }
        if (start < source.length()) {
            int curr = start;
            if (source.charAt(curr) == '"' || source.charAt(curr) == '\'') {
                curr = appendQuotedString(buffer, source, curr);
                return curr;
            } else {
                curr = CssUtils.findNextUnescapedChar(source, ')', curr);
                if (curr == -1) {
                    LoggerFactory.getLogger(CssPropertyNormalizer.class).warn(MessageFormatUtil.format(
                            StyledXmlParserLogMessageConstant.URL_IS_NOT_CLOSED_IN_CSS_EXPRESSION, source));
                    return source.length();
                } else {
                    buffer.append(source.substring(start, curr).trim());
                    buffer.append(')');
                    return curr + 1;
                }
            }
        } else {
            LoggerFactory.getLogger(CssPropertyNormalizer.class).warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.URL_IS_EMPTY_IN_CSS_EXPRESSION, source));
            return source.length();
        }
    }

    /**
     * Checks if spaces can be trimmed after a specific character.
     *
     * @param ch the character
     * @return true, if spaces can be trimmed after the character
     */
    private static boolean trimSpaceAfter(char ch) {
        return ch == ',' || ch == '(';
    }

    /**
     * Checks if spaces can be trimmed before a specific character.
     *
     * @param ch the character
     * @return true, if spaces can be trimmed before the character
     */
    private static boolean trimSpaceBefore(char ch) {
        return ch == ',' || ch == ')';
    }
}
