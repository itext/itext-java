/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.barcodes.qrcode;

/**
* This class implements an array of unsigned bytes.
*
* @author dswitkin@google.com (Daniel Switkin)
*/
final class ByteArray {

    private static final int INITIAL_SIZE = 32;

    private byte[] bytes;
    private int size;

    /**
     * Creates a new ByteArray instance with size 0.
     */
    public ByteArray() {
        bytes = null;
        size = 0;
    }

    /**
     * Creates a new ByteArray instance of the specified size.
     *
     * @param size size of the array
     */
    public ByteArray(int size) {
        bytes = new byte[size];
        this.size = size;
    }

    /**
     * Creates a new ByteArray instance based on an existing byte[].
     *
     * @param byteArray the byte[]
     */
    public ByteArray(byte[] byteArray) {
        bytes = byteArray;
        size = bytes.length;
    }

    /**
     * Access an unsigned byte at location index.
     * @param index The index in the array to access.
     * @return The unsigned value of the byte as an int.
     */
    public int at(int index) {
        return bytes[index] & 0xff;
    }

    /**
     * Set the value at "index" to "value"
     * @param index position in the byte-array
     * @param value new value
     */
    public void set(int index, int value) {
        bytes[index] = (byte) value;
    }

    /**
     * @return size of the array
     */
    public int size() {
        return size;
    }

    /**
     * @return true if size is equal to 0, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Append a byte to the end of the array. If the array is too small, it's capacity is doubled.
     * @param value byte to append.
     */
    public void appendByte(int value) {
        if (size == 0 || size >= bytes.length) {
            int newSize = Math.max(INITIAL_SIZE, size << 1);
            reserve(newSize);
        }
        bytes[size] = (byte) value;
        size++;
    }

    /**
     * Increase the capacity of the array to "capacity" if the current capacity is smaller
     * @param capacity the new capacity
     */
    public void reserve(int capacity) {
        if (bytes == null || bytes.length < capacity) {
            byte[] newArray = new byte[capacity];
            if (bytes != null) {
                System.arraycopy(bytes, 0, newArray, 0, bytes.length);
            }
            bytes = newArray;
        }
    }

    /**
     * Copy count bytes from array source starting at offset.
     * @param source source of the copied bytes
     * @param offset offset to start at
     * @param count number of bytes to copy
     */
    public void set(byte[] source, int offset, int count) {
        bytes = new byte[count];
        size = count;
        for (int x = 0; x < count; x++) {
            bytes[x] = source[offset + x];
        }
    }

}
