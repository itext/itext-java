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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;

import java.io.OutputStream;

/**
 * A compression strategy that uses the Flate (DEFLATE) compression algorithm for PDF streams.
 * <p>
 * This strategy implements the {@link IStreamCompressionStrategy} interface and provides
 * Flate compression.
 */
public class FlateCompressionStrategy implements IStreamCompressionStrategy {

    // 32KB buffer size
    private static final int BUFFER = 32 * 1024;

    /**
     * Constructs a new {@link FlateCompressionStrategy} instance.
     */
    public FlateCompressionStrategy() {
        // empty constructor
    }

    /**
     * Returns the name of the compression filter.
     *
     * @return {@link PdfName#FlateDecode} representing the Flate compression filter
     */
    @Override
    public PdfName getFilterName() {
        return PdfName.FlateDecode;
    }

    /**
     * Returns the decode parameters for the Flate filter.
     * <p>
     * This implementation returns {@code null} as no special decode parameters
     * are required for standard Flate compression.
     *
     * @return {@code null} as no decode parameters are needed
     */
    @Override
    public PdfObject getDecodeParams() {
        return null;
    }

    /**
     * Creates a new output stream with Flate compression applied.
     * <p>
     * This method wraps the original output stream in a {@link DeflaterOutputStream}
     * that applies Flate compression using the compression level specified in the
     * PDF stream and a 32KB buffer for optimal performance.
     *
     * @param original the original output stream to wrap
     * @param stream   the PDF stream containing compression configuration
     *
     * @return a new {@link DeflaterOutputStream} that compresses data using the Flate algorithm
     */
    @Override
    public OutputStream createNewOutputStream(OutputStream original, PdfStream stream) {
        // Use 32KB buffer size for deflater stream
        return new DeflaterOutputStream(original, stream.getCompressionLevel(), BUFFER);
    }

    /**
     * Finishes writing compressed data to the output stream and flushes any remaining data.
     * <p>
     * This method must be called after all data has been written to ensure that all compressed
     * data is properly flushed to the underlying stream. The output stream must be an instance
     * of {@link DeflaterOutputStream}, otherwise a {@link PdfException} will be thrown.
     *
     * @param outputStream the output stream to finish, must be a {@link DeflaterOutputStream}
     *
     * @throws PdfException if the output stream is not a {@link DeflaterOutputStream} or if
     *                      an error occurs during the finish operation
     */
    @Override
    public void finish(OutputStream outputStream) {
        if (outputStream instanceof DeflaterOutputStream) {
            try {
                ((DeflaterOutputStream) outputStream).finish();
            } catch (Exception e) {
                throw new PdfException(KernelExceptionMessageConstant.CANNOT_WRITE_TO_PDF_STREAM, e);
            }
        } else {
            throw new PdfException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.OUTPUTSTREAM_IS_NOT_OF_INSTANCE,
                            "DeflaterOutputStream"));
        }
    }
}
