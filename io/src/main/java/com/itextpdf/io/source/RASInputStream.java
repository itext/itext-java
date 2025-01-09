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

import java.io.InputStream;

/**
 * An input stream that uses a {@link IRandomAccessSource} as
 * its underlying source.
 */
public class RASInputStream extends InputStream {

    /**
     * The source.
     */
    private final IRandomAccessSource source;

    /**
     * The current position in the source.
     */
    private long position = 0;

    /**
     * Creates an input stream based on the source.
     * @param source The source.
     */
    public RASInputStream(IRandomAccessSource source){
        this.source = source;
    }

    /**
     * Gets the source
     *
     * @return an instance of {@link IRandomAccessSource}
     */
    public IRandomAccessSource getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws java.io.IOException {
        int count = source.get(position, b, off, len);
        position += count;
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws java.io.IOException {
        return source.get(position++);
    }
}
