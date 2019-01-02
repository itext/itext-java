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
package com.itextpdf.io.source;

import java.io.Serializable;
import com.itextpdf.io.util.MessageFormatUtil;

public class ByteBuffer implements Serializable {

    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    private static final long serialVersionUID = -4380712536267312975L;

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
