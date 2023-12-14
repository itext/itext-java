/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.io.font.woff2;

public class FontCompressionException extends RuntimeException {
    public static final String BUFFER_READ_FAILED = "Reading woff2 exception";
    public static final String READ_BASE_128_FAILED = "Reading woff2 base 128 number exception";
    public static final String READ_TABLE_DIRECTORY_FAILED = "Reading woff2 tables directory exception";
    public static final String INCORRECT_SIGNATURE = "Incorrect woff2 signature";
    public static final String RECONSTRUCT_GLYPH_FAILED = "Reconstructing woff2 glyph exception";
    public static final String RECONSTRUCT_POINT_FAILED = "Reconstructing woff2 glyph's point exception";
    public static final String PADDING_OVERFLOW = "woff2 padding overflow exception";
    public static final String LOCA_SIZE_OVERFLOW = "woff2 loca table content size overflow exception";
    public static final String RECONSTRUCT_GLYF_TABLE_FAILED = "Reconstructing woff2 glyf table exception";
    public static final String RECONSTRUCT_HMTX_TABLE_FAILED = "Reconstructing woff2 hmtx table exception";
    public static final String BROTLI_DECODING_FAILED = "Woff2 brotli decoding exception";
    public static final String RECONSTRUCT_TABLE_DIRECTORY_FAILED = "Reconstructing woff2 table directory exception";
    public static final String READ_HEADER_FAILED = "Reading woff2 header exception";
    public static final String READ_COLLECTION_HEADER_FAILED = "Reading collection woff2 header exception";
    public static final String WRITE_FAILED = "Writing woff2 exception";

    public FontCompressionException() {
    }

    public FontCompressionException(String message) {
        super(message);
    }

    public FontCompressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
