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

/**
 * This class is a convenience method to sequentially calculate hash code of the
 * object based on the field values. The result depends on the order of elements
 * appended. The exact formula is the same as for
 * {@link java.util.List#hashCode}.
 *
 * If you need order independent hash code just summate, multiply or XOR all
 * elements.
 *
 * <p>
 * Suppose we have class:
 *
 * <pre><code>
 * class Thing {
 *     long id;
 *     String name;
 *     float weight;
 * }
 * </code></pre>
 *
 * The hash code calculation can be expressed in 2 forms.
 *
 * <p>
 * For maximum performance:
 *
 * <pre><code>
 * public int hashCode() {
 *     int hashCode = HashCode.EMPTY_HASH_CODE;
 *     hashCode = HashCode.combine(hashCode, id);
 *     hashCode = HashCode.combine(hashCode, name);
 *     hashCode = HashCode.combine(hashCode, weight);
 *     return hashCode;
 * }
 * </code></pre>
 *
 * <p>
 * For convenience:
 * <pre><code>
 * public int hashCode() {
 *     return new HashCode().append(id).append(name).append(weight).hashCode();
 * }
 * </code></pre>
 *
 * @see java.util.List#hashCode()
 */
public final class HashCode {
    /**
     * The hashCode value before any data is appended, equals to 1.
     * @see java.util.List#hashCode()
     */
    public static final int EMPTY_HASH_CODE = 1;

    private int hashCode = EMPTY_HASH_CODE;

    /**
     * Returns accumulated hashCode
     */
    @Override
    public final int hashCode() {
        return hashCode;
    }

    /**
     * Combines hashCode of previous elements sequence and value's hashCode.
     * @param hashCode previous hashCode value
     * @param value new element
     * @return combined hashCode
     */
    public static int combine(int hashCode, boolean value) {
        int v = value ? 1231 : 1237;
        return combine(hashCode, v);
    }

    /**
     * Combines hashCode of previous elements sequence and value's hashCode.
     * @param hashCode previous hashCode value
     * @param value new element
     * @return combined hashCode
     */
    public static int combine(int hashCode, long value) {
        int v = (int) (value ^ (value >>> 32));
        return combine(hashCode, v);
    }

    /**
     * Combines hashCode of previous elements sequence and value's hashCode.
     * @param hashCode previous hashCode value
     * @param value new element
     * @return combined hashCode
     */
    public static int combine(int hashCode, float value) {
        int v = Float.floatToIntBits(value);
        return combine(hashCode, v);
    }

    /**
     * Combines hashCode of previous elements sequence and value's hashCode.
     * @param hashCode previous hashCode value
     * @param value new element
     * @return combined hashCode
     */
    public static int combine(int hashCode, double value) {
        long v = Double.doubleToLongBits(value);
        return combine(hashCode, v);
    }

    /**
     * Combines hashCode of previous elements sequence and value's hashCode.
     * @param hashCode previous hashCode value
     * @param value new element
     * @return combined hashCode
     */
    public static int combine(int hashCode, Object value) {
        return combine(hashCode, value.hashCode());
    }

    /**
     * Combines hashCode of previous elements sequence and value's hashCode.
     * @param hashCode previous hashCode value
     * @param value new element
     * @return combined hashCode
     */
    public static int combine(int hashCode, int value) {
        return 31 * hashCode + value;
    }

    /**
     * Appends value's hashCode to the current hashCode.
     * @param value new element
     * @return this
     */
    public final HashCode append(int value) {
        hashCode = combine(hashCode, value);
        return this;
    }

    /**
     * Appends value's hashCode to the current hashCode.
     * @param value new element
     * @return this
     */
    public final HashCode append(long value) {
        hashCode = combine(hashCode, value);
        return this;
    }

    /**
     * Appends value's hashCode to the current hashCode.
     * @param value new element
     * @return this
     */
    public final HashCode append(float value) {
        hashCode = combine(hashCode, value);
        return this;
    }

    /**
     * Appends value's hashCode to the current hashCode.
     * @param value new element
     * @return this
     */
    public final HashCode append(double value) {
        hashCode = combine(hashCode, value);
        return this;
    }

    /**
     * Appends value's hashCode to the current hashCode.
     * @param value new element
     * @return this
     */
    public final HashCode append(boolean value) {
        hashCode = combine(hashCode, value);
        return this;
    }

    /**
     * Appends value's hashCode to the current hashCode.
     * @param value new element
     * @return this
     */
    public final HashCode append(Object value) {
        hashCode = combine(hashCode, value);
        return this;
    }
}
