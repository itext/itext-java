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
package com.itextpdf.styledxmlparser.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for white-space handling methods that are used both in pdfHTML and the iText-core SVG module
 */
public class WhiteSpaceUtil {

    private static final Set<Character> EM_SPACES;

    static {
        // HashSet is required in order to autoport correctly in .Net
        HashSet<Character> tempSet = new HashSet<>();
        tempSet.add((char) 0x2002);
        tempSet.add((char) 0x2003);
        tempSet.add((char) 0x2009);
        EM_SPACES = Collections.unmodifiableSet(tempSet);
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

    /**
     * Checks if a character is white space value that doesn't cause a newline.
     *
     * @param ch the character
     *
     * @return {@code true}, if the character is a white space character, but no newline
     */
    public static boolean isNonLineBreakSpace(char ch) {
        return WhiteSpaceUtil.isNonEmSpace(ch) && ch != '\n';
    }

    /**
     * Processes whitespaces according to provided {@code keepLineBreaks} and {@code collapseSpaces} values.
     *
     * @param text string to process
     * @param keepLineBreaks whether to keep line breaks
     * @param collapseSpaces whether to collapse spaces
     *
     * @return processed string
     */
    public static String processWhitespaces(String text, boolean keepLineBreaks, boolean collapseSpaces) {
        if (!keepLineBreaks && collapseSpaces) {
            // Don't keep line breaks and collapse spaces. Normal or nowrap.
            text = WhiteSpaceUtil.collapseConsecutiveSpaces(text);
        } else if (keepLineBreaks && collapseSpaces) {
            // Keep line breaks and collapse spaces. Pre-line.
            StringBuilder sb = new StringBuilder(text.length());
            for (int i = 0; i < text.length(); i++) {
                if (WhiteSpaceUtil.isNonLineBreakSpace(text.charAt(i))) {
                    if (sb.length() == 0 || sb.charAt(sb.length() - 1) != ' ') {
                        sb.append(" ");
                    }
                } else {
                    sb.append(text.charAt(i));
                }
            }
            text = sb.toString();
        } else {
            // Preserve line breaks and spaces. Pre, pre-wrap and break-spaces.
            text = keepLineBreaksAndSpaces(text);
        }
        return text;
    }

    private static String keepLineBreaksAndSpaces(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        // Prohibit trimming first and last spaces.
        sb.append('\u200d');
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i));
            if ('\n' == text.charAt(i) ||
                    ('\r' == text.charAt(i) && i + 1 < text.length() && '\n' != text.charAt(i + 1))) {
                sb.append('\u200d');
            }
        }
        if ('\u200d' == sb.charAt(sb.length() - 1)) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }
}
