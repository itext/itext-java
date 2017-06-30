package com.itextpdf.io.util;


/**
 * This file is a helper class for internal usage only.
 * Be aware that it's API and functionality may be changed in future.
 */
public class NumberUtil {

    private NumberUtil() {
    }

    public static Float asFloat(Object obj) {
        Number value = (Number)obj;
        return value != null ? value.floatValue() : null;
    }

    public static Integer asInteger(Object obj) {
        Number value = (Number)obj;
        return value != null ? value.intValue() : null;
    }

}
