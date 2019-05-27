/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TIFFFaxDecoder;
import com.itextpdf.io.codec.TIFFFaxDecompressor;
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
        if (wn == null || hn == null)
            throw new PdfException(PdfException.FilterCcittfaxdecodeIsOnlySupportedForImages);
        int width = wn.intValue();
        int height = hn.intValue();

        PdfDictionary param = decodeParams instanceof PdfDictionary ? (PdfDictionary)decodeParams : null;
        int k = 0;
        boolean blackIs1 = false;
        boolean byteAlign = false;
        if (param != null) {
            PdfNumber kn = param.getAsNumber(PdfName.K);
            if (kn != null)
                k = kn.intValue();
            PdfBoolean bo = param.getAsBoolean(PdfName.BlackIs1);
            if (bo != null)
                blackIs1 = bo.getValue();
            bo = param.getAsBoolean(PdfName.EncodedByteAlign);
            if (bo != null)
                byteAlign = bo.getValue();
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
        }
        else {
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
