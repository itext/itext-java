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
package com.itextpdf.styledxmlparser.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for white-space handling methods that are used both in pdfHTML and the iText-core SVG module
 */
public class WhiteSpaceUtil {

    private static final Set<Character> EM_SPACES = new HashSet<>();

    static {
        EM_SPACES.add((char) 0x2002);
        EM_SPACES.add((char) 0x2003);
        EM_SPACES.add((char) 0x2009);
    }

    /**
     * Collapse all consecutive spaces of the passed String into single spaces
     * @param s String to collapse
     * @return a String containing the contents of the input, with consecutive spaces collapsed
     */
    public static String collapseConsecutiveSpaces(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (isNonEmSpace(s.charAt(i))) {
                if (sb.length() == 0 || !isNonEmSpace(sb.charAt(sb.length() - 1))) {
                    sb.append(" ");
                }
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * Checks if a character is white space value that is not em, en or similar special whitespace character.
     *
     * @param ch the character
     * @return true, if the character is a white space character, but no em, en or similar
     */
    public static boolean isNonEmSpace(char ch) {
        return Character.isWhitespace(ch) && !EM_SPACES.contains(ch);
    }
}
