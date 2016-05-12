package com.itextpdf.kernel.crypto;

public class SystemUtility {
    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
