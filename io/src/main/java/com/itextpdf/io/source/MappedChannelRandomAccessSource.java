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

import java.nio.channels.FileChannel;

/**
 * A RandomAccessSource that represents a memory mapped section of an underlying FileChannel.
 * This source can be closed and will automatically re-open as needed.
 * This class is an internal implementation detail of the {@link FileChannelRandomAccessSource} class and
 * shouldn't be used by general iText users.
 */
class MappedChannelRandomAccessSource implements IRandomAccessSource {
    /**
     * The underlying channel
     */
    private final FileChannel channel;
    /**
     * The offset into the channel that this source maps to
     */
    private final long offset;
    /**
     * The number of bytes this source maps to
     */
    private final long length;

    /**
     * If the map is active, the actual map.  null other wise.
     */
    private ByteBufferRandomAccessSource source;

    /**
     * Create a new source based on the channel.  Mapping will not occur until data is actually read.
     * @param channel the underlying channel
     * @param offset the offset of the map
     * @param length the length of the map
     */
    public MappedChannelRandomAccessSource(FileChannel channel, long offset, long length) {
        if (offset < 0)
            throw new IllegalArgumentException(offset + " is negative");
        if (length <= 0)
            throw new IllegalArgumentException(length + " is zero or negative");

        this.channel = channel;
        this.offset = offset;
        this.length = length;
        this.source = null;
    }

    /**
     * Map the region of the channel
     * @throws java.io.IOException if there is a problem with creating the map
     */
    void open() throws java.io.IOException {
        if (source != null)
            return;

        if (!channel.isOpen())
            throw new IllegalStateException("Channel is closed");

        source = new ByteBufferRandomAccessSource(channel.map(FileChannel.MapMode.READ_ONLY, offset, length));
    }



    /**
     * {@inheritDoc}
     */
    public int get(long position) throws java.io.IOException {
        if (source == null)
            throw new java.io.IOException("RandomAccessSource not opened");
        return source.get(position);
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws java.io.IOException {
        if (source == null)
            throw new java.io.IOException("RandomAccessSource not opened");
        return source.get(position, bytes, off, len);
    }

    /**
     * {@inheritDoc}
     */
    public long length() {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws java.io.IOException {
        if (source == null)
            return;
        source.close();
        source = null;
    }

    @Override
    public String toString() {
        return getClass().getName() + " (" + offset + ", " + length + ")";
    }
}
