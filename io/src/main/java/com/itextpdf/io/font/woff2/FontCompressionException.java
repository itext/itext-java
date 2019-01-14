/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
