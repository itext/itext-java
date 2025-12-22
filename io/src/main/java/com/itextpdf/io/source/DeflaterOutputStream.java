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

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

/**
 * An output stream that compresses data using the DEFLATE compression algorithm.
 * This is a wrapper around {@link java.util.zip.DeflaterOutputStream} that provides
 * convenient constructors for specifying compression level and buffer size.
 * <p>
 * The compression level can be set to values between 0 (no compression) and 9 (maximum compression),
 * or use -1 for the default compression level.
 */
public class DeflaterOutputStream extends java.util.zip.DeflaterOutputStream implements IFinishable {

    /**
     * Default buffer size for the deflater output stream (512 bytes).
     */
    private static final int DEFAULT_BUFFER_SIZE = 512;

    /**
     * Default compression level (uses Deflater's default).
     */
    private static final int DEFAULT_COMPRESSION_LEVEL = -1;

    /**
     * Creates a new deflater output stream with a specified compression level and buffer size.
     *
     * @param out the output stream to write compressed data to
     * @param level the compression level (0-9, or -1 for default)
     * @param size the buffer size in bytes
     */
    public DeflaterOutputStream(OutputStream out, int level, int size) {
        super(out, new Deflater(level), size);
    }

    /**
     * Creates a new deflater output stream with a specified compression level and default buffer size.
     *
     * @param out the output stream to write compressed data to
     * @param level the compression level (0-9, or -1 for default)
     */
    public DeflaterOutputStream(OutputStream out, int level) {
        this(out, level, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a new deflater output stream with default compression level and buffer size.
     *
     * @param out the output stream to write compressed data to
     */
    public DeflaterOutputStream(OutputStream out) {
        this(out, DEFAULT_COMPRESSION_LEVEL);
    }

    /**
     * Closes this output stream and releases any system resources associated with it.
     * This method finishes writing compressed data to the output stream before closing.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        finish();
        super.close();
    }

    /**
     * Finishes writing compressed data to the output stream without closing the underlying stream.
     * This method completes the compression process and ends the deflater, releasing its resources.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void finish() throws IOException {
        super.finish();
        def.end();
    }
}
