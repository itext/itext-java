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


/**
 * A RandomAccessSource that wraps another RandomAccessSource and provides a window of it at a specific offset and over
 * a specific length.  Position 0 becomes the offset position in the underlying source.
 */
public class WindowRandomAccessSource implements IRandomAccessSource {
    /**
     * The source
     */
    private final IRandomAccessSource source;

    /**
     * The amount to offset the source by
     */
    private final long offset;

    /**
     * The length
     */
    private final long length;

    /**
     * Constructs a new OffsetRandomAccessSource that extends to the end of the underlying source
     * @param source the source
     * @param offset the amount of the offset to use
     */
    public WindowRandomAccessSource(IRandomAccessSource source, long offset) {
        this(source, offset, source.length() - offset);
    }

    /**
     * Constructs a new OffsetRandomAccessSource with an explicit length
     * @param source the source
     * @param offset the amount of the offset to use
     * @param length the number of bytes to be included in this RAS
     */
    public WindowRandomAccessSource(IRandomAccessSource source, long offset, long length) {
        this.source = source;
        this.offset = offset;
        this.length = length;
    }

    /**
     * {@inheritDoc}
     * Note that the position will be adjusted to read from the corrected location in the underlying source
     */
    public int get(long position) throws java.io.IOException {
        if (position >= length) return -1;
        return source.get(offset + position);
    }

    /**
     * {@inheritDoc}
     * Note that the position will be adjusted to read from the corrected location in the underlying source
     */
    public int get(long position, byte[] bytes, int off, int len) throws java.io.IOException {
        if (position >= length)
            return -1;

        long toRead = Math.min(len, length - position);
        return source.get(offset + position, bytes, off, (int)toRead);
    }

    /**
     * {@inheritDoc}
     * Note that the length will be adjusted to read from the corrected location in the underlying source
     */
    public long length() {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws java.io.IOException {
        source.close();
    }
}
