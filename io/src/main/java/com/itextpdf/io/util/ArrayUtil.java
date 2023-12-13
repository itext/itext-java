/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
 * Be aware that its API and functionality may be changed in future.
 */
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

    public static int[] toIntArray(Collection<Integer> collection) {
        int[] array = new int[collection.size()];
        int k = 0;
        for (Integer key : collection) {
            array[k++] = (int) key;
        }
        return array;
    }

    public static int hashCode(byte[] a) {
        if (a == null)
            return 0;

        int result = 1;
        for (byte element : a)
            result = 31 * result + element;

        return result;
    }

    public static int[] fillWithValue(int[] a, int value) {
        for (int i = 0; i < a.length; i++) {
            a[i] = value;
        }
        return a;
    }

    public static float[] fillWithValue(float[] a, float value) {
        for (int i = 0; i < a.length; i++) {
            a[i] = value;
        }
        return a;
    }

    public static <T> void fillWithValue(T[] a, T value) {
        for (int i = 0; i < a.length; i++) {
            a[i] = value;
        }
    }

    public static int[] cloneArray(int[] src) {
        return (int[]) src.clone();
    }
}
