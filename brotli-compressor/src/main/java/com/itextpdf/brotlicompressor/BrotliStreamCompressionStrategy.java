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
package com.itextpdf.brotlicompressor;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.IStreamCompressionStrategy;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.encoder.Encoder;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of {@link IStreamCompressionStrategy} that uses Brotli compression algorithm.
 * <p>
 * Brotli is a modern compression algorithm that typically provides better compression ratios
 * than traditional Flate/Deflate compression, especially for text-heavy content. This strategy
 * can be registered with a PDF document to use Brotli compression for all stream objects.
 * <p>
 * The compression level from iText (0-9) is automatically mapped to Brotli's compression
 * levels (0-11) for compatibility with existing iText compression settings.
 * <p>
 * Example usage:
 * <pre>
 * PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
 * pdfDoc.getDiContainer().register(IStreamCompressionStrategy.class,
 *                                   new BrotliStreamCompressionStrategy());
 * </pre>
 *
 * @see IStreamCompressionStrategy
 * @see com.aayushatharva.brotli4j.encoder.BrotliOutputStream
 */
public class BrotliStreamCompressionStrategy implements IStreamCompressionStrategy {

    /**
     * Default Brotli compression level used when the input level is out of range.
     */
    private static final int DEFAULT_COMPRESSION_LEVEL = 6;

    /**
     * Maximum Brotli compression level.
     */
    private static final int MAX_BROTLI_LEVEL = 11;

    static {
        Brotli4jLoader.ensureAvailability();
    }

    /**
     * Constructs a new {@link BrotliStreamCompressionStrategy} instance.
     */
    public BrotliStreamCompressionStrategy() {
        // empty constructor
    }

    /**
     * Returns the PDF filter name for Brotli compression.
     * <p>
     * The filter name /BrotliDecode is used in the PDF stream dictionary to indicate
     * that the stream is compressed using Brotli compression.
     *
     * @return {@link PdfName#BrotliDecode} representing the Brotli filter
     */
    @Override
    public PdfName getFilterName() {
        return PdfName.BrotliDecode;
    }

    /**
     * Returns the decode parameters for Brotli decompression.
     * <p>
     * Brotli compression does not require additional decode parameters,
     * so this method returns {@code null}.
     *
     * @return {@code null} as no decode parameters are needed for Brotli
     */
    @Override
    public PdfObject getDecodeParams() {
        return null;
    }

    /**
     * Creates a new Brotli output stream that wraps the original stream.
     * <p>
     * This method creates a {@link com.aayushatharva.brotli4j.encoder.BrotliOutputStream} configured with the compression
     * level specified in the PDF stream. The compression level is automatically converted
     * from iText's 0-9 scale to Brotli's 0-11 scale.
     *
     * @param original the original output stream to wrap
     * @param stream   the PDF stream being compressed (used to get compression level)
     *
     * @return a new {@link com.aayushatharva.brotli4j.encoder.BrotliOutputStream} that compresses data before writing to the original stream
     *
     * @throws PdfException if an I/O error occurs while creating the Brotli output stream
     */
    @Override
    public OutputStream createNewOutputStream(OutputStream original, PdfStream stream) {
        int compressionLevel = convertCompressionLevel(stream.getCompressionLevel());
        Encoder.Parameters params = Encoder.Parameters.create(compressionLevel);
        try {
            return new BrotliOutputStream(original, params);
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_WRITE_TO_PDF_STREAM, e);
        }
    }

    /**
     * Converts iText compression levels (0-9) to Brotli compression levels (0-11).
     * <p>
     * This method maps the compression level from iText's standard 0-9 range to
     * Brotli's 0-11 range using linear interpolation. If the input level is out of
     * range, it is set to the default level of 6.
     * <p>
     * Mapping formula: {@code brotliLevel = round(iTextLevel * (11.0 / 9.0))}
     *
     * @param compressionLevel the iText compression level (0-9)
     *
     * @return the corresponding Brotli compression level (0-11)
     */
    protected int convertCompressionLevel(int compressionLevel) {
        // If level is not in range 0-9, set to default 6
        int level = compressionLevel;
        if (level < CompressionConstants.NO_COMPRESSION || level > CompressionConstants.BEST_COMPRESSION) {
            level = DEFAULT_COMPRESSION_LEVEL;
        }
        // Map iText compression levels (0-9) to Brotli levels (0-11)
        if (level < CompressionConstants.BEST_SPEED) {
            level = 1;
        }
        return (int) Math.round(level * ((double) MAX_BROTLI_LEVEL / CompressionConstants.BEST_COMPRESSION));
    }
}
