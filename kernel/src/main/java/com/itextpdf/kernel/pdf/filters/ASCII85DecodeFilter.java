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

import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.MemoryLimitsAwareFilter;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

import java.io.ByteArrayOutputStream;


/**
 * Handles ASCII85Decode filter
 */
public class ASCII85DecodeFilter extends MemoryLimitsAwareFilter {

    /**
     * Decodes the input bytes according to ASCII85.
     *
     * @param in the byte[] to be decoded
     * @return the decoded byte[]
     */
    public static byte[] ASCII85Decode(byte[] in) {
        return ASCII85DecodeInternal(in, new ByteArrayOutputStream());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        ByteArrayOutputStream outputStream = enableMemoryLimitsAwareHandler(streamDictionary);
        b = ASCII85DecodeInternal(b, outputStream);
        return b;
    }


    /**
     * Decodes the input bytes according to ASCII85.
     *
     * @param in  the byte[] to be decoded
     * @param out the out stream which will be used to write the bytes.
     * @return the decoded byte[]
     */
    private static byte[] ASCII85DecodeInternal(byte[] in, ByteArrayOutputStream out) {
        int state = 0;
        int[] chn = new int[5];
        for (int k = 0; k < in.length; ++k) {
            int ch = in[k] & 0xff;
            if (ch == '~') {
                break;
            }
            if (PdfTokenizer.isWhitespace(ch)) {
                continue;
            }
            if (ch == 'z' && state == 0) {
                out.write(0);
                out.write(0);
                out.write(0);
                out.write(0);
                continue;
            }
            if (ch < '!' || ch > 'u') {
                throw new PdfException(KernelExceptionMessageConstant.ILLEGAL_CHARACTER_IN_ASCII85DECODE);
            }
            chn[state] = ch - '!';
            ++state;
            if (state == 5) {
                state = 0;
                int r = 0;
                for (int j = 0; j < 5; ++j) {
                    r = r * 85 + chn[j];
                }
                out.write((byte) (r >> 24));
                out.write((byte) (r >> 16));
                out.write((byte) (r >> 8));
                out.write((byte) r);
            }
        }
        if (state == 2) {
            int r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + 85 * 85 * 85 + 85 * 85 + 85;
            out.write((byte) (r >> 24));
        } else if (state == 3) {
            int r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85 + 85 * 85 + 85;
            out.write((byte) (r >> 24));
            out.write((byte) (r >> 16));
        } else if (state == 4) {
            int r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85 + chn[3] * 85 + 85;
            out.write((byte) (r >> 24));
            out.write((byte) (r >> 16));
            out.write((byte) (r >> 8));
        }
        return out.toByteArray();
    }
}
