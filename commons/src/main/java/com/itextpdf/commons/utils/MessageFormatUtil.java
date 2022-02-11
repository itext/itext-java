/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.commons.utils;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class MessageFormatUtil {

    private MessageFormatUtil() {
        // Empty constructor.
    }

    /**
     * This method provides a generic way for formatting strings.
     * Indexed arguments can be referred with {index},
     * to escape curly braces you have to double them.
     *
     * @param pattern   to format
     * @param arguments arguments
     *
     * @return The formatted string
     */
    public static String format(String pattern, Object... arguments) {
        boolean mustClose = false;
        StringBuilder result = new StringBuilder(pattern.length());
        int i = 0;
        int n = pattern.length();
        while (i < n) {
            char current = pattern.charAt(i);
            switch (current) {
                case '{': {
                    int curlyCount = 0;
                    int j;
                    for (j = i; j < n && pattern.charAt(j) == '{'; j++, curlyCount++)
                        ;
                    i += curlyCount - 1;
                    if (curlyCount > 1) {
                        if (!mustClose) {
                            result.append("'");
                        }
                        while (curlyCount >= 2) {
                            result.append('{');
                            curlyCount -= 2;
                        }
                        mustClose = true;
                    }
                    if (curlyCount == 1) {
                        if (mustClose) {
                            result.append('\'');
                        }
                        result.append('{');
                        mustClose = false;
                    }
                }
                break;
                case '}': {
                    int curlyCount = 0;
                    int j;
                    for (j = i; j < n && pattern.charAt(j) == '}'; j++, curlyCount++)
                        ;
                    i += curlyCount - 1;
                    if (curlyCount % 2 == 1) {
                        if (mustClose) {
                            result.append('\'');
                        }
                        result.append('}');
                        mustClose = false;
                    }
                    if (curlyCount > 1) {
                        result.append("'");
                        while (curlyCount >= 2) {
                            result.append('}');
                            curlyCount -= 2;
                        }
                        mustClose = true;
                    }
                }
                break;
                case '\'':
                    result.append("''");
                    break;
                default:
                    if (mustClose) {
                        result.append('\'');
                        mustClose = false;
                    }
                    result.append(current);
            }
            i++;
        }
        if (mustClose) {
            result.append('\'');
        }
        return new MessageFormat(result.toString(), Locale.ROOT).format(arguments);
    }
}
