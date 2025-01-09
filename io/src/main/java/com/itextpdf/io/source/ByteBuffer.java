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
package com.itextpdf.io.source;

import com.itextpdf.commons.utils.MessageFormatUtil;

public class ByteBuffer {

    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};


    protected int count;
    private byte[] buffer;

    public ByteBuffer() {
        this(128);
    }

    public ByteBuffer(int size) {
        if (size < 1)
            size = 128;
        buffer = new byte[size];
    }

    public static int getHex(int v) {
        if (v >= '0' && v <= '9')
            return v - '0';
        if (v >= 'A' && v <= 'F')
            return v - 'A' + 10;
        if (v >= 'a' && v <= 'f')
            return v - 'a' + 10;
        return -1;
    }

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

    public ByteBuffer append(byte[] b) {
        return append(b, 0, b.length);
    }

    public ByteBuffer append(int b) {
        return append((byte) b);
    }

    public ByteBuffer append(String str) {
        return append(ByteUtils.getIsoBytes(str));
    }

    public ByteBuffer appendHex(byte b) {
        append(bytes[(b >> 4) & 0x0f]);
        return append(bytes[b & 0x0f]);
    }

    public byte get(int index) {
        if (index >= count) {
            throw new IndexOutOfBoundsException(MessageFormatUtil.format("Index: {0}, Size: {1}", index, count));
        }
        return buffer[index];
    }

    public byte[] getInternalBuffer() {
        return buffer;
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int capacity() {
        return buffer.length;
    }

    public ByteBuffer reset() {
        count = 0;
        return this;
    }

    public byte[] toByteArray(int off, int len) {
        byte[] newBuf = new byte[len];
        System.arraycopy(buffer, off, newBuf, 0, len);
        return newBuf;
    }

    public byte[] toByteArray() {
        return toByteArray(0, count);
    }

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
