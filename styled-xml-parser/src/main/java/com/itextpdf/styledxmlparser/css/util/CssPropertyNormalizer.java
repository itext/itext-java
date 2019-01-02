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
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.PortUtil;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Utilities class with functionality to normalize CSS properties.
 */
class CssPropertyNormalizer {

    private static final Pattern URL_PATTERN = PortUtil.createRegexPatternWithDotMatchingNewlines("^[uU][rR][lL]\\(.*?");

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
                } else if ((str.charAt(i) == 'u' || str.charAt(i) == 'U') && URL_PATTERN.matcher(str.substring(i)).matches()) {
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
            LoggerFactory.getLogger(CssPropertyNormalizer.class).warn(MessageFormatUtil.format(LogMessageConstant.QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION, source));
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
        while (Character.isWhitespace(source.charAt(start)) && start < source.length()) {
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
                    LoggerFactory.getLogger(CssPropertyNormalizer.class).warn(MessageFormatUtil.format(LogMessageConstant.URL_IS_NOT_CLOSED_IN_CSS_EXPRESSION, source));
                    return source.length();
                } else {
                    buffer.append(source.substring(start, curr).trim());
                    buffer.append(')');
                    return curr + 1;
                }
            }
        } else {
            LoggerFactory.getLogger(CssPropertyNormalizer.class).warn(MessageFormatUtil.format(LogMessageConstant.URL_IS_EMPTY_IN_CSS_EXPRESSION, source));
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
