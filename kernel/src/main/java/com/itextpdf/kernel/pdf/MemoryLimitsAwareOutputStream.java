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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;

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
        // NOTE: in case this method is updated, the ManualCompressionTest should be run!
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }

        int minCapacity = count + len;
        if (minCapacity < 0) {
            // overflow
            throw new MemoryLimitsAwareException(
                    KernelExceptionMessageConstant.DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_THAN_MAX_INTEGER_VALUE);
        }
        if (minCapacity > maxStreamSize) {
            throw new MemoryLimitsAwareException(
                    KernelExceptionMessageConstant.DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED);
        }

        // calculate new capacity
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity < 0 || newCapacity - minCapacity < 0) {
            // overflow
            newCapacity = minCapacity;
        }

        if (newCapacity - maxStreamSize > 0) {
            newCapacity = maxStreamSize;
            buf = Arrays.copyOf(buf, newCapacity);
        }
        super.write(b, off, len);
    }
}
