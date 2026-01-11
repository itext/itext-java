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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.RunLengthOutputStream;

import java.io.OutputStream;

/**
 * A compression strategy that uses the {@code RunLengthDecode} filter for PDF
 * streams.
 *
 * <p>
 * This strategy implements the {@link IStreamCompressionStrategy} interface
 * and provides {@code RunLengthDecode} encoding.
 *
 * <p>
 * Run-length encoding works best, when input data has long sequences of
 * identical bytes. This is, usually, not the case for PDF content streams, so
 * using this strategy will often cause a marginal size increase instead.
 */
public class RunLengthCompressionStrategy implements IStreamCompressionStrategy {
    /**
     * Constructs a new {@link RunLengthCompressionStrategy} instance.
     */
    public RunLengthCompressionStrategy() {
        // empty constructor
    }

    /**
     * Returns the name of the compression filter.
     *
     * @return {@link PdfName#RunLengthDecode} representing the {@code RunLengthDecode} filter
     */
    @Override
    public PdfName getFilterName() {
        return PdfName.RunLengthDecode;
    }

    /**
     * Returns the decode parameters for the {@code RunLengthDecode} filter.
     * <p>
     * This implementation returns {@code null} as no special decode parameters
     * are required for standard run length compression.
     *
     * @return {@code null} as no decode parameters are needed
     */
    @Override
    public PdfObject getDecodeParams() {
        return null;
    }

    /**
     * Creates a new output stream with run length compression applied.
     * <p>
     * This method wraps the original output stream in a {@link RunLengthOutputStream}
     * that applies run length compression.
     *
     * @param original the original output stream to wrap
     * @param stream   the PDF stream containing compression configuration
     *
     * @return a new {@link RunLengthOutputStream} that compresses data using the run length algorithm
     */
    @Override
    public OutputStream createNewOutputStream(OutputStream original, PdfStream stream) {
        return new RunLengthOutputStream(original);
    }
}
