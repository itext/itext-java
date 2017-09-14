package com.itextpdf.io.util;

/**
 * This file is a helper class for internal usage only.
 * Be aware that it's API and functionality may be changed in future.
 */
public final class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static boolean isOutOfRange(Exception e) {
        return e instanceof IndexOutOfBoundsException;
    }
}
