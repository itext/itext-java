package com.itextpdf.io.util;

/**
 * This file is a helper class for internal usage only.
 * Be aware that it's API and functionality may be changed in future.
 */
public final class EnumUtil {
    private EnumUtil() {
    }

    public static <T extends Enum<T>> T throwIfNull(T enumInstance) {
        if (enumInstance == null) {
            throw new RuntimeException("Expected not null enum instance");
        }
        return enumInstance;
    }
}
