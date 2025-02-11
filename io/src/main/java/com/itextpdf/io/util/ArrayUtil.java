/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.util;

import java.util.Collection;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in the future.
 */
public final class ArrayUtil {

    private ArrayUtil() {
    }

    /**
     * Shortens byte array.
     *
     * @param src the byte array
     * @param length the new length of bytes array
     * @return the shortened byte array
     */
    public static byte[] shortenArray(byte[] src, int length) {
        if (length < src.length) {
            byte[] shortened = new byte[length];
            System.arraycopy(src, 0, shortened, 0, length);
            return shortened;
        }
        return src;
    }

    /**
     * Converts a collection to an int array.
     *
     * @param collection the collection
     * @return the int array
     */
    public static int[] toIntArray(Collection<Integer> collection) {
        int[] array = new int[collection.size()];
        int k = 0;
        for (Integer key : collection) {
            array[k++] = (int) key;
        }
        return array;
    }

    /**
     * Creates a hash of the given byte array.
     *
     * @param a the byte array
     * @return the byte array
     */
    public static int hashCode(byte[] a) {
        if (a == null)
            return 0;

        int result = 1;
        for (byte element : a)
            result = 31 * result + element;

        return result;
    }

    /**
     * Fills an array with the given value.
     *
     * @param a the int array
     * @param value the number of a value
     * @return the int array
     */
    public static int[] fillWithValue(int[] a, int value) {
        for (int i = 0; i < a.length; i++) {
            a[i] = value;
        }
        return a;
    }

    /**
     * Fills an array with the given value.
     *
     * @param a the float array
     * @param value the number of a value
     * @return the float array
     */
    public static float[] fillWithValue(float[] a, float value) {
        for (int i = 0; i < a.length; i++) {
            a[i] = value;
        }
        return a;
    }

    /**
     * Fills an array with the given value.
     *
     * @param a the array
     * @param value the value of type
     * @param <T> the type of the implementation
     */
    public static <T> void fillWithValue(T[] a, T value) {
        for (int i = 0; i < a.length; i++) {
            a[i] = value;
        }
    }

    /**
     * Clones int array.
     *
     * @param src the int array
     * @return the int array
     */
    public static int[] cloneArray(int[] src) {
        return (int[]) src.clone();
    }

    /**
     * Gets the index of object.
     *
     * @param a the object array
     * @param key the object key
     * @return the index of object
     */
    public static int indexOf(Object[] a, Object key) {
        for (int i = 0; i < a.length; i++) {
            Object el = a[i];
            if (el.equals(key)) {
                return i;
            }
        }
        return -1;
    }
}
