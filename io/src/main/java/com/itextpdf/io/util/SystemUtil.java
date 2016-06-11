package com.itextpdf.io.util;

/**
 * This file is a helper class for internal usage only.
 * Be aware that it's API and functionality may be changed in future.
 */
public final class SystemUtil {
    public static long getSystemTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}
