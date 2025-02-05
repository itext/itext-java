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


public class GetBufferedRandomAccessSource implements IRandomAccessSource {

    private final IRandomAccessSource source;

    private final byte[] getBuffer;
    private long getBufferStart = -1;
    private long getBufferEnd = -1;

    /**
     * Constructs a new OffsetRandomAccessSource
     * @param source the source
     */
    public GetBufferedRandomAccessSource(IRandomAccessSource source) {
        this.source = source;
        this.getBuffer = new byte[(int)Math.min(Math.max(source.length()/4, 1), 4096)];
        this.getBufferStart = -1;
        this.getBufferEnd = -1;
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position) throws java.io.IOException {
        if (position < getBufferStart || position > getBufferEnd){
            int count = source.get(position, getBuffer, 0, getBuffer.length);
            if (count == -1)
                return -1;
            getBufferStart = position;
            getBufferEnd = position + count - 1;
        }
        int bufPos = (int)(position-getBufferStart);
        return 0xff & getBuffer[bufPos];
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws java.io.IOException {
        return source.get(position, bytes, off, len);
    }

    /**
     * {@inheritDoc}
     */
    public long length() {
        return source.length();
    }

    /**
     * Does nothing - the underlying source is not closed
     */
    public void close() throws java.io.IOException {
        source.close();
        getBufferStart = -1;
        getBufferEnd = -1;
    }
}
