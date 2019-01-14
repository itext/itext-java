/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.utils;


/**
 * Class containing utility methods for text operations in the context of SVG processing
 */
public final class SvgTextUtil {


    private SvgTextUtil() {
    }

    /**
     * Trim all the leading whitespace characters from the passed string
     *
     * @param toTrim string to trim
     * @return string with all leading whitespace characters removed
     */
    public static String trimLeadingWhitespace(String toTrim) {
        if (toTrim == null) {
            return "";
        }
        int current = 0;
        int end = toTrim.length();
        while (current < end) {
            char currentChar = toTrim.charAt(current);
            if (Character.isWhitespace(currentChar) && !(currentChar == '\n' || currentChar == '\r')) {
                //if the character is whitespace and not a newline, increase current
                current++;
            } else {
                break;
            }
        }
        return toTrim.substring(current);
    }

    /**
     * Trim all the trailing whitespace characters from the passed string
     *
     * @param toTrim string to trom
     * @return string with al trailing whitespace characters removed
     */
    public static String trimTrailingWhitespace(String toTrim) {
        if (toTrim == null) {
            return "";
        }
        int end = toTrim.length();
        if (end > 0) {
            int current = end - 1;
            while (current >= 0) {
                char currentChar = toTrim.charAt(current);
                if (Character.isWhitespace(currentChar) && !(currentChar == '\n' || currentChar == '\r')) {
                    //if the character is whitespace and not a newline, increase current
                    current--;
                } else {
                    break;
                }
            }
            if (current < 0) {
                return "";
            } else {
                return toTrim.substring(0, current + 1);
            }
        } else {
            return toTrim;
        }
    }
}
