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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TIFFFaxDecoder;
import com.itextpdf.io.codec.TIFFFaxDecompressor;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

/**
 * Handles CCITTFaxDecode filter
 */
public class CCITTFaxDecodeFilter implements IFilterHandler {

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        PdfNumber wn = streamDictionary.getAsNumber(PdfName.Width);
        PdfNumber hn = streamDictionary.getAsNumber(PdfName.Height);
        if (wn == null || hn == null) {
            throw new PdfException(KernelExceptionMessageConstant.FILTER_CCITTFAXDECODE_IS_ONLY_SUPPORTED_FOR_IMAGES);
        }
        int width = wn.intValue();
        int height = hn.intValue();

        PdfDictionary param = decodeParams instanceof PdfDictionary ? (PdfDictionary) decodeParams : null;
        int k = 0;
        boolean blackIs1 = false;
        boolean byteAlign = false;
        if (param != null) {
            PdfNumber kn = param.getAsNumber(PdfName.K);
            if (kn != null) {
                k = kn.intValue();
            }
            PdfBoolean bo = param.getAsBoolean(PdfName.BlackIs1);
            if (bo != null) {
                blackIs1 = bo.getValue();
            }
            bo = param.getAsBoolean(PdfName.EncodedByteAlign);
            if (bo != null) {
                byteAlign = bo.getValue();
            }
        }
        byte[] outBuf = new byte[(width + 7) / 8 * height];
        TIFFFaxDecompressor decoder = new TIFFFaxDecompressor();
        if (k == 0 || k > 0) {
            int tiffT4Options = k > 0 ? TIFFConstants.GROUP3OPT_2DENCODING : 0;
            tiffT4Options |= byteAlign ? TIFFConstants.GROUP3OPT_FILLBITS : 0;
            decoder.SetOptions(1, TIFFConstants.COMPRESSION_CCITTFAX3, tiffT4Options, 0);
            decoder.decodeRaw(outBuf, b, width, height);
            if (decoder.fails > 0) {
                byte[] outBuf2 = new byte[(width + 7) / 8 * height];
                int oldFails = decoder.fails;
                decoder.SetOptions(1, TIFFConstants.COMPRESSION_CCITTRLE, tiffT4Options, 0);
                decoder.decodeRaw(outBuf2, b, width, height);
                if (decoder.fails < oldFails) {
                    outBuf = outBuf2;
                }
            }
        } else {
            long tiffT6Options = 0;
            tiffT6Options |= byteAlign ? TIFFConstants.GROUP4OPT_FILLBITS : 0;
            TIFFFaxDecoder deca = new TIFFFaxDecoder(1, width, height);
            deca.decodeT6(outBuf, b, 0, height, tiffT6Options);
        }
        if (!blackIs1) {
            int len = outBuf.length;
            for (int t = 0; t < len; ++t) {
                outBuf[t] ^= 0xff;
            }
        }
        b = outBuf;
        return b;
    }
}
