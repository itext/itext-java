/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * This class implements an output stream which can be used for memory limits aware decompression of pdf streams.
 */
class MemoryLimitsAwareOutputStream extends ByteArrayOutputStream {

    /**
     * The maximum size of array to allocate.
     * Attempts to allocate larger arrays will result in an exception.
     */
    private static final int DEFAULT_MAX_STREAM_SIZE = Integer.MAX_VALUE - 8;

    /**
     * The maximum size of array to allocate.
     * Attempts to allocate larger arrays will result in an exception.
     */
    private int maxStreamSize = DEFAULT_MAX_STREAM_SIZE;

    /**
     * Creates a new byte array output stream. The buffer capacity is
     * initially 32 bytes, though its size increases if necessary.
     */
    public MemoryLimitsAwareOutputStream() {
        super();
    }

    /**
     * Creates a new byte array output stream, with a buffer capacity of
     * the specified size, in bytes.
     *
     * @param size the initial size.
     * @throws IllegalArgumentException if size is negative.
     */
    public MemoryLimitsAwareOutputStream(int size) {
        super(size);
    }

    /**
     * Gets the maximum size which can be occupied by this output stream.
     *
     * @return the maximum size which can be occupied by this output stream.
     */
    public long getMaxStreamSize() {
        return maxStreamSize;
    }

    /**
     * Sets the maximum size which can be occupied by this output stream.
     *
     * @param maxStreamSize the maximum size which can be occupied by this output stream.
     * @return this {@link MemoryLimitsAwareOutputStream}
     */
    public MemoryLimitsAwareOutputStream setMaxStreamSize(int maxStreamSize) {
        this.maxStreamSize = maxStreamSize;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void write(byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }

        int minCapacity = count + len;
        if (minCapacity < 0) { // overflow
            throw new MemoryLimitsAwareException(PdfException.DuringDecompressionSingleStreamOccupiedMoreThanMaxIntegerValue);
        }
        if (minCapacity > maxStreamSize) {
            throw new MemoryLimitsAwareException(PdfException.DuringDecompressionSingleStreamOccupiedMoreMemoryThanAllowed);
        }

        // calculate new capacity
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity < 0 || newCapacity - minCapacity < 0) { // overflow
            newCapacity = minCapacity;
        }

        if (newCapacity - maxStreamSize > 0) {
            newCapacity = maxStreamSize;
            buf = Arrays.copyOf(buf, newCapacity);
        }
        super.write(b, off, len);
    }
}