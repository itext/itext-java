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
