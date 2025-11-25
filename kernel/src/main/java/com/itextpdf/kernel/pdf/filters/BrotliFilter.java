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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.io.codec.brotli.dec.BrotliInputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.MemoryLimitsAwareFilter;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Filter implementation for decoding Brotli-compressed PDF streams.
 * This filter supports optional Brotli dictionary streams and memory limits awareness.
 */
public class BrotliFilter extends MemoryLimitsAwareFilter {
    private static final int DEFAULT_INTERNAL_BUFFER_SIZE = 16384;
    /**
     * Default buffer size for Brotli decompression (64 KiB).
     */
    private static final int DEFAULT_BUFFER_SIZE = 65536;

    /**
     * Constructs an empty BrotliFilter instance.
     */
    public BrotliFilter() {
        //empty constructor
    }

    /**
     * Decodes Brotli-compressed data from a PDF stream.
     *
     * @param b                the bytes that need to be decoded
     * @param filterName       PdfName of the filter (unused)
     * @param decodeParams     decode parameters, may contain a Brotli dictionary stream under key 'D'
     * @param streamDictionary the dictionary of the stream. Can contain additional information needed to decode the
     *                         byte[]
     *
     * @return the decoded byte[]
     *
     * @throws PdfException if decompression fails
     */
    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        try {
            final PdfStream brotliDictionary = getBrotliDictionaryStream(decodeParams);
            final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            final ByteArrayInputStream input = new ByteArrayInputStream(b);
            final ByteArrayOutputStream output = enableMemoryLimitsAwareHandler(streamDictionary);
            final InputStream brotliInput =
                    brotliDictionary != null ? new BrotliInputStream(input, DEFAULT_INTERNAL_BUFFER_SIZE,
                            brotliDictionary.getBytes()) : new BrotliInputStream(input);

            int len;
            while ((len = brotliInput.read(buffer, 0, buffer.length)) > 0) {
                output.write(buffer, 0, len);
            }
            brotliInput.close();
            return FlateDecodeFilter.decodePredictor(output.toByteArray(), decodeParams);
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.FAILED_TO_DECODE_BROTLI_STREAM, e);
        }
    }


    /**
     * Extracts the Brotli dictionary stream from decode parameters if present.
     *
     * @param decodeParams decode parameters, may contain a Brotli dictionary stream under key 'D'
     *
     * @return an Optional containing the Brotli dictionary stream if present, otherwise empty
     *
     * @throws RuntimeException if the dictionary is present but not a stream
     */
    private static PdfStream getBrotliDictionaryStream(PdfObject decodeParams) {
        if (!(decodeParams instanceof PdfDictionary)) {
            return null;
        }
        PdfDictionary dict = (PdfDictionary) decodeParams;
        PdfObject brotliDecompressionDictionary = dict.get(PdfName.D);
        if (brotliDecompressionDictionary instanceof PdfStream) {
            // Brotli dictionary stream found
            return (PdfStream) brotliDecompressionDictionary;
        } else if (brotliDecompressionDictionary != null) {
            throw new PdfException(KernelExceptionMessageConstant.BROTLI_DICTIONARY_IS_NOT_A_STREAM);
        } else {
            return null;
        }
    }
}
