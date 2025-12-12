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

import com.itextpdf.io.source.ASCII85OutputStream;

import java.io.OutputStream;

/**
 * A compression strategy that uses the {@code ASCII85Decode} filter for PDF
 * streams.
 *
 * <p>
 * This strategy implements the {@link IStreamCompressionStrategy} interface
 * and provides {@code ASCII85Decode} encoding.
 *
 * <p>
 * The strategy ensures, that streams are saved using just 7-bit ASCII
 * characters, but it typically increases sizes of streams by 25% compared to
 * just saving them as-is. So calling this a "compression strategy" is a
 * misnomer.
 */
public class ASCII85CompressionStrategy implements IStreamCompressionStrategy {
    /**
     * Constructs a new {@link ASCII85CompressionStrategy} instance.
     */
    public ASCII85CompressionStrategy() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfName getFilterName() {
        return PdfName.ASCII85Decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfObject getDecodeParams() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream createNewOutputStream(OutputStream original, PdfStream stream) {
        return new ASCII85OutputStream(original);
    }
}
