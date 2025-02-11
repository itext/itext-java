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

import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.MemoryLimitsAwareFilter;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

import java.io.ByteArrayOutputStream;

/**
 * Handles ASCIIHexDecode filter
 */
public class ASCIIHexDecodeFilter extends MemoryLimitsAwareFilter {

    /**
     * Decodes a byte[] according to ASCII Hex encoding.
     *
     * @param in byte[] to be decoded
     * @return decoded byte[]
     */
    public static byte[] ASCIIHexDecode(byte[] in) {
        return ASCIIHexDecodeInternal(in, new ByteArrayOutputStream());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        ByteArrayOutputStream outputStream = enableMemoryLimitsAwareHandler(streamDictionary);
        b = ASCIIHexDecodeInternal(b, outputStream);
        return b;
    }

    /**
     * Decodes a byte[] according to ASCII Hex encoding.
     *
     * @param in  byte[] to be decoded
     * @param out the out stream which will be used to write the bytes.
     * @return decoded byte[]
     */
    private static byte[] ASCIIHexDecodeInternal(byte[] in, ByteArrayOutputStream out) {
        boolean first = true;
        int n1 = 0;
        for (int k = 0; k < in.length; ++k) {
            int ch = in[k] & 0xff;
            if (ch == '>') {
                break;
            }
            if (PdfTokenizer.isWhitespace(ch)) {
                continue;
            }
            int n = ByteBuffer.getHex(ch);
            if (n == -1) {
                throw new PdfException(KernelExceptionMessageConstant.ILLEGAL_CHARACTER_IN_ASCIIHEXDECODE);
            }
            if (first) {
                n1 = n;
            } else {
                out.write((byte) ((n1 << 4) + n));
            }
            first = !first;
        }
        if (!first) {
            out.write((byte) (n1 << 4));
        }
        return out.toByteArray();
    }
}
