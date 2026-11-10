/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup;

import java.nio.charset.Charset;

public class PortUtil {

    public static boolean charsetIsSupported(String charsetName) {
        try {
            return Charset.isSupported(charsetName);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String trimControlCodes(String str) {
        // In java trim method removes control codes by default.
        return str.trim();
    }
}
