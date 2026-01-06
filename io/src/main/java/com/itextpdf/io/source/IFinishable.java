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

import java.io.IOException;

/**
 * Interface for output streams that supports finalization without closing the underlying stream.
 * <p>
 * This interface is designed for output streams that wrap other streams and need to complete
 * their processing (such as finishing compression, flushing buffers, or writing final data)
 * without closing the underlying output stream. This is particularly useful when multiple
 * operations need to be performed on the same underlying stream sequentially.
 * <p>
 * Implementations of this interface should ensure that calling {@link #finish()} completes
 * all pending operations and releases any resources associated with the stream processing,
 * but does not close the underlying stream.
 *
 * @see DeflaterOutputStream
 * @see java.io.OutputStream#close()
 */
public interface IFinishable {

    /**
     * Is called to finalize the stream, implementing classes should ensure that the underlying
     * output stream remains open after this method is called.
     * <p>
     * This method completes any pending write operations, flushes internal buffers,
     * writes any final data required by the stream format, and releases resources
     * associated with the stream processing. However, unlike {@link java.io.OutputStream#close()},
     * it does not close the underlying output stream.
     * <p>
     * After calling this method, no further data should be written to this stream,
     * but the underlying stream remains open and can be used for other operations.
     * <p>
     * This method should be idempotent - calling it multiple times should have the
     * same effect as calling it once.
     *
     * @throws IOException if an I/O error occurs during finalization
     */
    void finish() throws IOException;
}
