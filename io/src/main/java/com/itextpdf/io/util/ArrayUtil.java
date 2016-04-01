package com.itextpdf.io.util;

import java.util.Collection;

public final class ArrayUtil {

    private ArrayUtil() {
    }

    public static byte[] shortenArray(byte[] src, int length) {
        if (length < src.length) {
            byte[] shortened = new byte[length];
            System.arraycopy(src, 0, shortened, 0, length);
            return shortened;
        }
        return src;
    }

    public static int[] toArray(Collection<Integer> collection) {
        int[] array = new int[collection.size()];
        int k = 0;
        for (Integer key : collection) {
            array[k++] = key;
        }
        return array;
    }

}
