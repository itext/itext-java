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

import com.itextpdf.io.source.IFinishable;

import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that compresses data using the Brotli compression algorithm.
 * This is a wrapper around {@link com.aayushatharva.brotli4j.encoder.BrotliOutputStream} that implements the
 * {@link IFinishable}
 * interface, allowing it to be used in contexts where finalization without closing the
 * underlying stream is required.
 * <p>
 * Brotli is a generic-purpose lossless compression algorithm that compresses data using a
 * combination of a modern variant of the LZ77 algorithm, Huffman coding, and 2nd order
 * context modeling. It provides compression ratios comparable to the best currently available
 * general-purpose compression methods, while offering significantly faster decompression speeds.
 * <p>
 * This class provides three constructors with varying levels of control over the compression
 * parameters and buffer size. If no parameters are specified, default Brotli compression
 * settings will be used.
 *
 * @see IFinishable
 * @see com.aayushatharva.brotli4j.encoder.BrotliOutputStream
 */
public class BrotliOutputStream extends com.aayushatharva.brotli4j.encoder.BrotliOutputStream implements IFinishable {

    /**
     * Creates a new Brotli output stream with specified compression parameters and buffer size.
     * <p>
     * This constructor provides full control over the compression behavior by allowing
     * specification of both the compression parameters and the internal buffer size.
     *
     * @param destination the output stream to write compressed data to
     * @param params      the Brotli compression parameters to use
     * @param bufferSize  the buffer size in bytes for the internal compression buffer
     *
     * @throws IOException if an I/O error occurs during initialization
     */
    public BrotliOutputStream(OutputStream destination,
            Parameters params, int bufferSize) throws IOException {
        super(destination, params, bufferSize);
    }

    /**
     * Creates a new Brotli output stream with specified compression parameters and default buffer size.
     * <p>
     * This constructor allows customization of the compression parameters while using
     * the default buffer size provided by the underlying {@link com.aayushatharva.brotli4j.encoder.BrotliOutputStream}.
     *
     * @param destination the output stream to write compressed data to
     * @param params      the Brotli compression parameters to use
     *
     * @throws IOException if an I/O error occurs during initialization
     */
    public BrotliOutputStream(OutputStream destination, Parameters params) throws IOException {
        super(destination, params);
    }

    /**
     * Creates a new Brotli output stream with default compression parameters and buffer size.
     * <p>
     * This constructor uses default Brotli compression settings, which provide a good
     * balance between compression ratio and speed for most use cases.
     *
     * @param destination the output stream to write compressed data to
     *
     * @throws IOException if an I/O error occurs during initialization
     */
    public BrotliOutputStream(OutputStream destination) throws IOException {
        super(destination);
    }

    /**
     * Finishes writing compressed data to the output stream without closing the underlying stream.
     * <p>
     * This method completes the compression process, finalizes the Brotli data format, and
     * destroys the encoder to release its resources. Unlike the standard {@link #close()} method,
     * this method does not close the underlying output stream, allowing additional data to be
     * written to it after compression is complete.
     * <p>
     * Note: The parent {@link com.aayushatharva.brotli4j.encoder.BrotliOutputStream} class does not close the
     * underlying output
     * stream in its {@code close()} method, unlike {@link java.util.zip.DeflaterOutputStream}.
     * Therefore, calling {@code super.close()} effectively finishes the compression without
     * closing the destination stream.
     *
     * @throws IOException if an I/O error occurs during finalization
     */
    @Override
    public void finish() throws IOException {
        super.close();
    }
}
