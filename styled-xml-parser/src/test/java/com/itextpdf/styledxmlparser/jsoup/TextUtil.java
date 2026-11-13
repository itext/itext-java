/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup;

/**
 Text utils to ease testing

 @author Jonathan Hedley, jonathan@hedley.net */
public class TextUtil {
    public static String stripNewlines(String text) {
        text = text.replaceAll("\\n\\s*", "");
        return text;
    }
}
