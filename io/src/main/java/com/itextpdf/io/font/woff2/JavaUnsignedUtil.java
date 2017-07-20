package com.itextpdf.io.font.woff2;

/**
 * Helper class to deal with unsigned primitives in java
 */
class JavaUnsignedUtil {
    public static int asU16(short number) {
        return number & 0xffff;
    }

    public static int asU8(byte number) {
        return number & 0xff;
    }

    public static byte toU8(int number) {
        return (byte) (number & 0xff);
    }

    public static short toU16(int number) {
        return (short) (number & 0xffff);
    }

    public static int compareAsUnsigned(int left, int right) {
        return Long.valueOf(left & 0xffffffffL).compareTo(right & 0xffffffffL);
    }
}
