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

import java.io.IOException;
import java.io.OutputStream;

/**
 * An <code>OutputStream</code> that counts the written bytes.
 * You should not use same instance of this class in different threads as far as it's not thread safe.
 */
public class CountOutputStream extends OutputStream {

    private final OutputStream outputStream;
    private long amountOfWrittenBytes = 0;

    /**
     * Creates an instance of output stream which counts written bytes.
     *
     * @param outputStream inner {@link OutputStream}
     */
    public CountOutputStream(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        amountOfWrittenBytes += b.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        amountOfWrittenBytes += len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        ++amountOfWrittenBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    /**
     * Gets amount of bytes written to the inner output stream.
     *
     * @return amount of bytes
     */
    public long getAmountOfWrittenBytes() {
        return amountOfWrittenBytes;
    }
}
