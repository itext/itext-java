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

/**
 * A byte-array output stream whose backing array can be assigned directly.
 */
public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {

    /**
     * Creates an empty stream with the default initial capacity.
     */
    public ByteArrayOutputStream() {
        super();
    }

    /**
     * Creates an empty stream with the specified initial capacity.
     *
     * @param size the initial backing-array size
     */
    public ByteArrayOutputStream(int size) {
        super(size);
    }

    /**
     * Replaces this stream's backing array and logical byte count.
     *
     * @param bytes the replacement backing array, retained without copying
     * @param count the number of bytes in {@code bytes} considered written
     *
     * @return this stream
     */
    public ByteArrayOutputStream assignBytes(byte[] bytes, int count) {
        buf = bytes;
        this.count = count;
        return this;
    }

    /**
     * Replaces this stream's backing array and marks all of it as written.
     *
     * @param bytes the replacement backing array, retained without copying
     *
     * @return this stream
     */
    public ByteArrayOutputStream assignBytes(byte[] bytes) {
        buf = bytes;
        this.count = bytes.length;
        return this;
    }
}
