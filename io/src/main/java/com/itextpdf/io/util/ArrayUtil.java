/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
