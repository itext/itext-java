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

import java.io.RandomAccessFile;

/**
 * A RandomAccessSource that uses a {@link java.io.RandomAccessFile} as it's source
 * Note: Unlike most of the RandomAccessSource implementations, this class is not thread safe
 */
class RAFRandomAccessSource implements IRandomAccessSource {
    /**
     * The source
     */
    private final RandomAccessFile raf;

    /**
     * The length of the underling RAF.  Note that the length is cached at construction time to avoid the possibility
     * of {@link java.io.IOException}s when reading the length.
     */
    private final long length;

    /**
     * Creates this object
     * @param raf the source for this RandomAccessSource
     * @throws java.io.IOException if the RAF can't be read
     */
    public RAFRandomAccessSource(RandomAccessFile raf) throws java.io.IOException {
        this.raf = raf;
        length = raf.length();
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position) throws java.io.IOException {
        if (position > length)
            return -1;

        // Not thread safe!
        if (raf.getFilePointer() != position) {
            raf.seek(position);
        }

        return raf.read();
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws java.io.IOException {
        if (position > length)
            return -1;

        // Not thread safe!
        if (raf.getFilePointer() != position) {
            raf.seek(position);
        }

        return raf.read(bytes, off, len);
    }

    /**
     * {@inheritDoc}
     * Note: the length is determined when the {@link RAFRandomAccessSource} is constructed.  If the file length changes
     * after construction, that change will not be reflected in this call.
     */
    public long length() {
        return length;
    }

    /**
     * Closes the underlying RandomAccessFile
     */
    public void close() throws java.io.IOException {
        raf.close();
    }
}
