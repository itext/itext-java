/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup;

import java.util.regex.Pattern;

/**
 Text utils to ease testing
*/
public class TextUtil {
    static Pattern stripper = Pattern.compile("\\r?\\n\\s*");

    public static String stripNewlines(String text) {
        return stripper.matcher(text).replaceAll("");
    }
}
