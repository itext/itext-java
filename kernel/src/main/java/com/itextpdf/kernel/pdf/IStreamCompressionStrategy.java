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

import java.io.OutputStream;

/**
 * Strategy interface for PDF stream compression implementations.
 * <p>
 * This interface defines the contract for compression strategies that can be applied to PDF streams.
 * Different compression algorithms can be
 * implemented by providing concrete implementations of this interface.
 *
 */
public interface IStreamCompressionStrategy {

    /**
     * Gets the PDF filter name that identifies this compression algorithm.
     *
     * @return the PDF name representing the compression filter
     */
    PdfName getFilterName();

    /**
     * Gets the decode parameters required for decompressing the stream.
     * <p>
     * Decode parameters provide additional information needed to correctly
     * decompress the stream data. This may include predictor settings,
     * color information, or other algorithm-specific parameters.
     * The returned object is typically a {@link PdfDictionary} or {@link PdfArray},
     * or {@code null} if no special parameters are required.
     *
     * @return the decode parameters as a PDF object, or {@code null} if not needed
     */
    PdfObject getDecodeParams();

    /**
     * Creates a new output stream that wraps the original stream and applies compression.
     * <p>
     * This method wraps the provided output stream with a compression implementation.
     * Data written to the returned stream will be compressed before being written
     * to the original stream.
     *
     * @param original the original output stream to wrap
     * @param stream   the PDF stream being compressed (may be used for context or configuration)
     *
     * @return a new output stream that performs compression
     */
    OutputStream createNewOutputStream(OutputStream original, PdfStream stream);

    /**
     * Finalizes the compression process.  The underlying compression stream should still be open when this method is
     * called.
     * <p>
     * This method should be called when compression is complete to ensure that
     * all data is properly flushed.
     *
     * @param outputStream the output stream to finalize
     */
    void finish(OutputStream outputStream);
}
