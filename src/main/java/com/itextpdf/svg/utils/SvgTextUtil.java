package com.itextpdf.svg.utils;

import com.itextpdf.io.util.TextUtil;

/**
 * Class containing utility methods for text operations in the context of SVG processing
 */
public class SvgTextUtil {

    /**
     * Trim all the leading whitespace characters from the passed string
     *
     * @param toTrim string to trim
     * @return string with all leading whitespace characters removed
     */
    public static String trimLeadingWhitespace(String toTrim) {
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
        int end = toTrim.length();
        if (end > 0) {
            int current = end - 1;
            while (current > 0) {
                char currentChar = toTrim.charAt(current);
                if (Character.isWhitespace(currentChar) && !(currentChar == '\n' || currentChar == '\r')) {
                    //if the character is whitespace and not a newline, increase current
                    current--;
                } else {
                    break;
                }
            }
            if(current == 0){
                return "";
            }else {
                return toTrim.substring(0, current + 1);
            }
        }else{
            return toTrim;
        }
    }
}
