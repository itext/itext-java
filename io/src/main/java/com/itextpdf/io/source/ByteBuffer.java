/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.source;

import com.itextpdf.commons.utils.MessageFormatUtil;

/**
 * A growable byte buffer with append and byte-oriented conversion operations.
 */
public class ByteBuffer {

    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};


    /** The number of bytes currently stored in this buffer. */
    protected int count;
    private byte[] buffer;

    /**
     * Creates a buffer with the default initial capacity.
     */
    public ByteBuffer() {
        this(128);
    }

    /**
     * Creates a buffer with the requested initial capacity.
     *
     * @param size the initial capacity; values below one use the default capacity
     */
    public ByteBuffer(int size) {
        if (size < 1)
            size = 128;
        buffer = new byte[size];
    }

    /**
     * Converts an ASCII hexadecimal digit to its numeric value.
     *
     * @param v the character value to convert
     *
     * @return a value from {@code 0} through {@code 15}, or {@code -1} when {@code v} is not hexadecimal
     */
    public static int getHex(int v) {
        if (v >= '0' && v <= '9')
            return v - '0';
        if (v >= 'A' && v <= 'F')
            return v - 'A' + 10;
        if (v >= 'a' && v <= 'f')
            return v - 'a' + 10;
        return -1;
    }

    /**
     * Appends one byte, expanding the backing array when necessary.
     *
     * @param b the byte to append
     *
     * @return this buffer
     */
    public ByteBuffer append(byte b) {
        int newCount = count + 1;
        if (newCount > buffer.length) {
            byte[] newBuffer = new byte[Math.max(buffer.length << 1, newCount)];
            System.arraycopy(buffer, 0, newBuffer, 0, count);
            buffer = newBuffer;
        }
        buffer[count] = b;
        count = newCount;
        return this;
    }

    /**
     * Appends a range from a byte array.
     *
     * @param b   the source array
     * @param off the zero-based source offset
     * @param len the number of bytes to append
     *
     * @return this buffer; invalid ranges and zero lengths leave it unchanged
     */
    public ByteBuffer append(byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0) || len == 0)
            return this;
        int newCount = count + len;
        if (newCount > buffer.length) {
            byte[] newBuffer = new byte[Math.max(buffer.length << 1, newCount)];
            System.arraycopy(buffer, 0, newBuffer, 0, count);
            buffer = newBuffer;
        }
        System.arraycopy(b, off, buffer, count, len);
        count = newCount;
        return this;
    }

    /**
     * Appends all bytes from an array.
     *
     * @param b the source array
     *
     * @return this buffer
     */
    public ByteBuffer append(byte[] b) {
        return append(b, 0, b.length);
    }

    /**
     * Appends the low eight bits of an integer.
     *
     * @param b the value whose low byte is appended
     *
     * @return this buffer
     */
    public ByteBuffer append(int b) {
        return append((byte) b);
    }

    /**
     * Appends the ISO-8859-1 compatible byte representation of a string.
     *
     * @param str the string to append
     *
     * @return this buffer
     */
    public ByteBuffer append(String str) {
        return append(ByteUtils.getIsoBytes(str));
    }

    /**
     * Appends two lowercase hexadecimal characters representing a byte.
     *
     * @param b the byte to encode
     *
     * @return this buffer
     */
    public ByteBuffer appendHex(byte b) {
        append(bytes[(b >> 4) & 0x0f]);
        return append(bytes[b & 0x0f]);
    }

    /**
     * Gets a stored byte by index.
     *
     * @param index the zero-based index
     *
     * @return the byte at {@code index}
     *
     * @throws IndexOutOfBoundsException if {@code index} is at or beyond {@link #size()}
     */
    public byte get(int index) {
        if (index >= count) {
            throw new IndexOutOfBoundsException(MessageFormatUtil.format("Index: {0}, Size: {1}", index, count));
        }
        return buffer[index];
    }

    /**
     * Gets the mutable backing array without copying.
     *
     * @return the backing array, whose length may exceed {@link #size()}
     */
    public byte[] getInternalBuffer() {
        return buffer;
    }

    /**
     * Gets the number of bytes stored in this buffer.
     *
     * @return the logical byte count
     */
    public int size() {
        return count;
    }

    /**
     * Tests whether this buffer has no stored bytes.
     *
     * @return {@code true} when {@link #size()} is zero
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Gets the current backing-array capacity.
     *
     * @return the number of bytes the backing array can hold without growing
     */
    public int capacity() {
        return buffer.length;
    }

    /**
     * Discards all stored bytes while retaining the backing array.
     *
     * @return this buffer
     */
    public ByteBuffer reset() {
        count = 0;
        return this;
    }

    /**
     * Copies a range from the backing array.
     *
     * @param off the zero-based source offset
     * @param len the number of bytes to copy
     *
     * @return a new array containing the requested bytes
     */
    public byte[] toByteArray(int off, int len) {
        byte[] newBuf = new byte[len];
        System.arraycopy(buffer, off, newBuf, 0, len);
        return newBuf;
    }

    /**
     * Copies all stored bytes.
     *
     * @return a new array containing bytes from zero through {@link #size()}
     */
    public byte[] toByteArray() {
        return toByteArray(0, count);
    }

    /**
     * Tests whether the stored bytes begin with a sequence.
     *
     * @param b the candidate prefix
     *
     * @return {@code true} when this buffer starts with {@code b}
     */
    public boolean startsWith(byte[] b) {
        if (size() < b.length)
            return false;
        for (int k = 0; k < b.length; ++k) {
            if (buffer[k] != b[k])
                return false;
        }
        return true;
    }

    /**
     * Fill {@code ByteBuffer} from the end.
     * Set byte at {@code capacity() - size() - 1} position.
     * @param b {@code byte}.
     * @return {@code ByteBuffer}.
     */
    ByteBuffer prepend(byte b) {
        buffer[buffer.length - count - 1] = b;
        count++;
        return this;
    }

    /**
     * Fill {@code ByteBuffer} from the end.
     * Set bytes from {@code capacity() - size() - b.length} position.
     * @param b {@code byte}.
     * @return {@code ByteBuffer}.
     */
    ByteBuffer prepend(byte[] b) {
        System.arraycopy(b, 0, buffer, buffer.length - count - b.length, b.length);
        count += b.length;
        return this;
    }
}
